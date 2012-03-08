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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.sonatype.sisu.maven.bridge.support.ModelBuildingRequestBuilder.model;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.model.Model;
import org.apache.maven.model.building.ModelBuildingException;
import org.junit.Test;
import org.sonatype.sisu.litmus.testsupport.inject.InjectedTestSupport;
import org.sonatype.sisu.maven.bridge.MavenModelResolver;

/**
 * {@link RemoteMavenModelResolverUsingSettings} related UTs.
 *
 * @since 2.0
 */
public class RemoteMavenModelResolverUsingSettingsTest
    extends InjectedTestSupport
{

    @Inject
    @Named( "remote-model-resolver-using-settings" )
    private MavenModelResolver resolver;

    /**
     * Test that resolver will resolve the effective model.
     *
     * @throws ModelBuildingException - Unexpected
     */
    @Test
    public void resolve()
        throws ModelBuildingException
    {
        assertThat( resolver, is( instanceOf( RemoteMavenModelResolverUsingSettings.class ) ) );
        final Model model = resolver.resolveModel( model().pom( "org.sonatype.aether:aether-api:1.9" ) );
        assertThat( model, is( notNullValue() ) );
        // if the following passes means that the parent was resolved and effective model was build
        // as inception year is set in aether-parent
        assertThat( model.getInceptionYear(), is( equalTo( "2010" ) ) );
    }

}
