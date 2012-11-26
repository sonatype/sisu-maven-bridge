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
package org.sonatype.sisu.maven.bridge.support.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.sonatype.sisu.maven.bridge.support.ArtifactRequestBuilder.request;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.Test;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.resolution.ArtifactResolutionException;
import org.sonatype.sisu.litmus.testsupport.inject.InjectedTestSupport;
import org.sonatype.sisu.maven.bridge.MavenArtifactResolver;
import org.sonatype.sisu.maven.bridge.support.artifact.RemoteMavenArtifactResolverUsingSettings;

/**
 * {@link RemoteMavenArtifactResolverUsingSettings} related UTs.
 *
 * @since 2.0
 */
public class RemoteMavenArtifactResolverUsingSettingsTest
    extends InjectedTestSupport
{

    @Inject
    @Named( "remote-artifact-resolver-using-settings" )
    private MavenArtifactResolver resolver;

    @Test
    public void resolve()
        throws ArtifactResolutionException
    {
        assertThat( resolver, is( instanceOf( RemoteMavenArtifactResolverUsingSettings.class ) ) );
        final Artifact artifact = resolver.resolveArtifact(
            request().artifact( "org.sonatype.aether:aether-api:1.9" )
        );
        assertThat( artifact, is( notNullValue() ) );
    }

}
