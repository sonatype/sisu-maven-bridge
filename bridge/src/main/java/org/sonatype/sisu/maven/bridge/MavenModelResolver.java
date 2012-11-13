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

import org.apache.maven.model.Model;
import org.apache.maven.model.building.ModelBuildingException;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.repository.RemoteRepository;

/**
 * Resolves Maven models.
 *
 * @since 2.0
 */
public interface MavenModelResolver
{

    /**
     * Builds the effective model of a Maven artifact.
     *
     * @param request      model build request
     * @param repositories remote repositories to be used to resolve models
     * @return effective model
     * @throws ModelBuildingException If model could not be built
     * @since 2.0
     */
    Model resolveModel( ModelBuildingRequest request,
                        RemoteRepository... repositories )
        throws ModelBuildingException;

    /**
     * Builds the effective model of a Maven artifact.
     *
     * @param request      model build request
     * @param session      session to be used while resolving
     * @param repositories remote repositories to be used to resolve models
     * @return effective model
     * @throws ModelBuildingException If model could not be built
     * @since 2.0
     */
    Model resolveModel( ModelBuildingRequest request,
                        RepositorySystemSession session,
                        RemoteRepository... repositories )
        throws ModelBuildingException;

}
