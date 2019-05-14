package com.enonic.lib.cron.scheduler;

import java.util.TimerTask;

import com.enonic.lib.cron.model.JobDescriptor;
import com.enonic.lib.cron.runner.JobRunner;

final class JobExecutionTask
    extends TimerTask
{
    private final JobDescriptor descriptor;

    private final JobScheduler scheduler;

    private final JobRunner runner;

    JobExecutionTask( final JobDescriptor descriptor, final JobScheduler scheduler, final JobRunner runner )
    {
        this.descriptor = descriptor;
        this.scheduler = scheduler;
        this.runner = runner;
    }

    @Override
    public void run()
    {
        this.runner.run( this.descriptor );
        this.scheduler.schedule( this.descriptor );
    }
}
