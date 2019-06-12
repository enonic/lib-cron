package com.enonic.lib.cron.scheduler;

import java.util.TimerTask;
import java.util.function.Function;

import com.enonic.lib.cron.model.JobDescriptor;
import com.enonic.lib.cron.runner.JobRunner;

final class JobExecutionTask
    extends TimerTask
{
    private final JobDescriptor descriptor;

    private final JobScheduler scheduler;

    private final JobRunner runner;

    private final Function<JobDescriptor, Boolean> runCheckFunction =
        ( JobDescriptor o ) -> o.getTimes().isEmpty() || this.runCount < o.getTimes().get();

    private Integer runCount;

    JobExecutionTask( final JobDescriptor descriptor, final JobScheduler scheduler, final JobRunner runner )
    {
        this.descriptor = descriptor;
        this.scheduler = scheduler;
        this.runner = runner;
        this.runCount = 0;
    }

    public JobExecutionTask( final JobExecutionTask source )
    {
        this.descriptor = source.descriptor;
        this.scheduler = source.scheduler;
        this.runner = source.runner;
        this.runCount = source.runCount;
    }

    @Override
    public void run()
    {

        if ( runCheckFunction.apply( this.descriptor ) )
        {
            this.runner.run( this.descriptor );
            this.runCount++;

            if ( runCheckFunction.apply( this.descriptor ) )
            {
                this.scheduler.reschedule( this.descriptor );
            }
        }
    }

    public JobDescriptor getDescriptor()
    {
        return descriptor;
    }
}
