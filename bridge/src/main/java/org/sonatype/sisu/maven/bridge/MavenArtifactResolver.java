/*
 * Copyright (c) 2009-2012 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 * The Eclipse Public License is available at
 *   http://www.eclipse.org/legal/epl-v10.html
 * The Apache License v2.0 is available at
 *   http://www.apache.org/licenses/LICENSE-2.0.html
 * You may elect to redistribute this code under either of these licenses.
 */
package org.sonatype.sisu.maven.bridge;

import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;

/**
 * Resolves maven artifacts.
 * 
 * @since 2.0
 */
public interface MavenArtifactResolver
{

  /**
   * Resolves an artifact given its coordinates.
   * 
   * @param request artifact request
   * @param repositories repositories to be used to resolve the artifacts
   * @return resolved artifact
   * @throws ArtifactResolutionException If artifact cannot be resolved
   * @since 2.0
   */
  Artifact resolveArtifact(ArtifactRequest request, RemoteRepository... repositories)
      throws ArtifactResolutionException;

  /**
   * Resolves an artifact given its coordinates.
   * 
   * @param request artifact request
   * @param session session to be used while resolving
   * @param repositories repositories to be used to resolve the artifacts
   * @return resolved artifact
   * @throws ArtifactResolutionException If artifact cannot be resolved
   * @since 2.0
   */
  Artifact resolveArtifact(ArtifactRequest request, RepositorySystemSession session, RemoteRepository... repositories)
      throws ArtifactResolutionException;

}
