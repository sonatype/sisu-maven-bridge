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

import static java.util.Arrays.asList;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.collection.DependencyCollectionException;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.aether.repository.LocalRepository;
import org.sonatype.aether.repository.LocalRepositoryManager;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.spi.locator.ServiceLocator;
import org.sonatype.inject.Nullable;
import org.sonatype.sisu.maven.bridge.MavenDependencyTreeResolver;
import org.sonatype.sisu.maven.bridge.Names;
import org.sonatype.sisu.maven.bridge.internal.RepositorySystemSessionWrapper;
import org.sonatype.sisu.maven.bridge.support.dependency.internal.MavenDependencyTreeResolverSupport;
import org.sonatype.sisu.maven.bridge.support.model.RemoteMavenModelResolver;

@Named( "remote-dependency-tree-resolver" )
@Singleton
public class RemoteMavenDependencyTreeResolver
    extends MavenDependencyTreeResolverSupport
    implements MavenDependencyTreeResolver
{

    private static final boolean RECESSIVE_IS_RAW = true;

    public RemoteMavenDependencyTreeResolver( final ServiceLocator serviceLocator,
                                              final @Nullable RemoteMavenModelResolver mavenModelResolver )
    {
        super( serviceLocator, mavenModelResolver );
    }

    @Inject
    public RemoteMavenDependencyTreeResolver( final ServiceLocator serviceLocator,
                                              final @Nullable RemoteMavenModelResolver mavenModelResolver,
                                              final @Nullable Provider<RepositorySystemSession> sessionProvider )
    {
        super( serviceLocator, mavenModelResolver, sessionProvider );
    }

    @Override
    public DependencyNode resolveDependencyTree( final CollectRequest request,
                                                 final RepositorySystemSession session,
                                                 final RemoteRepository... repositories )
        throws DependencyCollectionException
    {
        final List<RemoteRepository> allRepositories = new ArrayList<RemoteRepository>();
        if ( repositories != null && repositories.length > 0 )
        {
            allRepositories.addAll( asList( repositories ) );
        }
        allRepositories.addAll( request.getRepositories() );

        request.setRepositories(
            getRemoteRepositoryManager().aggregateRepositories(
                session, Collections.<RemoteRepository>emptyList(), allRepositories, RECESSIVE_IS_RAW
            )
        );

        RepositorySystemSession safeSession = session;
        if ( session.getLocalRepositoryManager() == null || session.getLocalRepository() == null )
        {
            safeSession = new RepositorySystemSessionWrapper( session )
            {
                final LocalRepositoryManager lrm = getRepositorySystem().newLocalRepositoryManager(
                    new LocalRepository( new File( Names.MAVEN_USER_HOME, "repository" ) )
                );

                public LocalRepositoryManager getLocalRepositoryManager()
                {
                    return lrm;
                }

                public LocalRepository getLocalRepository()
                {
                    return lrm.getRepository();
                }
            };
        }

        return super.resolveDependencyTree( request, safeSession, repositories );
    }

}
