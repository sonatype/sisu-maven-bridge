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
package org.sonatype.sisu.maven.bridge.internal.guice;

import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.maven.repository.internal.DefaultServiceLocator;
import org.sonatype.aether.connector.wagon.WagonProvider;
import org.sonatype.aether.connector.wagon.WagonRepositoryConnectorFactory;
import org.sonatype.aether.spi.connector.RepositoryConnectorFactory;
import org.sonatype.aether.spi.locator.ServiceLocator;
import org.sonatype.sisu.maven.bridge.internal.HttpWagonProvider;
import com.google.inject.AbstractModule;

@Named
@Singleton
public class GuiceModule
    extends AbstractModule
{

    @Override
    protected void configure()
    {
        bind( ServiceLocator.class ).toInstance( new DefaultServiceLocator()
        {
            {
                setService( RepositoryConnectorFactory.class, WagonRepositoryConnectorFactory.class );
                setService( WagonProvider.class, HttpWagonProvider.class );
            }
        } );
    }

}
