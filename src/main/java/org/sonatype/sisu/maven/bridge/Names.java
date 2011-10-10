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

import java.io.File;

public interface Names
{

    static final String LOCAL_MODEL_RESOLVER_ROOT_DIR = "maven.localModelResolver.rootDir";

    static final String LOCAL_ARTIFACT_RESOLVER_ROOT_DIR = "maven.localArtifactResolver.rootDir";

    static final String LOCAL_REPOSITORY_DIR = "maven.localRepository";

    static final String UPDATE_POLICY = "maven.updatePolicy";

    static final String CHECKSUM_POLICY = "maven.checksumPolicy";

    static final String GLOBAL_SETTINGS = "maven.globalSettings";

    static final String USER_SETTINGS = "maven.userSettings";

    static final String USER_HOME = System.getProperty( "user.home" );

    static final String USER_DIR = System.getProperty( "user.dir", "" );

    static final String MAVEN_HOME = System.getProperty( "maven.home", USER_DIR );

    static final File MAVEN_USER_HOME = new File( USER_HOME, ".m2" );

    static final File DEFAULT_USER_SETTINGS_FILE = new File( MAVEN_USER_HOME, "settings.xml" );

    static final File DEFAULT_GLOBAL_SETTINGS_FILE = new File( MAVEN_HOME, "conf/settings.xml" );

}
