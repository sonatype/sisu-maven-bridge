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

import java.io.File;
import java.util.Map;

import org.apache.maven.model.Model;
import org.apache.maven.model.Repository;
import org.apache.maven.model.building.ModelBuildingException;
import org.apache.maven.model.building.ModelSource;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.collection.DependencyCollectionException;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.graph.DependencyNode;

/**
 * Reusable Maven services.
 *
 * @author adreghiciu
 * @author cstamas
 */
public interface MavenBridge
{

    /**
     * Builds the effective model using POM passed in as model source and passed in repositories (if any).
     *
     * @param pom
     * @param repositories
     * @return
     * @throws ModelBuildingException
     */
    Model buildModel( ModelSource pom, Repository... repositories )
        throws ModelBuildingException;

    /**
     * Builds the effective model using POM passed in as model source and passed in repositories (if any).
     *
     * @param pom
     * @param repositories
     * @return
     * @throws ModelBuildingException
     */
    Model buildModel( ModelSource pom, Map<String, String> repositories )
        throws ModelBuildingException;

    /**
     * Builds effective model.
     *
     * @param pom
     * @param repositories
     * @return
     * @throws ModelBuildingException
     * @deprecated use {@link #buildModel(ModelSource, Repository...)} instead.
     */
    Model buildModel( File pom, Repository... repositories )
        throws ModelBuildingException;

    /**
     * Builds effective model.
     *
     * @param pom
     * @param repositories
     * @return
     * @throws ModelBuildingException
     * @deprecated use {@link #buildModel(ModelSource, Map)} instead.
     */

    Model buildModel( File pom, Map<String, String> repositories )
        throws ModelBuildingException;

    /**
     * Builds the dependency transitive hull of the passed in single Dependency node used as root.
     *
     * @param node
     * @param repositories
     * @return
     * @throws DependencyCollectionException
     */
    DependencyNode buildDependencyTree( Dependency node, Repository... repositories )
        throws DependencyCollectionException;

    /**
     * Builds the dependency transitive hull using the passed in model and reusing most parts of it (dependencies,
     * repositories, depMgt, etc).
     *
     * @param model
     * @param repositories
     * @return
     * @throws DependencyCollectionException
     */
    DependencyNode buildDependencyTree( Model model, Repository... repositories )
        throws DependencyCollectionException;

    // ==

    /**
     * Builds the effective model using POM passed in as model source and passed in repositories (if any). This method
     * uses the passed in session, and hence, is meant to be used in "server embedded" use cases like Nexus, when
     * multiple parallel running requests may occur (with different Nexus repositories involved, which means different
     * WorkspaceReaders are present in session).
     *
     * @param session
     * @param pom
     * @param repositories
     * @return
     * @throws ModelBuildingException
     */
    Model buildModel( RepositorySystemSession session, ModelSource pom, Repository... repositories )
        throws ModelBuildingException;

    /**
     * Builds the effective model using POM passed in as model source and passed in repositories (if any). This method
     * uses the passed in session, and hence, is meant to be used in "server embedded" use cases like Nexus, when
     * multiple parallel running requests may occur (with different Nexus repositories involved, which means different
     * WorkspaceReaders are present in session).
     *
     * @param session
     * @param pom
     * @param repositories
     * @return
     * @throws ModelBuildingException
     */
    Model buildModel( RepositorySystemSession session, ModelSource pom, Map<String, String> repositories )
        throws ModelBuildingException;

    /**
     * Builds the dependency transitive hull of the passed in single Dependency node used as root. This method uses the
     * passed in session, and hence, is meant to be used in "server embedded" use cases like Nexus, when multiple
     * parallel running requests may occur (with different Nexus repositories involved, which means different
     * WorkspaceReaders are present in session).
     *
     * @param node
     * @param repositories
     * @return
     * @throws DependencyCollectionException
     */
    DependencyNode buildDependencyTree( RepositorySystemSession session, Dependency node, Repository... repositories )
        throws DependencyCollectionException;

    /**
     * Builds the dependency transitive hull using the passed in model and reusing most parts of it (dependencies,
     * repositories, depMgt, etc). This method uses the passed in session, and hence, is meant to be used in
     * "server embedded" use cases like Nexus, when multiple parallel running requests may occur (with different Nexus
     * repositories involved, which means different WorkspaceReaders are present in session).
     *
     * @param model
     * @param repositories
     * @return
     * @throws DependencyCollectionException
     */
    DependencyNode buildDependencyTree( RepositorySystemSession session, Model model, Repository... repositories )
        throws DependencyCollectionException;

}
