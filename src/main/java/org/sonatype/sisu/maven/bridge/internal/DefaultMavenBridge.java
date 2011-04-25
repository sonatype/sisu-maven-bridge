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
import org.apache.maven.model.resolution.InvalidRepositoryException;
import org.apache.maven.model.resolution.ModelResolver;
import org.apache.maven.repository.internal.ArtifactDescriptorUtils;
import org.slf4j.Logger;
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
import org.sonatype.aether.util.artifact.ArtifactProperties;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.sonatype.aether.util.artifact.DefaultArtifactType;
import org.sonatype.sisu.maven.bridge.MavenBridge;

@Named
@Singleton
class DefaultMavenBridge
    implements MavenBridge
{

    private final ModelBuilder modelBuilder;

    private final ModelResolver modelResolver;

    private final RepositorySystem repositorySystem;

    private final RepositorySystemSession repositorySession;

    @Inject
    private Logger logger;

    @Inject
    DefaultMavenBridge( ModelResolver modelResolver, RepositorySystem repositorySystem,
                        RepositorySystemSession repositorySession )
    {
        this.modelResolver = modelResolver;

        this.modelBuilder = new DefaultModelBuilderFactory().newInstance();

        this.repositorySystem = repositorySystem;

        this.repositorySession = repositorySession;
    }

    public Model buildModel( File pom, Repository... repositories )
        throws ModelBuildingException
    {
        ModelResolver mr = modelResolver.newCopy();
        if ( repositories != null )
        {
            for ( Repository repository : repositories )
            {
                try
                {
                    mr.addRepository( repository );
                }
                catch ( InvalidRepositoryException e )
                {
                    logger.warn( String.format( "Could not use repository [%s]", repository.getUrl() ), e );
                }
            }
        }

        ModelBuildingRequest modelRequest = new DefaultModelBuildingRequest();
        modelRequest.setModelSource( new FileModelSource( pom ) );
        modelRequest.setSystemProperties( System.getProperties() );
        modelRequest.setValidationLevel( ModelBuildingRequest.VALIDATION_LEVEL_MINIMAL );
        modelRequest.setLocationTracking( false );
        modelRequest.setProcessPlugins( false );
        modelRequest.setModelResolver( mr );

        ModelBuildingResult modelResult = modelBuilder.build( modelRequest );
        Model model = modelResult.getEffectiveModel();
        return model;
    }

    public DependencyNode buildDependencyTree( Model model )
        throws DependencyCollectionException
    {
        CollectRequest request = new CollectRequest();
        request.setRequestContext( "project" );
        for ( Repository repo : model.getRepositories() )
        {
            request.addRepository( ArtifactDescriptorUtils.toRemoteRepository( repo ) );
        }
        ArtifactTypeRegistry stereotypes = repositorySession.getArtifactTypeRegistry();
        for ( org.apache.maven.model.Dependency dep : model.getDependencies() )
        {
            request.addDependency( toDependency( dep, stereotypes ) );
        }
        if ( model.getDependencyManagement() != null )
        {
            for ( org.apache.maven.model.Dependency dep : model.getDependencyManagement().getDependencies() )
            {
                request.addManagedDependency( toDependency( dep, stereotypes ) );
            }
        }

        return repositorySystem.collectDependencies( repositorySession, request ).getRoot();
    }

    private Dependency toDependency( org.apache.maven.model.Dependency dependency, ArtifactTypeRegistry stereotypes )
    {
        ArtifactType stereotype = stereotypes.get( dependency.getType() );
        if ( stereotype == null )
        {
            stereotype = new DefaultArtifactType( dependency.getType() );
        }

        boolean system = dependency.getSystemPath() != null && dependency.getSystemPath().length() > 0;

        Map<String, String> props = null;
        if ( system )
        {
            props = Collections.singletonMap( ArtifactProperties.LOCAL_PATH, dependency.getSystemPath() );
        }

        Artifact artifact =
            new DefaultArtifact( dependency.getGroupId(), dependency.getArtifactId(), dependency.getClassifier(), null,
                                 dependency.getVersion(), props, stereotype );

        List<Exclusion> exclusions = new ArrayList<Exclusion>( dependency.getExclusions().size() );
        for ( org.apache.maven.model.Exclusion exclusion : dependency.getExclusions() )
        {
            exclusions.add( toExclusion( exclusion ) );
        }

        Dependency result = new Dependency( artifact, dependency.getScope(), dependency.isOptional(), exclusions );

        return result;
    }

    private Exclusion toExclusion( org.apache.maven.model.Exclusion exclusion )
    {
        return new Exclusion( exclusion.getGroupId(), exclusion.getArtifactId(), "*", "*" );
    }

}
