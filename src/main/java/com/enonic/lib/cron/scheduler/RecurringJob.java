package com.enonic.lib.cron.scheduler;

import java.util.concurrent.ScheduledFuture;

import com.enonic.lib.cron.model.JobDescriptor;

final class RecurringJob
{
    private final ScheduledFuture<?> scheduledFuture;

    private final JobDescriptor descriptor;

    RecurringJob( final ScheduledFuture<?> scheduledFuture, final JobDescriptor descriptor )
    {
        this.scheduledFuture = scheduledFuture;
        this.descriptor = descriptor;
    }

    public JobDescriptor getDescriptor()
    {
        return descriptor;
    }

    public void cancel()
    {
        scheduledFuture.cancel( true );
    }
}
