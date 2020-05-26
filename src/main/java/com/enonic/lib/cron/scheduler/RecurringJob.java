package com.enonic.lib.cron.scheduler;

import java.util.concurrent.ScheduledFuture;

final class RecurringJob
{
    private final ScheduledFuture<?> scheduledFuture;

    RecurringJob( final ScheduledFuture<?> scheduledFuture )
    {
        this.scheduledFuture = scheduledFuture;
    }

    public void cancel()
    {
        scheduledFuture.cancel( true );
    }
}
