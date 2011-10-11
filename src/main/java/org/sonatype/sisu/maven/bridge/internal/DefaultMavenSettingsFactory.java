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

    @Inject
    DefaultMavenSettingsFactory( final @Nullable @Named( "${" + GLOBAL_SETTINGS + "}" ) File globalSettings,
                                 final @Nullable @Named( "${" + USER_SETTINGS + "}" ) File userSettings,
                                 final ServiceLocator serviceLocator )
    {
        this.serviceLocator = serviceLocator;
        this.globalSettings =
            globalSettings != null && globalSettings.isFile() ? globalSettings : DEFAULT_GLOBAL_SETTINGS_FILE;
        this.userSettings =
            userSettings != null && userSettings.isFile() ? userSettings : DEFAULT_USER_SETTINGS_FILE;
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
