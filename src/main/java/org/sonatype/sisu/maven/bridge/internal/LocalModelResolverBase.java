/*******************************************************************************
 * Copyright (c) 2010-2011 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 * The Eclipse Public License is available at
 *   http://www.eclipse.org/legal/epl-v10.html
 * The Apache License v2.0 is available at
 *   http://www.apache.org/licenses/LICENSE-2.0.html
 * You may elect to redistribute this code under either of these licenses.
 *******************************************************************************/
package org.sonatype.sisu.maven.bridge.internal;

import java.io.File;

import org.apache.maven.model.Repository;
import org.apache.maven.model.building.FileModelSource;
import org.apache.maven.model.building.ModelSource;
import org.apache.maven.model.resolution.InvalidRepositoryException;
import org.apache.maven.model.resolution.ModelResolver;
import org.apache.maven.model.resolution.UnresolvableModelException;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.sonatype.aether.util.layout.MavenDefaultLayout;
import org.sonatype.aether.util.layout.RepositoryLayout;

/**
 * A model resolver that resolves POMs from a local copy of a Maven remote repository using the default layout.
 */
public abstract class LocalModelResolverBase
    implements ModelResolver
{

    private final RepositoryLayout layout;

    protected LocalModelResolverBase()
    {
        layout = new MavenDefaultLayout();
    }

    public ModelSource resolveModel( String groupId, String artifactId, String version )
        throws UnresolvableModelException
    {
        String path = layout.getPath( new DefaultArtifact( groupId, artifactId, "pom", version ) ).getPath();

        File pomFile = new File( getBaseDir(), path );

        if ( !pomFile.isFile() )
        {
            throw new UnresolvableModelException( "POM does not exist: " + pomFile, groupId, artifactId, version );
        }

        return new FileModelSource( pomFile );
    }

    public void addRepository( Repository repository )
        throws InvalidRepositoryException
    {
        // ignored, we're backed by a static/local filesystem
    }

    public ModelResolver newCopy()
    {
        return this;
    }

    protected abstract File getBaseDir();

}
