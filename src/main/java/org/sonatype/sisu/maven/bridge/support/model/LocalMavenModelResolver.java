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

package org.sonatype.sisu.maven.bridge.support.model;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.sonatype.sisu.maven.bridge.MavenModelResolver;
import org.sonatype.sisu.maven.bridge.support.artifact.LocalMavenArtifactResolver;
import org.sonatype.sisu.maven.bridge.support.artifact.RemoteMavenArtifactResolver;
import org.sonatype.sisu.maven.bridge.support.model.internal.MavenModelResolverSupport;

@Singleton
public class LocalMavenModelResolver
    extends MavenModelResolverSupport
    implements MavenModelResolver
{

    @Inject
    LocalMavenModelResolver( final LocalMavenArtifactResolver artifactResolver )
    {
        super( artifactResolver );
    }

}
