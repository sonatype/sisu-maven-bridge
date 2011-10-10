/*******************************************************************************
 * Copyright (c) 2010-2011 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 * The Eclipse Public License is available at
 *   http://www.eclipse.org/legal/epl-v10.html
 * The Apache License v2.0 is available at
 *   http://www.apache.org/licenses/LICENSE-2.0.html
 * You may elect to redistribute this code under either of these licenses.
 *******************************************************************************/
package org.sonatype.sisu.maven.bridge.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.maven.model.Model;
import org.apache.maven.model.Repository;
import org.apache.maven.model.building.DefaultModelBuilderFactory;
import org.apache.maven.model.building.DefaultModelBuildingRequest;
import org.apache.maven.model.building.FileModelSource;
import org.apache.maven.model.building.ModelBuilder;
import org.apache.maven.model.building.ModelBuildingException;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelBuildingResult;
import org.apache.maven.model.building.ModelSource;
import org.apache.maven.model.resolution.InvalidRepositoryException;
import org.apache.maven.model.resolution.ModelResolver;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.artifact.ArtifactType;
import org.sonatype.aether.artifact.ArtifactTypeRegistry;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.collection.DependencyCollectionException;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.aether.graph.Exclusion;
import org.sonatype.aether.impl.RemoteRepositoryManager;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.repository.RepositoryPolicy;
import org.sonatype.aether.util.artifact.ArtifactProperties;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.sonatype.aether.util.artifact.DefaultArtifactType;
import org.sonatype.sisu.maven.bridge.MavenBridge;
import org.sonatype.sisu.maven.bridge.MavenBuilder;

