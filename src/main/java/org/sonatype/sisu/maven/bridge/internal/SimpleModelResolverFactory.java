package org.sonatype.sisu.maven.bridge.internal;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.maven.model.resolution.ModelResolver;
import org.sonatype.aether.RepositorySystemSession;

@Singleton
public class SimpleModelResolverFactory
    implements ModelResolverFactory
{
    private final ModelResolver modelResolver;

    @Inject
    public SimpleModelResolverFactory( final ModelResolver modelResolver )
    {
        this.modelResolver = modelResolver;
    }

    public ModelResolver getModelResolver( RepositorySystemSession session )
    {
        return modelResolver.newCopy();
    }
}
