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

import static org.sonatype.sisu.maven.bridge.support.RemoteRepositoryBuilder.remoteRepository;

import org.apache.maven.model.Model;
import org.apache.maven.model.Repository;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.sonatype.aether.artifact.ArtifactTypeRegistry;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.graph.Dependency;

/**
 * TODO
 *
 * @author adreghiciu
 * @since 2.0
 */
public class CollectRequestBuilder
    extends CollectRequest
{

    private ModelBuildingRequest request;

    private Model model;

    public CollectRequestBuilder()
    {
        setRequestContext( "project" );
    }

    public static CollectRequestBuilder tree()
    {
        return new CollectRequestBuilder();
    }

    public CollectRequestBuilder dependency( final Dependency dependency )
    {
        setRoot( dependency );
        return this;
    }

    public CollectRequestBuilder model( final Model model )
    {
        this.model = model;
        return this;
    }

    public CollectRequestBuilder model( final ModelBuildingRequest request )
    {
        this.request = request;
        return this;
    }

    public ModelBuildingRequest getModelBuildingRequest()
    {
        return request;
    }

    public Model getModel()
    {
        return model;
    }

}
