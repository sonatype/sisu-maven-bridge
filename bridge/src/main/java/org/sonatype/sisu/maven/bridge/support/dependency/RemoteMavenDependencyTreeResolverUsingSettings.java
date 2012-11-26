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
package org.sonatype.sisu.maven.bridge.support.dependency;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.collection.DependencyCollectionException;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.spi.locator.ServiceLocator;
import org.sonatype.inject.Nullable;
import org.sonatype.sisu.maven.bridge.MavenDependencyTreeResolver;
import org.sonatype.sisu.maven.bridge.support.MavenSettings;
import org.sonatype.sisu.maven.bridge.support.MavenSettingsFactory;
import org.sonatype.sisu.maven.bridge.support.model.RemoteMavenModelResolverUsingSettings;

@Named( "remote-dependency-tree-resolver-using-settings" )
@Singleton
public class RemoteMavenDependencyTreeResolverUsingSettings
    extends RemoteMavenDependencyTreeResolver
    implements MavenDependencyTreeResolver
{

    private final MavenSettingsFactory mavenSettingsFactory;

    public RemoteMavenDependencyTreeResolverUsingSettings( final ServiceLocator serviceLocator,
                                                           final MavenSettingsFactory mavenSettingsFactory,
                                                           final @Nullable RemoteMavenModelResolverUsingSettings mavenModelResolver )
    {
        this( serviceLocator, mavenSettingsFactory, mavenModelResolver, NO_SESSION_PROVIDER );
    }

    @Inject
    public RemoteMavenDependencyTreeResolverUsingSettings( final ServiceLocator serviceLocator,
                                                           final MavenSettingsFactory mavenSettingsFactory,
                                                           final @Nullable RemoteMavenModelResolverUsingSettings mavenModelResolver,
                                                           final @Nullable Provider<RepositorySystemSession> sessionProvider )
    {
        super( serviceLocator, mavenModelResolver, sessionProvider );
        this.mavenSettingsFactory = mavenSettingsFactory;
    }

    @Override
    public DependencyNode resolveDependencyTree( final CollectRequest request,
                                                 final RepositorySystemSession session,
                                                 final RemoteRepository... repositories )
        throws DependencyCollectionException
    {
        final MavenSettings mavenSettings = mavenSettingsFactory.create();
        return super.resolveDependencyTree(
            mavenSettings.inject( request ), mavenSettings.inject( session ), repositories
        );
    }

}