@Named
@Singleton
class DefaultMavenBridge
    extends ComponentSupport
    implements MavenBridge
{

    private final ModelBuilder modelBuilder;

    private final RepositorySystem repositorySystem;

    private final RepositorySystemSession repositorySession;

    private final RemoteRepositoryManager remoteRepositoryManager;

    private final ModelResolverFactory modelResolverFactory;

    @Inject
    DefaultMavenBridge( final ModelResolverFactory modelResolverFactory,
                        final RepositorySystem repositorySystem,
                        final MavenBridgeRepositorySystemSession repositorySession,
                        final RemoteRepositoryManager remoteRepositoryManager )
    {
        this.modelResolverFactory = modelResolverFactory;
        this.repositorySystem = repositorySystem;
        this.repositorySession = repositorySession;
        this.remoteRepositoryManager = remoteRepositoryManager;

        this.modelBuilder = new DefaultModelBuilderFactory().newInstance();
    }

    // ==

    public Model buildModel( final RepositorySystemSession session, final ModelSource pom,
                             final Repository... repositories )
        throws ModelBuildingException
    {
        final ModelResolver mr = modelResolverFactory.getModelResolver( session );
        if ( repositories != null )
        {
            for ( final Repository repository : repositories )
            {
                try
                {
                    mr.addRepository( repository );
                }
                catch ( final InvalidRepositoryException e )
                {
                    log().warn( String.format( "Could not use repository [%s]", repository.getUrl() ), e );
                }
            }
        }

        final ModelBuildingRequest modelRequest = new DefaultModelBuildingRequest();
        modelRequest.setModelSource( pom );
        modelRequest.setSystemProperties( System.getProperties() );
        modelRequest.setValidationLevel( ModelBuildingRequest.VALIDATION_LEVEL_MINIMAL );
        modelRequest.setLocationTracking( false );
        modelRequest.setProcessPlugins( false );
        modelRequest.setModelResolver( mr );

        final ModelBuildingResult modelResult = modelBuilder.build( modelRequest );
        final Model model = modelResult.getEffectiveModel();
        return model;
    }

    public Model buildModel( final RepositorySystemSession session, final ModelSource pom,
                             final Map<String, String> repositories )
        throws ModelBuildingException
    {
        if ( repositories == null )
        {
            return null;
        }
        final Repository[] repos = new Repository[repositories.size()];
        int i = 0;
        for ( final Map.Entry<String, String> entry : repositories.entrySet() )
        {
            final Repository repository = MavenBuilder.repository( entry.getKey(), entry.getValue() );
            repos[i++] = repository;
        }
        return buildModel( pom, repos );
    }

    public DependencyNode buildDependencyTree( final RepositorySystemSession session, final Dependency node,
                                               final Repository... repositories )
        throws DependencyCollectionException
    {
        final CollectRequest request = new CollectRequest();
        request.setRequestContext( "project" );
        final List<RemoteRepository> requestRepos = new ArrayList<RemoteRepository>();
        for ( final Repository repo : repositories )
        {
            requestRepos.add( toRemoteRepository( repo ) );
        }
        request.setRepositories( remoteRepositoryManager.aggregateRepositories( session, requestRepos,
                                                                                new ArrayList<RemoteRepository>(),
                                                                                true ) );
        request.setRoot( node );

        return repositorySystem.collectDependencies( session, request ).getRoot();
    }

    public DependencyNode buildDependencyTree( final RepositorySystemSession session, final Model model,
                                               final Repository... repositories )
        throws DependencyCollectionException
    {
        final CollectRequest request = new CollectRequest();
        request.setRequestContext( "project" );
        final List<RemoteRepository> requestRepos = new ArrayList<RemoteRepository>();
        final List<RemoteRepository> pomRepos = new ArrayList<RemoteRepository>();
        for ( final Repository repo : repositories )
        {
            requestRepos.add( toRemoteRepository( repo ) );
        }
        for ( final Repository repo : model.getRepositories() )
        {
            pomRepos.add( toRemoteRepository( repo ) );
        }
        request.setRepositories(
            remoteRepositoryManager.aggregateRepositories( session, requestRepos, pomRepos, true ) );
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

        return repositorySystem.collectDependencies( session, request ).getRoot();
    }

    // ==

    public Model buildModel( final ModelSource pom, final Repository... repositories )
        throws ModelBuildingException
    {
        return buildModel( repositorySession, pom, repositories );
    }

    public Model buildModel( final File pom, final Repository... repositories )
        throws ModelBuildingException
    {
        return buildModel( new FileModelSource( pom ), repositories );
    }

    public Model buildModel( final ModelSource pom, final Map<String, String> repositories )
        throws ModelBuildingException
    {
        return buildModel( repositorySession, pom, repositories );
    }

    public Model buildModel( final File pom, final Map<String, String> repositories )
        throws ModelBuildingException
    {
        return buildModel( new FileModelSource( pom ), repositories );
    }

    public DependencyNode buildDependencyTree( final Dependency node, final Repository... repositories )
        throws DependencyCollectionException
    {
        return buildDependencyTree( repositorySession, node, repositories );
    }

    public DependencyNode buildDependencyTree( final Model model, final Repository... repositories )
        throws DependencyCollectionException
    {
        return buildDependencyTree( repositorySession, model, repositories );
    }

    // ==

    private RemoteRepository toRemoteRepository( final Repository repository )
    {
        final RemoteRepository result =
            new RemoteRepository( repository.getId(), repository.getLayout(), repository.getUrl() );
        result.setPolicy( true, toRepositoryPolicy( repository.getSnapshots() ) );
        result.setPolicy( false, toRepositoryPolicy( repository.getReleases() ) );
        return result;
    }

    private RepositoryPolicy toRepositoryPolicy( final org.apache.maven.model.RepositoryPolicy policy )
    {
        boolean enabled = true;
        String checksums = RepositoryPolicy.CHECKSUM_POLICY_WARN;
        String updates = RepositoryPolicy.UPDATE_POLICY_DAILY;

        if ( policy != null )
        {
            enabled = policy.isEnabled();
            if ( policy.getUpdatePolicy() != null )
            {
                updates = policy.getUpdatePolicy();
            }
            if ( policy.getChecksumPolicy() != null )
            {
                checksums = policy.getChecksumPolicy();
            }
        }

        return new RepositoryPolicy( enabled, updates, checksums );
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

        final Dependency result =
            new Dependency( artifact, dependency.getScope(), dependency.isOptional(), exclusions );

        return result;
    }

    private Exclusion toExclusion( final org.apache.maven.model.Exclusion exclusion )
    {
        return new Exclusion( exclusion.getGroupId(), exclusion.getArtifactId(), "*", "*" );
    }

}
