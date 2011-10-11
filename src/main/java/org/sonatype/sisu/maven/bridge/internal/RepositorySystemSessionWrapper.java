package org.sonatype.sisu.maven.bridge.internal;

import java.util.Map;

import org.sonatype.aether.RepositoryCache;
import org.sonatype.aether.RepositoryListener;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.SessionData;
import org.sonatype.aether.artifact.ArtifactTypeRegistry;
import org.sonatype.aether.collection.DependencyGraphTransformer;
import org.sonatype.aether.collection.DependencyManager;
import org.sonatype.aether.collection.DependencySelector;
import org.sonatype.aether.collection.DependencyTraverser;
import org.sonatype.aether.repository.AuthenticationSelector;
import org.sonatype.aether.repository.LocalRepository;
import org.sonatype.aether.repository.LocalRepositoryManager;
import org.sonatype.aether.repository.MirrorSelector;
import org.sonatype.aether.repository.ProxySelector;
import org.sonatype.aether.repository.WorkspaceReader;
import org.sonatype.aether.transfer.TransferListener;

/**
 * TODO
 *
 * @since 1.0
 */
public class RepositorySystemSessionWrapper
    implements RepositorySystemSession
{

    private RepositorySystemSession wrapped;

    public RepositorySystemSessionWrapper( final RepositorySystemSession wrapped )
    {
        this.wrapped = wrapped;
    }


    @Override
    public ArtifactTypeRegistry getArtifactTypeRegistry()
    {
        return wrapped.getArtifactTypeRegistry();
    }

    @Override
    public AuthenticationSelector getAuthenticationSelector()
    {
        return wrapped.getAuthenticationSelector();
    }

    @Override
    public RepositoryCache getCache()
    {
        return wrapped.getCache();
    }

    @Override
    public String getChecksumPolicy()
    {
        return wrapped.getChecksumPolicy();
    }

    @Override
    public Map<String, Object> getConfigProperties()
    {
        return wrapped.getConfigProperties();
    }

    @Override
    public SessionData getData()
    {
        return wrapped.getData();
    }

    @Override
    public DependencyGraphTransformer getDependencyGraphTransformer()
    {
        return wrapped.getDependencyGraphTransformer();
    }

    @Override
    public DependencyManager getDependencyManager()
    {
        return wrapped.getDependencyManager();
    }

    @Override
    public DependencySelector getDependencySelector()
    {
        return wrapped.getDependencySelector();
    }

    @Override
    public DependencyTraverser getDependencyTraverser()
    {
        return wrapped.getDependencyTraverser();
    }

    @Override
    public LocalRepository getLocalRepository()
    {
        return wrapped.getLocalRepository();
    }

    @Override
    public LocalRepositoryManager getLocalRepositoryManager()
    {
        return wrapped.getLocalRepositoryManager();
    }

    @Override
    public MirrorSelector getMirrorSelector()
    {
        return wrapped.getMirrorSelector();
    }

    @Override
    public ProxySelector getProxySelector()
    {
        return wrapped.getProxySelector();
    }

    @Override
    public RepositoryListener getRepositoryListener()
    {
        return wrapped.getRepositoryListener();
    }

    @Override
    public Map<String, String> getSystemProperties()
    {
        return wrapped.getSystemProperties();
    }

    @Override
    public TransferListener getTransferListener()
    {
        return wrapped.getTransferListener();
    }

    @Override
    public String getUpdatePolicy()
    {
        return wrapped.getUpdatePolicy();
    }

    @Override
    public Map<String, String> getUserProperties()
    {
        return wrapped.getUserProperties();
    }

    @Override
    public WorkspaceReader getWorkspaceReader()
    {
        return wrapped.getWorkspaceReader();
    }

    @Override
    public boolean isIgnoreInvalidArtifactDescriptor()
    {
        return wrapped.isIgnoreInvalidArtifactDescriptor();
    }

    @Override
    public boolean isIgnoreMissingArtifactDescriptor()
    {
        return wrapped.isIgnoreMissingArtifactDescriptor();
    }

    @Override
    public boolean isNotFoundCachingEnabled()
    {
        return wrapped.isNotFoundCachingEnabled();
    }

    @Override
    public boolean isOffline()
    {
        return wrapped.isOffline();
    }

    @Override
    public boolean isTransferErrorCachingEnabled()
    {
        return wrapped.isTransferErrorCachingEnabled();
    }

}
