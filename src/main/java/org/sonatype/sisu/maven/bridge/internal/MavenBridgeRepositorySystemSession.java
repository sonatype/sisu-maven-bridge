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

import static org.sonatype.sisu.maven.bridge.Names.*;

import java.io.File;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.repository.internal.MavenRepositorySystemSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.aether.AbstractRepositoryListener;
import org.sonatype.aether.RepositoryEvent;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.repository.LocalRepository;
import org.sonatype.aether.transfer.AbstractTransferListener;
import org.sonatype.aether.transfer.TransferCancelledException;
import org.sonatype.aether.transfer.TransferEvent;
import org.sonatype.inject.Nullable;

/**
 * TODO
 *
 * @author adreghiciu
 * @since 2.0
 */
@Named
public class MavenBridgeRepositorySystemSession
    extends MavenRepositorySystemSession
    implements RepositorySystemSession
{

    private Logger log;

    private final RepositorySystem repositorySystem;

    @Inject
    public MavenBridgeRepositorySystemSession( final RepositorySystem repositorySystem )
    {
        this.repositorySystem = repositorySystem;
        setRepositoryListener( new AbstractRepositoryListener()
        {
            @Override
            public void artifactInstalling( final RepositoryEvent event )
            {
                log().info( "Installing " + event.getArtifact().getFile() + " to " + event.getFile() );
            }

            @Override
            public void metadataInstalling( final RepositoryEvent event )
            {
                log().debug( "Installing " + event.getMetadata() + " to " + event.getFile() );
            }

            @Override
            public void artifactDescriptorInvalid( final RepositoryEvent event )
            {
                if ( log().isDebugEnabled() )
                {
                    log().warn( "The POM for " + event.getArtifact() + " is invalid"
                                    + ", transitive dependencies (if any) will not be available: "
                                    + event.getException().getMessage() );
                }
                else
                {
                    log().warn( "The POM for " + event.getArtifact() + " is invalid"
                                    + ", transitive dependencies (if any) will not be available"
                                    + ", enable debug logging for more details" );
                }
            }

            @Override
            public void artifactDescriptorMissing( final RepositoryEvent event )
            {
                log().warn( "The POM for " + event.getArtifact()
                                + " is missing, no dependency information available" );
            }
        } );
        setTransferListener( new AbstractTransferListener()
        {
            private ThreadLocal<Long> last = new ThreadLocal<Long>();

            @Override
            public void transferInitiated( TransferEvent event )
                throws TransferCancelledException
            {
                log().info( "Downloading {}{}...", event.getResource().getRepositoryUrl(),
                            event.getResource().getResourceName() );
            }

            @Override
            public void transferSucceeded( TransferEvent event )
            {
                log().info( "Downloaded [{} bytes] {}{}", new Object[]{ event.getTransferredBytes(),
                    event.getResource().getRepositoryUrl(), event.getResource().getResourceName() } );
            }

            @Override
            public void transferFailed( TransferEvent event )
            {
                log().error( "Failed to download {}{}", new Object[]{ event.getResource().getRepositoryUrl(),
                    event.getResource().getResourceName(), event.getException() } );
            }

            @Override
            public void transferProgressed( TransferEvent event )
                throws TransferCancelledException
            {
                Long last = this.last.get();
                if ( last == null || last.longValue() < System.currentTimeMillis() - 5 * 1000 )
                {
                    String progress;
                    if ( event.getResource().getContentLength() > 0 )
                    {
                        progress =
                            (int) ( event.getTransferredBytes() * 100.0 / event.getResource().getContentLength() )
                                + "%";
                    }
                    else
                    {
                        progress = event.getTransferredBytes() + " bytes";
                    }
                    log().debug( "Downloading [{}] {}{}...",
                                 new Object[]{ progress, event.getResource().getRepositoryUrl(),
                                     event.getResource().getResourceName() } );
                    this.last.set( System.currentTimeMillis() );
                }
            }

        } );
    }

    @Inject
    public void setLocalRepository( final @Nullable @Named( LOCAL_REPOSITORY_DIR ) File localRepository )
    {
        if ( localRepository != null )
        {
            setLocalRepositoryManager( repositorySystem.newLocalRepositoryManager(
                new LocalRepository( localRepository ) )
            );
        }
    }

    @Inject
    void injectUpdatePolicy( final @Named( "${" + UPDATE_POLICY + ":-daily}" ) String updatePolicy )
    {
        super.setUpdatePolicy( updatePolicy );
    }

    @Inject
    void injectChecksumPolicy( final @Named( "${" + CHECKSUM_POLICY + ":-fail}" ) String checksumPolicy )
    {
        super.setChecksumPolicy( checksumPolicy );
    }

    protected Logger log()
    {
        if ( log == null )
        {
            log = LoggerFactory.getLogger( this.getClass() );
        }
        return log;
    }

}
