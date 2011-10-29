/*
 * Copyright (c) 2009-2011 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 * The Eclipse Public License is available at
 *   http://www.eclipse.org/legal/epl-v10.html
 * The Apache License v2.0 is available at
 *   http://www.apache.org/licenses/LICENSE-2.0.html
 * You may elect to redistribute this code under either of these licenses.
 */

package org.sonatype.sisu.maven.bridge.support.dependency.internal;

import static org.sonatype.sisu.maven.bridge.support.RemoteRepositoryBuilder.remoteRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.inject.Provider;

import org.apache.maven.model.Model;
import org.apache.maven.model.Repository;
import org.apache.maven.model.building.ModelBuildingException;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.artifact.ArtifactType;
import org.sonatype.aether.artifact.ArtifactTypeRegistry;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.collection.CollectResult;
import org.sonatype.aether.collection.DependencyCollectionException;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.aether.graph.Exclusion;
import org.sonatype.aether.impl.RemoteRepositoryManager;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.spi.locator.ServiceLocator;
import org.sonatype.aether.util.artifact.ArtifactProperties;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.sonatype.aether.util.artifact.DefaultArtifactType;
import org.sonatype.inject.Nullable;
import org.sonatype.sisu.maven.bridge.MavenDependencyTreeResolver;
import org.sonatype.sisu.maven.bridge.MavenModelResolver;
import org.sonatype.sisu.maven.bridge.internal.ComponentSupport;
import org.sonatype.sisu.maven.bridge.support.CollectRequestBuilder;

public abstract class MavenDependencyTreeResolverSupport
    extends ComponentSupport
    implements MavenDependencyTreeResolver
{

    protected static final Provider<RepositorySystemSession> NO_SESSION_PROVIDER = null;

    private MavenModelResolver mavenModelResolver;

    private Provider<RepositorySystemSession> sessionProvider;

    private RepositorySystem repositorySystem;

    private RemoteRepositoryManager remoteRepositoryManager;

    protected MavenDependencyTreeResolverSupport( final ServiceLocator serviceLocator,
                                                  final @Nullable MavenModelResolver mavenModelResolver )
    {
        this( serviceLocator, mavenModelResolver, NO_SESSION_PROVIDER );
    }

    protected MavenDependencyTreeResolverSupport( final ServiceLocator serviceLocator,
                                                  final @Nullable MavenModelResolver mavenModelResolver,
                                                  final @Nullable Provider<RepositorySystemSession> sessionProvider )
    {
        repositorySystem = serviceLocator.getService( RepositorySystem.class );
        remoteRepositoryManager = serviceLocator.getService( RemoteRepositoryManager.class );
        this.sessionProvider = sessionProvider;
        this.mavenModelResolver = mavenModelResolver;
    }

    @Override
    public DependencyNode resolveDependencyTree( final CollectRequest request,
                                                 final RepositorySystemSession session,
                                                 final RemoteRepository... repositories )
        throws DependencyCollectionException
    {

        if ( request instanceof CollectRequestBuilder )
        {
            final CollectRequestBuilder requestBuilder = (CollectRequestBuilder) request;
            final ModelBuildingRequest modelBuildingRequest = requestBuilder.getModelBuildingRequest();
            Model model = requestBuilder.getModel();
            if ( model == null && modelBuildingRequest != null )
            {
                model = resolveModel( request, session, modelBuildingRequest );
            }
            if ( model != null )
            {
                injectCollectionRequest( request, session, model );
            }
        }

        return repositorySystem.collectDependencies( session, request ).getRoot();
    }

    @Override
    public DependencyNode resolveDependencyTree( final CollectRequest request,
                                                 final RemoteRepository... repositories )
        throws DependencyCollectionException
    {
        return resolveDependencyTree(
            request,
            assertNotNull( sessionProvider, "Repository system session provider not specified" ).get()
        );
    }

    protected RemoteRepositoryManager getRemoteRepositoryManager()
    {
        return remoteRepositoryManager;
    }

    protected RepositorySystem getRepositorySystem()
    {
        return repositorySystem;
    }

    private void injectCollectionRequest( final CollectRequest request,
                                          final RepositorySystemSession session,
                                          final Model model )
    {
        for ( final Repository repository : model.getRepositories() )
        {
            request.addRepository( remoteRepository( repository ) );
        }
        final ArtifactTypeRegistry stereotypes = session.getArtifactTypeRegistry();
        for ( final org.apache.maven.model.Dependency dep : model.getDependencies() )
        {
            request.addDependency( toDependency( dep, stereotypes ) );
        }
        if ( model.getDependencyManagement() != null )
        {
            for ( final org.apache.maven.model.Dependency dep : model.getDependencyManagement().getDependencies() )
            {
                request.addManagedDependency( toDependency( dep, stereotypes ) );
            }
        }
    }

    private Model resolveModel( final CollectRequest request,
                                final RepositorySystemSession session,
                                final ModelBuildingRequest modelBuildingRequest )
        throws DependencyCollectionException
    {
        final Model model;
        try
        {
            model = assertNotNull( mavenModelResolver, "Maven model resolver is not set" )
                .resolveModel( modelBuildingRequest, session );
        }
        catch ( ModelBuildingException e )
        {
            final CollectResult collectResult = new CollectResult( request );
            collectResult.addException( e );
            throw new DependencyCollectionException( collectResult );
        }
        return model;
    }

    private Dependency toDependency( final org.apache.maven.model.Dependency dependency,
                                     final ArtifactTypeRegistry stereotypes )
    {
        ArtifactType stereotype = stereotypes.get( dependency.getType() );
        if ( stereotype == null )
        {
            stereotype = new DefaultArtifactType( dependency.getType() );
        }

        final boolean system = dependency.getSystemPath() != null && dependency.getSystemPath().length() > 0;

        Map<String, String> props = null;
        if ( system )
        {
            props = Collections.singletonMap( ArtifactProperties.LOCAL_PATH, dependency.getSystemPath() );
        }

        final Artifact artifact =
            new DefaultArtifact( dependency.getGroupId(), dependency.getArtifactId(), dependency.getClassifier(), null,
                                 dependency.getVersion(), props, stereotype );

        final List<Exclusion> exclusions = new ArrayList<Exclusion>( dependency.getExclusions().size() );
        for ( final org.apache.maven.model.Exclusion exclusion : dependency.getExclusions() )
        {
            exclusions.add( toExclusion( exclusion ) );
        }

        return new Dependency( artifact, dependency.getScope(), dependency.isOptional(), exclusions );
    }

    private Exclusion toExclusion( final org.apache.maven.model.Exclusion exclusion )
    {
        return new Exclusion( exclusion.getGroupId(), exclusion.getArtifactId(), "*", "*" );
    }

}
