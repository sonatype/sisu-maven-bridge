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

import java.io.File;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.maven.model.Model;
import org.apache.maven.model.Repository;
import org.apache.maven.model.building.DefaultModelBuilderFactory;
import org.apache.maven.model.building.DefaultModelBuildingRequest;
import org.apache.maven.model.building.FileModelSource;
import org.apache.maven.model.building.ModelBuilder;
import org.apache.maven.model.building.ModelBuildingException;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelBuildingResult;
import org.apache.maven.model.resolution.InvalidRepositoryException;
import org.apache.maven.model.resolution.ModelResolver;
import org.slf4j.Logger;
import org.sonatype.sisu.maven.bridge.MavenBridge;

@Named
@Singleton
class DefaultMavenBridge
    implements MavenBridge
{

    private final ModelBuilder modelBuilder;

    private final ModelResolver modelResolver;

    @Inject
    private Logger logger;

    @Inject
    DefaultMavenBridge( ModelResolver modelResolver )
    {
        this.modelResolver = modelResolver;

        this.modelBuilder = new DefaultModelBuilderFactory().newInstance();
    }

    public Model buildModel( File pom, Repository... repositories )
        throws ModelBuildingException
    {
        ModelResolver mr = modelResolver.newCopy();
        if ( repositories != null )
        {
            for ( Repository repository : repositories )
            {
                try
                {
                    mr.addRepository( repository );
                }
                catch ( InvalidRepositoryException e )
                {
                    logger.warn( String.format( "Could not use repository [%s]", repository.getUrl() ), e );
                }
            }
        }

        ModelBuildingRequest modelRequest = new DefaultModelBuildingRequest();
        modelRequest.setModelSource( new FileModelSource( pom ) );
        modelRequest.setSystemProperties( System.getProperties() );
        modelRequest.setValidationLevel( ModelBuildingRequest.VALIDATION_LEVEL_MINIMAL );
        modelRequest.setLocationTracking( false );
        modelRequest.setProcessPlugins( false );
        modelRequest.setModelResolver( mr );

        ModelBuildingResult modelResult = modelBuilder.build( modelRequest );
        Model model = modelResult.getEffectiveModel();
        return model;
    }

}
