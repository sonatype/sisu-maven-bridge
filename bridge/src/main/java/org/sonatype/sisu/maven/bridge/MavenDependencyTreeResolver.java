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

import org.sonatype.sisu.maven.bridge.support.CollectRequestBuilder;

import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.repository.RemoteRepository;

/**
 * Resolves maven dependency trees.
 * 
 * @since 2.0
 */
public interface MavenDependencyTreeResolver
{

  /**
   * Builds the dependency transitive hull.
   * 
   * @param requestBuilder dependency tree request builder
   * @param repositories repositories to use while resolving
   * @return resolved dependency tree
   * @throws DependencyCollectionException If dependencies could nto be resolved
   */
  DependencyNode resolveDependencyTree(CollectRequestBuilder requestBuilder, RemoteRepository... repositories)
      throws DependencyCollectionException;

  /**
   * Builds the dependency transitive hull.
   * 
   * @param requestBuilder dependency tree request builder
   * @param session session to be used while resolving
   * @param repositories repositories to use while resolving
   * @return resolved dependency tree
   * @throws DependencyCollectionException If dependencies could nto be resolved
   */
  DependencyNode resolveDependencyTree(CollectRequestBuilder requestBuilder, RepositorySystemSession session,
      RemoteRepository... repositories) throws DependencyCollectionException;

}
