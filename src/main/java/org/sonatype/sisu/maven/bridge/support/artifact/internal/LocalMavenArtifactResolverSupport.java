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
package org.sonatype.sisu.maven.bridge.support.artifact.internal;

import java.io.File;
import java.util.Arrays;

import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.ArtifactRequest;
import org.sonatype.aether.resolution.ArtifactResolutionException;
import org.sonatype.aether.resolution.ArtifactResult;
import org.sonatype.aether.transfer.ArtifactNotFoundException;
import org.sonatype.aether.util.layout.MavenDefaultLayout;
import org.sonatype.aether.util.layout.RepositoryLayout;

public abstract class LocalMavenArtifactResolverSupport
    extends MavenArtifactResolverSupport
{

    private final RepositoryLayout layout = new MavenDefaultLayout();

    @Override
    protected Artifact doResolve( final ArtifactRequest artifactRequest,
                                  final RepositorySystemSession session )
        throws ArtifactResolutionException
    {
        String path = layout.getPath( artifactRequest.getArtifact() ).getPath();

        final File basedir = getBaseDir();

        final File file = new File( basedir, path );

        if ( !file.isFile() )
        {
            ArtifactResult artifactResult = new ArtifactResult( artifactRequest );
            artifactResult.addException( new ArtifactNotFoundException(
                artifactRequest.getArtifact(),
                new RemoteRepository()
                {
                    @Override
                    public String toString()
                    {
                        return basedir.getAbsolutePath();
                    }
                } ) );
            throw new ArtifactResolutionException( Arrays.asList( artifactResult ) );
        }

        artifactRequest.getArtifact().setFile( file );

        return artifactRequest.getArtifact();
    }

    protected abstract File getBaseDir();

}
