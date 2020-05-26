package com.enonic.lib.cron.scheduler;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

final class RecurringJobScheduler
{
    private final ScheduledExecutorService scheduledExecutorService;

    public RecurringJobScheduler( final String namePattern )
    {
        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor( new ThreadFactoryImpl( namePattern ) );
    }

    public RecurringJob schedule( final JobExecutionCommand command, final Duration delay )
    {
        final ScheduledFuture<?> scheduledFuture =
            this.scheduledExecutorService.schedule( command, delay.toMillis(), TimeUnit.MILLISECONDS );

        return new RecurringJob( scheduledFuture );
    }

    public List<Runnable> shutdownNow()
    {
        return this.scheduledExecutorService.shutdownNow();
    }
}
