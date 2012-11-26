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

import javax.inject.Inject;

import org.junit.Test;
import org.sonatype.sisu.litmus.testsupport.inject.InjectedTestSupport;
import org.sonatype.sisu.maven.bridge.MavenModelResolver;
import com.google.inject.Binder;

/**
 * {@link MavenModelResolver} injection related UTs.
 *
 * @since 2.2
 */
public class MavenModelResolverInjectionTest
    extends InjectedTestSupport
{

    @Inject
    private MavenModelResolver resolver;

    @Override
    public void configure( final Binder binder )
    {
        binder.bind( MavenModelResolver.class ).to( RemoteMavenModelResolverUsingSettings.class );
    }

    /**
     * Test that the explicit binding is injected.
     */
    @Test
    public void boundedResolverIsInjected()
    {
        assertThat( resolver, is( instanceOf( RemoteMavenModelResolverUsingSettings.class ) ) );
    }

}
