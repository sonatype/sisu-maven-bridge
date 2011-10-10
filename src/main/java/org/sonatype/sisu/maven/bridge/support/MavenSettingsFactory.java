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
package org.sonatype.sisu.maven.bridge.support;

import java.io.File;

/**
 * Maven settings factory.
 *
 * @author adreghiciu
 * @since 2.0
 */
public interface MavenSettingsFactory
{

    MavenSettings create();

    MavenSettings create( File globalSettings );

    MavenSettings create( File globalSettings, File userSettings );

}
