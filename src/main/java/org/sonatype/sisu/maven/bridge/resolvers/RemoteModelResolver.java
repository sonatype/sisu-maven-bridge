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

import static java.util.Arrays.asList;
import static org.sonatype.sisu.maven.bridge.support.RemoteRepositoryBuilder.remoteRepositories;

import java.io.File;
import java.util.ArrayList;
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
import org.sonatype.aether.resolution.ArtifactRequest;
import org.sonatype.aether.resolution.ArtifactResolutionException;
import org.sonatype.aether.util.artifact.DefaultArtifact;

@Singleton
public class RemoteModelResolver
    implements ModelResolver
{

    static final boolean RECESSIVE_IS_RAW = true;

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

        repositories = remoteRepositoryManager.aggregateRepositories(
            session, repositories, asList( remoteRepositories( repository ) ), RECESSIVE_IS_RAW
        );
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

}
