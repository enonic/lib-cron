package com.enonic.lib.cron.scheduler;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.apache.felix.framework.Felix;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

public abstract class BundleBasedTest
{
    private static final int FELIX_STOP_WAIT_TIMEOUT_MS = 10000;

    @TempDir
    public Path temporaryFolder;

    private Felix felix;

    @BeforeEach
    public void setup()
        throws Exception
    {
        final Path cacheDir = Files.createDirectory( this.temporaryFolder.resolve( "cache" ) ).toAbsolutePath();

        final Map<String, Object> config = new HashMap<>();
        config.put( Constants.FRAMEWORK_STORAGE, cacheDir.toString() );
        config.put( Constants.FRAMEWORK_STORAGE_CLEAN, Constants.FRAMEWORK_STORAGE_CLEAN_ONFIRSTINIT );

        this.felix = new Felix( config );
        this.felix.start();
    }

    protected final BundleContext getBundleContext()
    {
        return this.felix.getBundleContext();
    }

    @AfterEach
    public final void destroy()
        throws Exception
    {
        this.felix.stop();
        this.felix.waitForStop( FELIX_STOP_WAIT_TIMEOUT_MS );
    }

}
