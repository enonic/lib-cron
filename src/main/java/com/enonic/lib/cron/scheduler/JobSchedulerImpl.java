package com.enonic.lib.cron.scheduler;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

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
import com.enonic.lib.cron.service.mapper.JobDescriptorMapper;
import com.enonic.xp.app.ApplicationKey;

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
            findFirst().ifPresent( this::doUnschedule );
    }

    @Override
    public void unscheduleByKey( final ApplicationKey key )
    {
        this.tasks.keySet().
            stream().
            filter( jobDescriptor -> jobDescriptor.getApplicationKey().equals( key ) ).
            findFirst().ifPresent( this::doUnschedule );
    }


    @Override
    public void schedule( final JobDescriptor job )
    {
        doSchedule( job );
        LOG.info( "Added job: " + job.getDescription() );
    }

    @Override
    public void reschedule( final JobDescriptor job )
    {
        doSchedule( job );
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

    private void doUnschedule( final JobDescriptor job )
    {
        final TimerTask task = this.tasks.remove( job );
        task.cancel();
    }

    @Reference
    public void setRunner( final JobRunner runner )
    {
        this.runner = runner;
    }
}
