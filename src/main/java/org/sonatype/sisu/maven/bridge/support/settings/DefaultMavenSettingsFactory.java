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

package org.sonatype.sisu.maven.bridge.support.settings;

import static org.sonatype.sisu.maven.bridge.Names.*;

import java.io.File;
import javax.inject.Inject;
import javax.inject.Named;

import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.spi.locator.ServiceLocator;
import org.sonatype.inject.Nullable;
import org.sonatype.sisu.maven.bridge.support.MavenSettings;
import org.sonatype.sisu.maven.bridge.support.MavenSettingsFactory;

/**
 * Maven settings factory.
 *
 * @author adreghiciu
 * @since 2.0
 */
@Named
public class DefaultMavenSettingsFactory
    implements MavenSettingsFactory
{

    private File globalSettings;

    private File userSettings;

    private ServiceLocator serviceLocator;

    private RepositorySystem repositorySystem;

    public DefaultMavenSettingsFactory( final ServiceLocator serviceLocator )
    {
        this.serviceLocator = serviceLocator;
        this.globalSettings = DEFAULT_GLOBAL_SETTINGS_FILE;
        this.userSettings = DEFAULT_USER_SETTINGS_FILE;
    }

    @Inject
    public DefaultMavenSettingsFactory( final ServiceLocator serviceLocator,
                                        final @Nullable @Named( "${" + GLOBAL_SETTINGS + "}" ) File globalSettings,
                                        final @Nullable @Named( "${" + USER_SETTINGS + "}" ) File userSettings )
    {
        this( serviceLocator );
        if ( globalSettings != null && globalSettings.isFile() )
        {
            this.globalSettings = globalSettings;
        }
        if ( userSettings != null && userSettings.isFile() )
        {
            this.userSettings = userSettings;
        }
    }

    @Override
    public MavenSettings create()
    {
        return create( globalSettings );
    }

    @Override
    public MavenSettings create( final File globalSettings )
    {
        return create( globalSettings, userSettings );
    }

    @Override
    public MavenSettings create( final File globalSettings, final File userSettings )
    {
        return new DefaultMavenSettings( globalSettings, userSettings, getRepositorySystem() );
    }

    private RepositorySystem getRepositorySystem()
    {
        if ( repositorySystem == null )
        {
            repositorySystem = serviceLocator.getService( RepositorySystem.class );
        }
        return repositorySystem;
    }

}
