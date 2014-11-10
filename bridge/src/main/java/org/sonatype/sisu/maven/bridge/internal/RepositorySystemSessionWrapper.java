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
package org.sonatype.sisu.maven.bridge.internal;

import java.util.Map;

import org.eclipse.aether.RepositoryCache;
import org.eclipse.aether.RepositoryListener;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.SessionData;
import org.eclipse.aether.artifact.ArtifactTypeRegistry;
import org.eclipse.aether.collection.DependencyGraphTransformer;
import org.eclipse.aether.collection.DependencyManager;
import org.eclipse.aether.collection.DependencySelector;
import org.eclipse.aether.collection.DependencyTraverser;
import org.eclipse.aether.collection.VersionFilter;
import org.eclipse.aether.repository.AuthenticationSelector;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.LocalRepositoryManager;
import org.eclipse.aether.repository.MirrorSelector;
import org.eclipse.aether.repository.ProxySelector;
import org.eclipse.aether.repository.WorkspaceReader;
import org.eclipse.aether.resolution.ArtifactDescriptorPolicy;
import org.eclipse.aether.resolution.ResolutionErrorPolicy;
import org.eclipse.aether.transfer.TransferListener;

/**
 * TODO
 * 
 * @since 1.0
 */
public class RepositorySystemSessionWrapper
    implements RepositorySystemSession
{

  private RepositorySystemSession wrapped;

  public RepositorySystemSessionWrapper(final RepositorySystemSession wrapped) {
    this.wrapped = wrapped;
  }

  @Override
  public ArtifactDescriptorPolicy getArtifactDescriptorPolicy() {
    return wrapped.getArtifactDescriptorPolicy();
  }

  @Override
  public ArtifactTypeRegistry getArtifactTypeRegistry() {
    return wrapped.getArtifactTypeRegistry();
  }

  @Override
  public AuthenticationSelector getAuthenticationSelector() {
    return wrapped.getAuthenticationSelector();
  }

  @Override
  public RepositoryCache getCache() {
    return wrapped.getCache();
  }

  @Override
  public String getChecksumPolicy() {
    return wrapped.getChecksumPolicy();
  }

  @Override
  public Map<String, Object> getConfigProperties() {
    return wrapped.getConfigProperties();
  }

  @Override
  public SessionData getData() {
    return wrapped.getData();
  }

  @Override
  public DependencyGraphTransformer getDependencyGraphTransformer() {
    return wrapped.getDependencyGraphTransformer();
  }

  @Override
  public DependencyManager getDependencyManager() {
    return wrapped.getDependencyManager();
  }

  @Override
  public DependencySelector getDependencySelector() {
    return wrapped.getDependencySelector();
  }

  @Override
  public DependencyTraverser getDependencyTraverser() {
    return wrapped.getDependencyTraverser();
  }

  @Override
  public LocalRepository getLocalRepository() {
    return wrapped.getLocalRepository();
  }

  @Override
  public LocalRepositoryManager getLocalRepositoryManager() {
    return wrapped.getLocalRepositoryManager();
  }

  @Override
  public MirrorSelector getMirrorSelector() {
    return wrapped.getMirrorSelector();
  }

  @Override
  public ProxySelector getProxySelector() {
    return wrapped.getProxySelector();
  }

  @Override
  public RepositoryListener getRepositoryListener() {
    return wrapped.getRepositoryListener();
  }

  @Override
  public ResolutionErrorPolicy getResolutionErrorPolicy() {
    return wrapped.getResolutionErrorPolicy();
  }

  @Override
  public Map<String, String> getSystemProperties() {
    return wrapped.getSystemProperties();
  }

  @Override
  public TransferListener getTransferListener() {
    return wrapped.getTransferListener();
  }

  @Override
  public String getUpdatePolicy() {
    return wrapped.getUpdatePolicy();
  }

  @Override
  public Map<String, String> getUserProperties() {
    return wrapped.getUserProperties();
  }

  @Override
  public VersionFilter getVersionFilter() {
    return wrapped.getVersionFilter();
  }

  @Override
  public WorkspaceReader getWorkspaceReader() {
    return wrapped.getWorkspaceReader();
  }

  @Override
  public boolean isIgnoreArtifactDescriptorRepositories() {
    return wrapped.isIgnoreArtifactDescriptorRepositories();
  }

  @Override
  public boolean isOffline() {
    return wrapped.isOffline();
  }

}
