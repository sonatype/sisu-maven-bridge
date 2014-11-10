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
package org.sonatype.sisu.maven.bridge.support;

import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.resolution.ArtifactRequest;

/**
 * Maven settings.
 * 
 * @author adreghiciu
 * @since 2.0
 */
public interface MavenSettings
{

  ArtifactRequest inject(ArtifactRequest request);

  CollectRequest inject(CollectRequest request);

  RepositorySystemSession inject(RepositorySystemSession session);

}
