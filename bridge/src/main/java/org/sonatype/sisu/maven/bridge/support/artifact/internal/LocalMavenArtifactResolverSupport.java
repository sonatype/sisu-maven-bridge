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
package org.sonatype.sisu.maven.bridge.support.artifact.internal;

import java.io.File;
import java.util.Arrays;

import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.internal.impl.Maven2RepositoryLayoutFactory;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.spi.connector.layout.RepositoryLayout;
import org.eclipse.aether.transfer.ArtifactNotFoundException;
import org.eclipse.aether.transfer.NoRepositoryLayoutException;

public abstract class LocalMavenArtifactResolverSupport
    extends MavenArtifactResolverSupport
{

  private final RepositoryLayout layout;

  public LocalMavenArtifactResolverSupport() {
    try {
      // MavenDefaultLayout is no longer visible, so we have to take the long way round to access it
      layout = new Maven2RepositoryLayoutFactory().newInstance(MavenRepositorySystemUtils.newSession(),
          new RemoteRepository.Builder(null, "default", null).build());
    }
    catch (final NoRepositoryLayoutException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  protected Artifact doResolve(final ArtifactRequest artifactRequest,
      final RepositorySystemSession session /* ignored */, final RemoteRepository... repositories /* ignored */)
      throws ArtifactResolutionException
  {
    String path = layout.getLocation(artifactRequest.getArtifact(), false).getPath();

    final File basedir = getBaseDir();

    final File file = new File(basedir, path);

    if (!file.isFile()) {
      ArtifactResult artifactResult = new ArtifactResult(artifactRequest);
      artifactResult.addException(new ArtifactNotFoundException(artifactRequest.getArtifact(), null,
          "Could not find artifact " + artifactRequest.getArtifact() + " in " + basedir.getAbsolutePath()));
      throw new ArtifactResolutionException(Arrays.asList(artifactResult));
    }

    return artifactRequest.getArtifact().setFile(file);
  }

  protected abstract File getBaseDir();

}
