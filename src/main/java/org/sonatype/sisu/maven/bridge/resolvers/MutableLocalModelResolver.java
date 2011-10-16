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

import static org.sonatype.sisu.maven.bridge.Names.LOCAL_MODEL_RESOLVER_ROOT_DIR;

import java.io.File;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.maven.model.resolution.ModelResolver;
import org.sonatype.inject.Nullable;

/**
 * A model resolver that resolves POMs from a local copy of a Maven remote repository using the default layout.
 */
@Singleton
public class MutableLocalModelResolver
    extends LocalModelResolverBase
    implements ModelResolver
{

    private File basedir;

    @Inject
    MutableLocalModelResolver( @Nullable @Named( LOCAL_MODEL_RESOLVER_ROOT_DIR ) File rootdir )
    {
        this.basedir = rootdir;
    }

    public void setBaseDir( File basedir )
    {
        this.basedir = basedir;
    }

    @Override
    public File getBaseDir()
    {
        return basedir;
    }

}
