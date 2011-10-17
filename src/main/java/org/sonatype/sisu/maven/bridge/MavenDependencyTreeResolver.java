/*
 * Copyright (c) 2009-2011 Sonatype, Inc.
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

import org.apache.maven.model.Repository;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.collection.DependencyCollectionException;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.aether.repository.RemoteRepository;

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
     * @param request      dependency tree request
     * @param repositories repositories to use while resolving
     * @return resolved dependency tree
     * @throws DependencyCollectionException If dependencies could nto be resolved
     */
    DependencyNode resolveDependencyTree( CollectRequest request,
                                          RemoteRepository... repositories )
        throws DependencyCollectionException;

    /**
     * Builds the dependency transitive hull.
     *
     * @param request      dependency tree request
     * @param session      session to be used while resolving
     * @param repositories repositories to use while resolving
     * @return resolved dependency tree
     * @throws DependencyCollectionException If dependencies could nto be resolved
     */
    DependencyNode resolveDependencyTree( CollectRequest request,
                                          RepositorySystemSession session,
                                          RemoteRepository... repositories )
        throws DependencyCollectionException;

}
