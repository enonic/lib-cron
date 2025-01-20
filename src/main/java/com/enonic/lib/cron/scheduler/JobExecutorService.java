package com.enonic.lib.cron.scheduler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

@Component(immediate = true, service = JobExecutorService.class)
public class JobExecutorService
{
    private ScheduledExecutorService scheduledExecutorService;

    @Activate
    public void activate( final BundleContext context )
    {
        final Bundle bundle = context.getBundle();
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(
            new ThreadFactoryImpl( bundle.getSymbolicName() + "-" + bundle.getBundleId() + "-job-thread-%d" ) );
    }

    @Deactivate
    public void deactivate()
    {
        this.scheduledExecutorService.shutdownNow();
    }

    public ScheduledFuture<?> scheduleWithFixedDelay( Runnable command, long initialDelay, long delay, TimeUnit unit )
    {
        return this.scheduledExecutorService.scheduleWithFixedDelay( command, initialDelay, delay, unit );
    }

    public ScheduledFuture<?> schedule( Runnable command, long delay, TimeUnit unit )
    {
        return this.scheduledExecutorService.schedule( command, delay, unit );
    }
}
