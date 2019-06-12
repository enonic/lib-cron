package com.enonic.lib.cron.scheduler;

import java.util.Map;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import com.enonic.lib.cron.model.JobDescriptor;
import com.enonic.lib.cron.model.JobDescriptors;
import com.enonic.lib.cron.runner.JobRunner;
import com.enonic.xp.app.ApplicationKey;

@Component(immediate = true)
public final class JobSchedulerImpl
    implements JobScheduler
{
    private final static Logger LOG = LoggerFactory.getLogger( JobSchedulerImpl.class );

    private final Timer timer;

    private final Map<JobDescriptor, JobExecutionTask> tasks;

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
    public boolean unschedule( final String jobName )
    {
        final Optional<JobDescriptor> job = this.tasks.keySet().
            stream().
            filter( jobDescriptor -> jobDescriptor.getName().equals( jobName ) ).
            findFirst();

        return job.filter( this::doUnschedule ).isPresent();
    }

    @Override
    public boolean unscheduleByKey( final ApplicationKey key )
    {
        final Optional<JobDescriptor> job = this.tasks.keySet().
            stream().
            filter( jobDescriptor -> jobDescriptor.getApplicationKey().equals( key ) ).
            findFirst();

        return job.filter( this::doUnschedule ).isPresent();

    }

    @Override
    public void schedule( final JobDescriptor job )
    {
        doSchedule( job );
        LOG.info( "Added job: " + job.getDescription() );
    }

    @Override
    public void reschedule( final JobDescriptor jobDescriptor )
    {
        final JobExecutionTask task = tasks.get( jobDescriptor );

        if ( task == null )
        {
            LOG.warn( "Can't reschedule task: " + jobDescriptor.getName() );
            return;
        }

        final long delay = task.getDescriptor().nextExecution().toMillis();

        final JobExecutionTask newTask = new JobExecutionTask( task );

        this.tasks.put( jobDescriptor, newTask );
        this.timer.schedule( newTask, delay );
    }

    @Override
    public JobDescriptor get( final String jobName )
    {
        final Optional<JobDescriptor> jobDescriptor = this.tasks.keySet().
            stream().
            filter( desc -> desc.getName().equals( jobName ) ).
            findFirst();

        return jobDescriptor.orElse( null );

    }

    @Override
    public JobDescriptors list( final String jobNamePattern )
    {
        final JobDescriptors jobDescriptors = new JobDescriptors();

        this.tasks.keySet().
            stream().
            filter( job -> StringUtils.isBlank( jobNamePattern ) || job.getName().matches( jobNamePattern ) ).
            forEach( jobDescriptors::add );

        return jobDescriptors;
    }

    private void doSchedule( final JobDescriptor job )
    {
        final long delay = job.nextExecution().toMillis();
        final JobExecutionTask task = new JobExecutionTask( job, this, this.runner );

        this.tasks.put( job, task );
        this.timer.schedule( task, delay );

    }

    private boolean doUnschedule( final JobDescriptor job )
    {
        final JobExecutionTask task = this.tasks.remove( job );
        final boolean result = task.cancel();

        if ( result )
        {
            LOG.info( "Removed job: " + job.getDescription() );
        }
        else
        {
            LOG.warn( "Can't remove job: " + job.getDescription() );
        }

        return result;
    }

    @Reference
    public void setRunner( final JobRunner runner )
    {
        this.runner = runner;
    }
}
