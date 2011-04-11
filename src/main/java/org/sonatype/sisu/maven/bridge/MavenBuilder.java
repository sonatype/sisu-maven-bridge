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
package org.sonatype.sisu.maven.bridge;

import org.apache.maven.model.Repository;
import org.apache.maven.model.RepositoryPolicy;

public class MavenBuilder
{

    public static Repository repository( final String id, final String url )
    {
        Repository repository = new Repository();
        repository.setId( id );
        repository.setLayout( "default" );
        repository.setUrl( url );

        RepositoryPolicy policy = new RepositoryPolicy();
        policy.setChecksumPolicy( "warn" );
        policy.setEnabled( true );
        policy.setUpdatePolicy( "always" );
        repository.setReleases( policy );
        repository.setSnapshots( policy );

        return repository;
    }

}
