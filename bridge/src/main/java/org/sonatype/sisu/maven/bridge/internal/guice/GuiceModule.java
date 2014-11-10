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

import org.sonatype.sisu.maven.bridge.internal.HttpWagonProvider;

import com.google.inject.AbstractModule;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.spi.locator.ServiceLocator;
import org.eclipse.aether.transport.wagon.WagonProvider;
import org.eclipse.aether.transport.wagon.WagonTransporterFactory;

@Named
public class GuiceModule
    extends AbstractModule
{

  @Override
  protected void configure() {
    bind(ServiceLocator.class).toInstance(
        MavenRepositorySystemUtils.newServiceLocator()
            .setService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class)
            .setService(TransporterFactory.class, WagonTransporterFactory.class)
            .setService(WagonProvider.class, HttpWagonProvider.class));
  }

}
