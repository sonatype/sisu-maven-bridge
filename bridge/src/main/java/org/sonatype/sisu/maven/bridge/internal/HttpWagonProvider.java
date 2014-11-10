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
package org.sonatype.sisu.maven.bridge.internal;

import com.google.common.base.Throwables;
import org.apache.maven.wagon.ConnectionException;
import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.providers.http.HttpWagon;
import org.eclipse.aether.transport.wagon.WagonProvider;

/**
 * Simple http/https wagon provider using wagon-http.
 * 
 * @since 1.0
 */
public class HttpWagonProvider
    implements WagonProvider
{

  @Override
  public Wagon lookup(final String roleHint) throws Exception {
    if ("http".equals(roleHint) || "https".equals(roleHint)) {
      return new HttpWagon();
    }
    return null;
  }

  @Override
  public void release(final Wagon wagon) {
    try {
      wagon.disconnect();
    }
    catch (ConnectionException e) {
      throw Throwables.propagate(e);
    }
  }

}
