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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.sonatype.sisu.maven.bridge.support.CollectRequestBuilder.tree;
import static org.sonatype.sisu.maven.bridge.support.ModelBuildingRequestBuilder.model;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.Test;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.sisu.litmus.testsupport.inject.InjectedTestSupport;
import org.sonatype.sisu.maven.bridge.MavenDependencyTreeResolver;
import org.sonatype.sisu.maven.bridge.support.dependency.RemoteMavenDependencyTreeResolverUsingSettings;

/**
 * {@link RemoteMavenDependencyTreeResolverUsingSettingsTest} related UTs.
 *
 * @since 2.0
 */
public class RemoteMavenDependencyTreeResolverUsingSettingsTest
    extends InjectedTestSupport
{

    @Inject
    @Named( "remote-dependency-tree-resolver-using-settings" )
    private MavenDependencyTreeResolver resolver;

    @Test
    public void resolve()
        throws Exception
    {
        assertThat( resolver, is( instanceOf( RemoteMavenDependencyTreeResolverUsingSettings.class ) ) );
        DependencyNode node = resolver.resolveDependencyTree(
            tree().model( model().pom( "org.sonatype.aether:aether-impl:1.9" ) )
        );
        assertThat( node, is( notNullValue() ) );
        assertThat( node.getChildren(), is( notNullValue() ) );
        assertThat( node.getChildren().size(), is( equalTo( 8 ) ) );
    }

}
