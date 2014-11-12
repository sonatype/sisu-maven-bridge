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

import org.eclipse.aether.AbstractForwardingRepositorySystemSession;
import org.eclipse.aether.RepositorySystemSession;

/**
 * TODO
 * 
 * @since 1.0
 */
public class RepositorySystemSessionWrapper
    extends AbstractForwardingRepositorySystemSession
{

  private RepositorySystemSession wrapped;

  public RepositorySystemSessionWrapper(final RepositorySystemSession wrapped) {
    this.wrapped = wrapped;
  }

  @Override
  protected RepositorySystemSession getSession() {
    return wrapped;
  }

}
