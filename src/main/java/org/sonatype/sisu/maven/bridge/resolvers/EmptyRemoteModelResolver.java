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

package org.sonatype.sisu.maven.bridge.resolvers;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.maven.model.resolution.ModelResolver;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.impl.RemoteRepositoryManager;

@Singleton
public class EmptyRemoteModelResolver
    extends RemoteModelResolver
    implements ModelResolver
{

    @Inject
    public EmptyRemoteModelResolver( final RepositorySystem repositorySystem, final RepositorySystemSession session,
                                     final RemoteRepositoryManager remoteRepositoryManager )
    {
        super( repositorySystem, session, remoteRepositoryManager );
    }

}
