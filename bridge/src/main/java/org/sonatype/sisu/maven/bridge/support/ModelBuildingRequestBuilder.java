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

import java.io.File;

import org.sonatype.sisu.maven.bridge.support.model.internal.MavenModelResolverSupport.ArtifactModelSource;

import org.apache.maven.model.building.DefaultModelBuildingRequest;
import org.apache.maven.model.building.FileModelSource;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.eclipse.aether.artifact.Artifact;

import static org.sonatype.sisu.maven.bridge.support.ArtifactRequestBuilder.request;

/**
 * TODO
 * 
 * @since 1.0
 */
public class ModelBuildingRequestBuilder
    extends DefaultModelBuildingRequest
{

  private ModelBuildingRequestBuilder() {
    setSystemProperties(System.getProperties());
    setValidationLevel(ModelBuildingRequest.VALIDATION_LEVEL_MINIMAL);
    setLocationTracking(false);
    setProcessPlugins(false);
  }

  public static ModelBuildingRequestBuilder model() {
    return new ModelBuildingRequestBuilder();
  }

  public ModelBuildingRequestBuilder pom(final File pom) {
    setModelSource(new FileModelSource(pom));
    return this;
  }

  public ModelBuildingRequestBuilder pom(final String coordinates) {
    final Artifact artifact = request().artifact(coordinates).getArtifact();
    return pom(artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion());
  }

  public ModelBuildingRequestBuilder pom(final String groupId, final String artifactId, final String version) {
    setModelSource(new ArtifactModelSource(request().artifact(groupId, artifactId, version, "pom")));
    return this;
  }

}
