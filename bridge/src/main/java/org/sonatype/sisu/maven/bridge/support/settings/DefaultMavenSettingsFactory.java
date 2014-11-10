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
package org.sonatype.sisu.maven.bridge.support.settings;

import java.io.File;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;

import org.sonatype.sisu.maven.bridge.support.MavenSettings;
import org.sonatype.sisu.maven.bridge.support.MavenSettingsFactory;

import com.google.common.collect.Lists;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.spi.locator.ServiceLocator;

import static org.sonatype.sisu.maven.bridge.Names.DEFAULT_GLOBAL_SETTINGS_FILE;
import static org.sonatype.sisu.maven.bridge.Names.DEFAULT_USER_SETTINGS_FILE;
import static org.sonatype.sisu.maven.bridge.Names.GLOBAL_SETTINGS;
import static org.sonatype.sisu.maven.bridge.Names.REPOSITORIES;
import static org.sonatype.sisu.maven.bridge.Names.USER_SETTINGS;

/**
 * Maven settings factory.
 * 
 * @author adreghiciu
 * @since 2.0
 */
@Named
public class DefaultMavenSettingsFactory
    implements MavenSettingsFactory
{

  private File globalSettings;

  private File userSettings;

  private ServiceLocator serviceLocator;

  private RepositorySystem repositorySystem;

  private final List<RemoteRepository> repositories;

  public DefaultMavenSettingsFactory(final ServiceLocator serviceLocator) {
    this(serviceLocator, null);
  }

  public DefaultMavenSettingsFactory(final ServiceLocator serviceLocator, final List<RemoteRepository> repositories) {
    this.serviceLocator = serviceLocator;
    this.globalSettings = DEFAULT_GLOBAL_SETTINGS_FILE;
    this.userSettings = DEFAULT_USER_SETTINGS_FILE;
    this.repositories = repositories;
  }

  @Inject
  public DefaultMavenSettingsFactory(final ServiceLocator serviceLocator, final @Nullable @Named("${" + GLOBAL_SETTINGS
      + "}") File globalSettings, final @Nullable @Named("${" + USER_SETTINGS + "}") File userSettings,
      final @Nullable @Named("${" + REPOSITORIES + "}") String repositories)
  {
    this(serviceLocator, toRemoteRepositories(repositories));
    if (globalSettings != null && globalSettings.isFile()) {
      this.globalSettings = globalSettings;
    }
    if (userSettings != null && userSettings.isFile()) {
      this.userSettings = userSettings;
    }
  }

  @Override
  public MavenSettings create() {
    return create(globalSettings);
  }

  @Override
  public MavenSettings create(final File globalSettings) {
    return create(globalSettings, userSettings);
  }

  @Override
  public MavenSettings create(final File globalSettings, final File userSettings) {
    return new DefaultMavenSettings(globalSettings, userSettings, getRepositorySystem(), repositories);
  }

  private RepositorySystem getRepositorySystem() {
    if (repositorySystem == null) {
      repositorySystem = serviceLocator.getService(RepositorySystem.class);
    }
    return repositorySystem;
  }

  private static List<RemoteRepository> toRemoteRepositories(final String repositories) {
    final List<RemoteRepository> remoteRepositories = Lists.newArrayList();
    if (repositories != null && repositories.length() > 0) {
      final String[] segments = repositories.split(",");
      for (final String segment : segments) {
        final String[] parts = segment.split("::");
        if (parts.length != 3) {
          throw new IllegalArgumentException("Expected '<id>::<layout>::<url>' but got '" + segment + "'");
        }
        remoteRepositories.add(new RemoteRepository.Builder(parts[0], parts[1], parts[2]).build());
      }
    }
    return remoteRepositories;
  }

}
