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

import javax.inject.Inject;
import javax.inject.Named;

import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.spi.locator.ServiceLocator;

/**
 * TODO
 *
 * @author adreghiciu
 * @since 2.0
 */
@Named
public class DefaultRepositorySystemSession
    extends MavenBridgeRepositorySystemSession
{

    @Inject
    public DefaultRepositorySystemSession( final ServiceLocator serviceLocator )
    {
        super( serviceLocator.getService( RepositorySystem.class ) );
    }

}
