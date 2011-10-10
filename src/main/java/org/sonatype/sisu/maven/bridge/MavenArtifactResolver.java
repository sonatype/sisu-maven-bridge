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
package org.sonatype.sisu.maven.bridge;

import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.resolution.ArtifactRequest;
import org.sonatype.aether.resolution.ArtifactResolutionException;

/**
 * Resolves maven artifacts.
 *
 * @author adreghiciu
 * @since 2.0
 */
public interface MavenArtifactResolver
{

    /**
     * Resolves an artifact given its coordinates.
     *
     * @param request artifact request
     * @return resolved artifact
     * @throws org.sonatype.aether.resolution.ArtifactResolutionException
     *          if artifact cannot be resolved
     * @since 2.0
     */
    Artifact resolveArtifact( ArtifactRequest request )
        throws ArtifactResolutionException;

    /**
     * Resolves an artifact given its coordinates.
     *
     * @param request artifact request
     * @param session session to be usd while resolving
     * @return resolved artifact
     * @throws org.sonatype.aether.resolution.ArtifactResolutionException
     *          if artifact cannot be resolved
     * @since 2.0
     */
    Artifact resolveArtifact( ArtifactRequest request, RepositorySystemSession session )
        throws ArtifactResolutionException;

}
