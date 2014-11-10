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
package org.sonatype.sisu.maven.bridge.support;

import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;

/**
 * TODO
 * 
 * @author adreghiciu
 * @since 2.0
 */
public class ArtifactRequestBuilder
{
  private final ArtifactRequest artifactRequest = new ArtifactRequest();

  public static ArtifactRequestBuilder request() {
    return new ArtifactRequestBuilder();
  }

  public ArtifactRequest artifact(final String coordinates) {
    artifactRequest.setArtifact(new DefaultArtifact(coordinates));
    return artifactRequest;
  }

  public ArtifactRequest artifact(final String groupId, final String artifactId, final String version,
      final String extension, final String classifier)
  {
    artifactRequest.setArtifact(new DefaultArtifact(groupId, artifactId, classifier, extension, version));
    return artifactRequest;
  }

  public ArtifactRequest artifact(final String groupId, final String artifactId, final String version,
      final String extension)
  {
    artifactRequest.setArtifact(new DefaultArtifact(groupId, artifactId, extension, version));
    return artifactRequest;
  }

  public ArtifactRequest artifact(final String groupId, final String artifactId, final String version) {
    return artifact(groupId, artifactId, version, "jar");
  }

  public ArtifactRequestBuilder repository(final RemoteRepository... repositories) {
    for (RemoteRepository repository : repositories) {
      artifactRequest.addRepository(repository);
    }
    return this;
  }

}
