package com.enonic.lib.cron.scheduler;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.enonic.lib.cron.model.JobDescriptor;

final class RecurringJobScheduler
{
    private final ScheduledExecutorService scheduledExecutorService;

    public RecurringJobScheduler( final String namePattern )
    {
        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor( new ThreadFactoryImpl( namePattern ) );
    }

    public RecurringJob schedule( final JobExecutionCommand command )
    {
        final JobDescriptor descriptor = command.getDescriptor();

        final ScheduledFuture<?> scheduledFuture = descriptor.getFixedDelay() > 0
            ? doScheduleWithFixedDelay( command, descriptor.getDelay(), descriptor.getFixedDelay() )
            : doSchedule( command, descriptor.nextExecution() );

        return new RecurringJob( scheduledFuture );
    }

    private ScheduledFuture<?> doScheduleWithFixedDelay( final JobExecutionCommand command, final Integer delay, final Integer fixedDelay )
    {
        return this.scheduledExecutorService.scheduleWithFixedDelay( command, delay, fixedDelay, TimeUnit.MILLISECONDS );
    }

    private ScheduledFuture<?> doSchedule( final JobExecutionCommand command, final Duration delay )
    {
        return this.scheduledExecutorService.schedule( command, delay.toMillis(), TimeUnit.MILLISECONDS );
    }

    public List<Runnable> shutdownNow()
    {
        return this.scheduledExecutorService.shutdownNow();
    }
}
