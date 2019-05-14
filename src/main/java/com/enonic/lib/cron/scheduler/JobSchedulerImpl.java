package com.enonic.lib.cron.scheduler;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import com.enonic.lib.cron.model.JobDescriptor;
import com.enonic.lib.cron.model.JobDescriptors;
import com.enonic.lib.cron.runner.JobRunner;

@Component(immediate = true)
public final class JobSchedulerImpl
    implements JobScheduler
{
    private final static Logger LOG = LoggerFactory.getLogger( JobSchedulerImpl.class );

    private final Timer timer;

    private final Map<JobDescriptor, TimerTask> tasks;

    private JobRunner runner;

    public JobSchedulerImpl()
    {
        this.timer = new Timer( "JobScheduler" );
        this.tasks = Maps.newHashMap();
    }

    @Deactivate
    public void deactivate()
    {
        this.timer.cancel();
        this.tasks.clear();
    }

    @Override
    public void schedule( final JobDescriptors jobs )
    {
        for ( final JobDescriptor job : jobs )
        {
            schedule( job );
        }
    }

    @Override
    public void unschedule( final String jobName )
    {
        this.tasks.keySet().
            stream().
            filter( jobDescriptor -> jobDescriptor.getName().equals( jobName ) ).
            findFirst().ifPresent(this::unschedule);
    }

    private void unschedule(final JobDescriptor job) {
        final TimerTask task = this.tasks.remove( job );
        task.cancel();

        LOG.info( "Removed job " + job.getDescription() );
    }

    @Override
    public void schedule( final JobDescriptor job )
    {
        final long delay = job.nextExecution().toMillis();
        final JobExecutionTask task = new JobExecutionTask( job, this, this.runner );

        this.tasks.put( job, task );
        this.timer.schedule( task, delay );

        LOG.info( "Added job " + job.getDescription() );
    }

    @Reference
    public void setRunner( final JobRunner runner )
    {
        this.runner = runner;
    }
}
