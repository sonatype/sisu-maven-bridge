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

import java.util.Collection;

import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.repository.RepositoryPolicy;

import org.apache.maven.model.Repository;

/**
 * TODO
 * 
 * @author adreghiciu
 * @since 2.0
 */
public class RemoteRepositoryBuilder
{

  public static RemoteRepository remoteRepository(final String url) {
    return remoteRepository(null, "default", url);
  }

  public static RemoteRepository[] remoteRepositories(final Collection<RemoteRepository> repositories) {
    if (repositories == null) {
      return new RemoteRepository[0];
    }
    return repositories.toArray(new RemoteRepository[repositories.size()]);
  }

  public static RemoteRepository remoteRepository(final String id, final String type, final String url) {
    final RemoteRepository repository = new RemoteRepository(id, type, url);
    repository.setPolicy(false, new RepositoryPolicy(true, RepositoryPolicy.UPDATE_POLICY_DAILY,
        RepositoryPolicy.CHECKSUM_POLICY_WARN));
    return repository;
  }

  public static RemoteRepository remoteRepository(final Repository repository) {
    final RemoteRepository remoteRepository = new RemoteRepository(repository.getId(), repository.getLayout(),
        repository.getUrl());
    remoteRepository.setPolicy(true, convert(repository.getSnapshots()));
    remoteRepository.setPolicy(false, convert(repository.getReleases()));
    return remoteRepository;
  }

  public static RemoteRepository remoteRepository(final org.apache.maven.settings.Repository repository) {
    final RemoteRepository remoteRepository = new RemoteRepository(repository.getId(), repository.getLayout(),
        repository.getUrl());
    remoteRepository.setPolicy(true, convert(repository.getSnapshots()));
    remoteRepository.setPolicy(false, convert(repository.getReleases()));
    return remoteRepository;
  }

  private static RepositoryPolicy convert(final org.apache.maven.model.RepositoryPolicy policy) {
    boolean enabled = true;
    String checksum = RepositoryPolicy.CHECKSUM_POLICY_WARN;
    String updates = RepositoryPolicy.UPDATE_POLICY_DAILY;

    if (policy != null) {
      enabled = policy.isEnabled();
      if (policy.getUpdatePolicy() != null) {
        updates = policy.getUpdatePolicy();
      }
      if (policy.getChecksumPolicy() != null) {
        checksum = policy.getChecksumPolicy();
      }
    }

    return new RepositoryPolicy(enabled, updates, checksum);
  }

  private static RepositoryPolicy convert(final org.apache.maven.settings.RepositoryPolicy policy) {
    boolean enabled = true;
    String checksum = RepositoryPolicy.CHECKSUM_POLICY_WARN;
    String updates = RepositoryPolicy.UPDATE_POLICY_DAILY;

    if (policy != null) {
      enabled = policy.isEnabled();
      if (policy.getUpdatePolicy() != null) {
        updates = policy.getUpdatePolicy();
      }
      if (policy.getChecksumPolicy() != null) {
        checksum = policy.getChecksumPolicy();
      }
    }

    return new RepositoryPolicy(enabled, updates, checksum);
  }

}
