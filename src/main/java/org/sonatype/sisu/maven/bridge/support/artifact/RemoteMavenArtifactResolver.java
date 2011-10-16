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

package org.sonatype.sisu.maven.bridge.support.artifact;

import static java.util.Arrays.asList;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.impl.RemoteRepositoryManager;
import org.sonatype.aether.repository.LocalRepository;
import org.sonatype.aether.repository.LocalRepositoryManager;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.ArtifactRequest;
import org.sonatype.aether.resolution.ArtifactResolutionException;
import org.sonatype.aether.spi.locator.ServiceLocator;
import org.sonatype.sisu.maven.bridge.MavenArtifactResolver;
import org.sonatype.sisu.maven.bridge.Names;
import org.sonatype.sisu.maven.bridge.internal.RepositorySystemSessionWrapper;
import org.sonatype.sisu.maven.bridge.support.artifact.internal.MavenArtifactResolverSupport;

@Singleton
public class RemoteMavenArtifactResolver
    extends MavenArtifactResolverSupport
    implements MavenArtifactResolver
{

    static final boolean RECESSIVE_IS_RAW = true;

    private RepositorySystem repositorySystem;

    private RemoteRepositoryManager remoteRepositoryManager;

    @Inject
    void setServiceLocator( final ServiceLocator serviceLocator )
    {
        repositorySystem = serviceLocator.getService( RepositorySystem.class );
        remoteRepositoryManager = serviceLocator.getService( RemoteRepositoryManager.class );
    }

    @Override
    protected Artifact doResolve( final ArtifactRequest artifactRequest,
                                  final RepositorySystemSession session,
                                  final RemoteRepository... repositories )
        throws ArtifactResolutionException
    {

        final List<RemoteRepository> allRepositories = new ArrayList<RemoteRepository>();
        if ( repositories != null && repositories.length > 0 )
        {
            allRepositories.addAll( asList( repositories ) );
        }
        allRepositories.addAll( artifactRequest.getRepositories() );

        artifactRequest.setRepositories(
            remoteRepositoryManager.aggregateRepositories(
                session, Collections.<RemoteRepository>emptyList(), allRepositories, RECESSIVE_IS_RAW
            )
        );

        RepositorySystemSession safeSession = session;
        if ( session.getLocalRepositoryManager() == null || session.getLocalRepository() == null )
        {
            safeSession = new RepositorySystemSessionWrapper( session )
            {
                final LocalRepositoryManager lrm = repositorySystem.newLocalRepositoryManager(
                    new LocalRepository( new File( Names.MAVEN_USER_HOME, "repository" ) )
                );

                public LocalRepositoryManager getLocalRepositoryManager()
                {
                    return lrm;
                }

                public LocalRepository getLocalRepository()
                {
                    return lrm.getRepository();
                }
            };
        }
        return repositorySystem.resolveArtifact( safeSession, artifactRequest ).getArtifact();
    }

}
