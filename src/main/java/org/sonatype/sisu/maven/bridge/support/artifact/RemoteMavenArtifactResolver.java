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
package org.sonatype.sisu.maven.bridge.support.artifact;

import java.util.Collections;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.impl.RemoteRepositoryManager;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.ArtifactRequest;
import org.sonatype.aether.resolution.ArtifactResolutionException;
import org.sonatype.aether.spi.locator.ServiceLocator;
import org.sonatype.sisu.maven.bridge.MavenArtifactResolver;
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
                                  final RepositorySystemSession session )
        throws ArtifactResolutionException
    {
        artifactRequest.setRepositories(
            remoteRepositoryManager.aggregateRepositories(
                session, Collections.<RemoteRepository>emptyList(), artifactRequest.getRepositories(), RECESSIVE_IS_RAW
            )
        );
        return repositorySystem.resolveArtifact( session, artifactRequest ).getArtifact();
    }

}
