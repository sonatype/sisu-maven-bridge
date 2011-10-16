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

package org.sonatype.sisu.maven.bridge.support.artifact.internal;

import javax.inject.Inject;
import javax.inject.Provider;

import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.ArtifactRequest;
import org.sonatype.aether.resolution.ArtifactResolutionException;
import org.sonatype.sisu.maven.bridge.MavenArtifactResolver;
import org.sonatype.sisu.maven.bridge.internal.ComponentSupport;

public abstract class MavenArtifactResolverSupport
    extends ComponentSupport
    implements MavenArtifactResolver
{

    private Provider<RepositorySystemSession> repositorySystemSessionProvider;

    @Inject
    void setRepositorySystemSessionProvider( final Provider<RepositorySystemSession> repositorySystemSessionProvider )
    {
        this.repositorySystemSessionProvider = repositorySystemSessionProvider;
    }

    @Override
    public Artifact resolveArtifact( final ArtifactRequest artifactRequest,
                                     final RepositorySystemSession session,
                                     final RemoteRepository... repositories )
        throws ArtifactResolutionException
    {
        assertNotNull( session, session.getClass() );

        return doResolve( artifactRequest, session );
    }

    // ==

    @Override
    public Artifact resolveArtifact( final ArtifactRequest artifactRequest,
                                     final RemoteRepository... repositories )
        throws ArtifactResolutionException
    {
        return resolveArtifact( artifactRequest, repositorySystemSessionProvider.get() );
    }

    protected abstract Artifact doResolve( final ArtifactRequest artifactRequest,
                                           final RepositorySystemSession session,
                                           final RemoteRepository... repositories )
        throws ArtifactResolutionException;

}
