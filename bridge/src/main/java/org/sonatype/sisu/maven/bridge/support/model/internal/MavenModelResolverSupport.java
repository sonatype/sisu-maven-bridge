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
package org.sonatype.sisu.maven.bridge.support.model.internal;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Provider;

import org.sonatype.sisu.maven.bridge.MavenArtifactResolver;
import org.sonatype.sisu.maven.bridge.MavenModelResolver;
import org.sonatype.sisu.maven.bridge.internal.ComponentSupport;

import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Repository;
import org.apache.maven.model.building.DefaultModelBuilder;
import org.apache.maven.model.building.DefaultModelBuilderFactory;
import org.apache.maven.model.building.FileModelSource;
import org.apache.maven.model.building.ModelBuildingException;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelBuildingResult;
import org.apache.maven.model.building.ModelSource;
import org.apache.maven.model.resolution.InvalidRepositoryException;
import org.apache.maven.model.resolution.ModelResolver;
import org.apache.maven.model.resolution.UnresolvableModelException;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;

import static java.util.Arrays.asList;
import static org.sonatype.sisu.maven.bridge.support.ArtifactRequestBuilder.request;
import static org.sonatype.sisu.maven.bridge.support.RemoteRepositoryBuilder.remoteRepositories;
import static org.sonatype.sisu.maven.bridge.support.RemoteRepositoryBuilder.remoteRepository;

public abstract class MavenModelResolverSupport
    extends ComponentSupport
    implements MavenModelResolver
{

  protected static final Provider<RepositorySystemSession> NO_SESSION_PROVIDER = null;

  private final DefaultModelBuilder modelBuilder;

  private final MavenArtifactResolver artifactResolver;

  private Provider<RepositorySystemSession> sessionProvider;

  protected MavenModelResolverSupport(final MavenArtifactResolver artifactResolver) {
    this(artifactResolver, NO_SESSION_PROVIDER);
  }

  protected MavenModelResolverSupport(final MavenArtifactResolver artifactResolver,
      final @Nullable Provider<RepositorySystemSession> sessionProvider)
  {
    this.artifactResolver = assertNotNull(artifactResolver, "remote maven artifact resolver not specified");
    this.modelBuilder = new DefaultModelBuilderFactory().newInstance();
    this.sessionProvider = sessionProvider;
  }

  @Override
  public Model resolveModel(final ModelBuildingRequest request, final RepositorySystemSession session,
      final RemoteRepository... repositories) throws ModelBuildingException
  {
    assertNotNull(session, session.getClass());

    request.setModelResolver(new ModelResolverProxy(session, repositories));
    if (request.getModelSource() instanceof ArtifactModelSource) {
      final ArtifactModelSource source = (ArtifactModelSource) request.getModelSource();
      source.resolver = new ModelResolverProxy(session, repositories);
    }
    final ModelBuildingResult modelResult = modelBuilder.build(request);
    return modelResult.getEffectiveModel();
  }

  @Override
  public Model resolveModel(final ModelBuildingRequest request, final RemoteRepository... repositories)
      throws ModelBuildingException
  {
    return resolveModel(request, assertNotNull(sessionProvider, "Repository system session provider not specified")
        .get());
  }

  // ==

  public MavenArtifactResolver getArtifactResolver() {
    return artifactResolver;
  }

  public static class ArtifactModelSource
      implements ModelSource
  {

    private ArtifactRequest artifactRequest;

    public ArtifactModelSource(final ArtifactRequest artifactRequest) {
      this.artifactRequest = artifactRequest;
    }

    private ModelResolver resolver;

    private ModelSource source;

    @Override
    public InputStream getInputStream() throws IOException {
      try {
        resolve();
      }
      catch (UnresolvableModelException e) {
        throw new IOException(e);
      }
      return source == null ? null : source.getInputStream();
    }

    @Override
    public String getLocation() {
      try {
        resolve();
      }
      catch (UnresolvableModelException ignore) {
        // ignore
      }
      return source == null ? null : source.getLocation();
    }

    private void resolve() throws UnresolvableModelException {
      if (source == null && resolver != null) {
        final Artifact artifact = artifactRequest.getArtifact();
        source = resolver.resolveModel(artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion());
      }
    }

  }

  private class ModelResolverProxy
      implements ModelResolver
  {

    private final RepositorySystemSession session;

    private final RemoteRepository[] repositories;

    private final List<RemoteRepository> allRepositories;

    public ModelResolverProxy(final RepositorySystemSession session, final RemoteRepository... repositories) {
      this.session = session;
      this.repositories = repositories;
      this.allRepositories = new ArrayList<RemoteRepository>();
      if (repositories != null) {
        this.allRepositories.addAll(asList(repositories));
      }
    }

    @Override
    public ModelSource resolveModel(final String groupId, final String artifactId, final String version)
        throws UnresolvableModelException
    {
      try {
        final Artifact pom = artifactResolver.resolveArtifact(request().artifact(groupId, artifactId, version, "pom"),
            session, remoteRepositories(allRepositories));
        return new FileModelSource(pom.getFile());
      }
      catch (ArtifactResolutionException e) {
        throw new UnresolvableModelException(e.getMessage(), groupId, artifactId, version, e);
      }
    }

    @Override
    public ModelSource resolveModel(final Parent parent) throws UnresolvableModelException {
      return resolveModel(parent.getGroupId(), parent.getArtifactId(), parent.getVersion());
    }

    @Override
    public void addRepository(final Repository repository) throws InvalidRepositoryException {
      allRepositories.add(remoteRepository(repository));
    }

    @Override
    public void addRepository(final Repository repository, final boolean replace) throws InvalidRepositoryException {
      if (replace) {
        for (int i = 0; i < allRepositories.size(); i++) {
          if (allRepositories.get(i).getId().equals(repository.getId())) {
            allRepositories.set(i, remoteRepository(repository));
            return;
          }
        }
      }
      allRepositories.add(remoteRepository(repository));
    }

    @Override
    public ModelResolver newCopy() {
      return new ModelResolverProxy(session, repositories);
    }

  }

}
