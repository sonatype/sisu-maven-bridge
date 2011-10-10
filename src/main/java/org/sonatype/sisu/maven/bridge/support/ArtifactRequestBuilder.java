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
package org.sonatype.sisu.maven.bridge.support;

import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.ArtifactRequest;
import org.sonatype.aether.util.artifact.DefaultArtifact;

/**
 * TODO
 *
 * @author adreghiciu
 * @since 2.0
 */
public class ArtifactRequestBuilder
    extends ArtifactRequest
{

    public static ArtifactRequestBuilder request()
    {
        return new ArtifactRequestBuilder();
    }

    public ArtifactRequestBuilder artifact( final String coordinates )
    {
        setArtifact( new DefaultArtifact( coordinates ) );
        return this;
    }

    public ArtifactRequestBuilder artifact( final String groupId,
                                            final String artifactId,
                                            final String version,
                                            final String extension,
                                            final String classifier )
    {
        setArtifact( new DefaultArtifact( groupId, artifactId, version, extension, classifier ) );
        return this;
    }

    public ArtifactRequestBuilder artifact( final String groupId,
                                            final String artifactId,
                                            final String version,
                                            final String extension )
    {
        setArtifact( new DefaultArtifact( groupId, artifactId, version, extension ) );
        return this;
    }

    public ArtifactRequestBuilder artifact( final String groupId,
                                            final String artifactId,
                                            final String version )
    {
        return artifact( groupId, artifactId, version, "jar" );
    }

    public ArtifactRequestBuilder repository( final RemoteRepository... repositories )
    {
        for ( RemoteRepository repository : repositories )
        {
            addRepository( repository );
        }
        return this;
    }

}
