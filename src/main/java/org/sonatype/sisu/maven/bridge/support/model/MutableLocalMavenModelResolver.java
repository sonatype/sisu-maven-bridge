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

import java.io.File;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.sisu.maven.bridge.MavenModelResolver;
import org.sonatype.sisu.maven.bridge.support.artifact.MutableLocalMavenArtifactResolver;
import org.sonatype.sisu.maven.bridge.support.model.internal.MavenModelResolverSupport;

@Named( "mutable-local-model-resolver" )
@Singleton
public class MutableLocalMavenModelResolver
    extends MavenModelResolverSupport
    implements MavenModelResolver
{

    @Inject
    public MutableLocalMavenModelResolver( final MutableLocalMavenArtifactResolver artifactResolver )
    {
        super( artifactResolver );
    }

    public void setBaseDir( final File basedir )
    {
        ( (MutableLocalMavenArtifactResolver) getArtifactResolver() ).setBaseDir( basedir );
    }

}
