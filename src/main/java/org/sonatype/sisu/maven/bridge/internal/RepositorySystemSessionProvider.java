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

import static org.sonatype.sisu.maven.bridge.Names.LOCAL_REPOSITORY_DIR;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.sonatype.aether.AbstractRepositoryListener;
import org.sonatype.aether.RepositoryEvent;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.collection.DependencyGraphTransformer;
import org.sonatype.aether.collection.DependencyManager;
import org.sonatype.aether.collection.DependencySelector;
import org.sonatype.aether.collection.DependencyTraverser;
import org.sonatype.aether.repository.LocalRepository;
import org.sonatype.aether.repository.RepositoryPolicy;
import org.sonatype.aether.util.DefaultRepositorySystemSession;
import org.sonatype.aether.util.artifact.DefaultArtifactTypeRegistry;
import org.sonatype.aether.util.graph.manager.ClassicDependencyManager;
import org.sonatype.aether.util.graph.selector.AndDependencySelector;
import org.sonatype.aether.util.graph.selector.ExclusionDependencySelector;
import org.sonatype.aether.util.graph.selector.OptionalDependencySelector;
import org.sonatype.aether.util.graph.selector.ScopeDependencySelector;
import org.sonatype.aether.util.graph.transformer.ChainedDependencyGraphTransformer;
import org.sonatype.aether.util.graph.transformer.ConflictMarker;
import org.sonatype.aether.util.graph.transformer.JavaDependencyContextRefiner;
import org.sonatype.aether.util.graph.transformer.JavaEffectiveScopeCalculator;
import org.sonatype.aether.util.graph.transformer.NearestVersionConflictResolver;
import org.sonatype.aether.util.graph.traverser.FatArtifactTraverser;
import org.sonatype.aether.util.repository.DefaultAuthenticationSelector;
import org.sonatype.aether.util.repository.DefaultMirrorSelector;
import org.sonatype.aether.util.repository.DefaultProxySelector;

@Named
@Singleton
public class RepositorySystemSessionProvider
    implements Provider<RepositorySystemSession>
{

    private final RepositorySystem repositorySystem;

    private final File localRepository;

    @Inject
    private Logger logger;

    private RepositorySystemSession repositorySession;

    @Inject
    RepositorySystemSessionProvider( RepositorySystem repositorySystem,
                                     @Named( LOCAL_REPOSITORY_DIR ) File localRepository )
    {
        this.repositorySystem = repositorySystem;
        this.localRepository = localRepository;
    }

    public RepositorySystemSession get()
    {
        if ( repositorySession == null )
        {
            repositorySession = newRepositorySession();
        }
        return repositorySession;
    }

    private RepositorySystemSession newRepositorySession()
    {
        final DefaultRepositorySystemSession session = new DefaultRepositorySystemSession();

        session.setIgnoreInvalidArtifactDescriptor( true ).setIgnoreMissingArtifactDescriptor( true );

        session.setOffline( false );
        session.setUpdatePolicy( RepositoryPolicy.UPDATE_POLICY_ALWAYS );

        session.setNotFoundCachingEnabled( false );
        session.setTransferErrorCachingEnabled( false );

        session.setArtifactTypeRegistry( new DefaultArtifactTypeRegistry() );

        final LocalRepository localRepo = new LocalRepository( localRepository );
        session.setLocalRepositoryManager( repositorySystem.newLocalRepositoryManager( localRepo ) );

        final DependencyTraverser depTraverser = new FatArtifactTraverser();
        session.setDependencyTraverser( depTraverser );

        final DependencyManager depManager = new ClassicDependencyManager();
        session.setDependencyManager( depManager );

        final DependencySelector depFilter =
            new AndDependencySelector( new ScopeDependencySelector( "test", "provided" ),
                new OptionalDependencySelector(), new ExclusionDependencySelector() );
        session.setDependencySelector( depFilter );

        final DependencyGraphTransformer transformer =
            new ChainedDependencyGraphTransformer( new ConflictMarker(), new JavaEffectiveScopeCalculator(),
                new NearestVersionConflictResolver(), new JavaDependencyContextRefiner() );
        session.setDependencyGraphTransformer( transformer );

        session.setRepositoryListener( new AbstractRepositoryListener()
                       {
                           @Override
                           public void artifactInstalling( final RepositoryEvent event )
                           {
                               logger.info( "Installing " + event.getArtifact().getFile() + " to " + event.getFile() );
                           }

                           @Override
                           public void metadataInstalling( final RepositoryEvent event )
                           {
                               logger.debug( "Installing " + event.getMetadata() + " to " + event.getFile() );
                           }

                           @Override
                           public void artifactDescriptorInvalid( final RepositoryEvent event )
                           {
                               if ( logger.isDebugEnabled() )
                               {
                                   logger.warn( "The POM for " + event.getArtifact() + " is invalid"
                                       + ", transitive dependencies (if any) will not be available: "
                                       + event.getException().getMessage() );
                               }
                               else
                               {
                                   logger.warn( "The POM for " + event.getArtifact() + " is invalid"
                                       + ", transitive dependencies (if any) will not be available"
                                       + ", enable debug logging for more details" );
                               }
                           }

                           @Override
                           public void artifactDescriptorMissing( final RepositoryEvent event )
                           {
                               logger.warn( "The POM for " + event.getArtifact()
                                   + " is missing, no dependency information available" );
                           }
                       } );

        session.setMirrorSelector( new DefaultMirrorSelector() );
        session.setAuthenticationSelector( new DefaultAuthenticationSelector() );
        session.setProxySelector( new DefaultProxySelector() );

        return session;
    }
}
