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
package org.sonatype.sisu.maven.bridge.resolvers;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.maven.model.Repository;
import org.apache.maven.model.building.FileModelSource;
import org.apache.maven.model.building.ModelSource;
import org.apache.maven.model.resolution.InvalidRepositoryException;
import org.apache.maven.model.resolution.ModelResolver;
import org.apache.maven.model.resolution.UnresolvableModelException;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.impl.RemoteRepositoryManager;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.repository.RepositoryPolicy;
import org.sonatype.aether.resolution.ArtifactRequest;
import org.sonatype.aether.resolution.ArtifactResolutionException;
import org.sonatype.aether.util.artifact.DefaultArtifact;

@Singleton
public class RemoteModelResolver
    implements ModelResolver
{

    private final RepositorySystemSession session;

    private List<RemoteRepository> repositories;

    private final RepositorySystem repositorySystem;

    private final RemoteRepositoryManager remoteRepositoryManager;

    private final Set<String> repositoryIds;

    @Inject
    public RemoteModelResolver( final RepositorySystem repositorySystem, final RepositorySystemSession session,
                                final RemoteRepositoryManager remoteRepositoryManager,
                                final List<RemoteRepository> repositories )
    {
        this.session = session;
        this.repositorySystem = repositorySystem;
        this.remoteRepositoryManager = remoteRepositoryManager;
        this.repositories = repositories == null ? new ArrayList<RemoteRepository>() : repositories;
        repositoryIds = new HashSet<String>();
    }

    @Inject
    public RemoteModelResolver( final RepositorySystem repositorySystem, final RepositorySystemSession session,
                                final RemoteRepositoryManager remoteRepositoryManager )
    {
        this( repositorySystem, session, remoteRepositoryManager, null );
    }

    private RemoteModelResolver( final RemoteModelResolver original )
    {
        session = original.session;
        repositorySystem = original.repositorySystem;
        remoteRepositoryManager = original.remoteRepositoryManager;
        repositories = original.repositories;
        repositoryIds = new HashSet<String>( original.repositoryIds );
    }

    public void addRepository( final Repository repository )
        throws InvalidRepositoryException
    {
        if ( !repositoryIds.add( repository.getId() ) )
        {
            return;
        }

        final List<RemoteRepository> newRepositories = Collections.singletonList( convert( repository ) );

        repositories = remoteRepositoryManager.aggregateRepositories( session, repositories, newRepositories, true );
    }

    public ModelResolver newCopy()
    {
        return new RemoteModelResolver( this );
    }

    public ModelSource resolveModel( final String groupId, final String artifactId, final String version )
        throws UnresolvableModelException
    {
        Artifact pomArtifact = new DefaultArtifact( groupId, artifactId, "", "pom", version );

        try
        {
            final ArtifactRequest request = new ArtifactRequest( pomArtifact, repositories, null );
            pomArtifact = repositorySystem.resolveArtifact( session, request ).getArtifact();
        }
        catch ( final ArtifactResolutionException e )
        {
            throw new UnresolvableModelException( e.getMessage(), groupId, artifactId, version, e );
        }

        final File pomFile = pomArtifact.getFile();

        return new FileModelSource( pomFile );
    }

    static RemoteRepository convert( final Repository repository )
    {
        final RemoteRepository result =
            new RemoteRepository( repository.getId(), repository.getLayout(), repository.getUrl() );
        result.setPolicy( true, convert( repository.getSnapshots() ) );
        result.setPolicy( false, convert( repository.getReleases() ) );
        return result;
    }

    private static RepositoryPolicy convert( final org.apache.maven.model.RepositoryPolicy policy )
    {
        boolean enabled = true;
        String checksums = RepositoryPolicy.CHECKSUM_POLICY_WARN;
        String updates = RepositoryPolicy.UPDATE_POLICY_DAILY;

        if ( policy != null )
        {
            enabled = policy.isEnabled();
            if ( policy.getUpdatePolicy() != null )
            {
                updates = policy.getUpdatePolicy();
            }
            if ( policy.getChecksumPolicy() != null )
            {
                checksums = policy.getChecksumPolicy();
            }
        }

        return new RepositoryPolicy( enabled, updates, checksums );
    }

}
