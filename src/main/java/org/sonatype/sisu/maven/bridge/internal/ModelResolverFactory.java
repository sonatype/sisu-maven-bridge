package org.sonatype.sisu.maven.bridge.internal;

import org.apache.maven.model.resolution.ModelResolver;
import org.sonatype.aether.RepositorySystemSession;

public interface ModelResolverFactory
{
    ModelResolver getModelResolver( RepositorySystemSession session );
}
