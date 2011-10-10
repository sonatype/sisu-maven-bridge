package org.sonatype.sisu.maven.bridge.internal.guice;

import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.maven.repository.internal.DefaultServiceLocator;
import org.sonatype.aether.connector.async.AsyncRepositoryConnectorFactory;
import org.sonatype.aether.spi.connector.RepositoryConnectorFactory;
import org.sonatype.aether.spi.locator.ServiceLocator;
import com.google.inject.AbstractModule;

@Named
@Singleton
public class GuiceModule
    extends AbstractModule
{

    @Override
    protected void configure()
    {
        bind( ServiceLocator.class ).toInstance( new DefaultServiceLocator()
        {
            {
                setService( RepositoryConnectorFactory.class, AsyncRepositoryConnectorFactory.class );
            }
        } );
    }

}
