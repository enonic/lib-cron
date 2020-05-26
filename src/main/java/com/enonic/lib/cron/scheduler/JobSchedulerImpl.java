package com.enonic.lib.cron.scheduler;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import com.enonic.lib.cron.model.JobDescriptor;
import com.enonic.lib.cron.model.JobDescriptors;
import com.enonic.xp.app.ApplicationKey;

@Component(immediate = true)
public final class JobSchedulerImpl
    implements JobScheduler
{
    private final static Logger LOG = LoggerFactory.getLogger( JobSchedulerImpl.class );

    private RecurringJobScheduler scheduler;

    private Map<JobDescriptor, RecurringJob> tasks;

    @Activate
    public void setup( final BundleContext bundleContext )
    {
        this.scheduler = new RecurringJobScheduler( ApplicationKey.from( bundleContext.getBundle() ) + "-job-thread" );
        this.tasks = Maps.newHashMap();
    }

    @Deactivate
    public void deactivate()
    {
        this.scheduler.shutdownNow();
        this.tasks.clear();
    }

    public void schedule( final JobDescriptors jobs )
    {
        for ( final JobDescriptor job : jobs )
        {
            doSchedule( job );
        }
    }

    public boolean unschedule( final String jobName )
    {
        final Set<JobDescriptor> descriptors = this.tasks.keySet().
            stream().
            filter( jobDescriptor -> jobDescriptor.getName().equals( jobName ) ).
            collect( Collectors.toSet() );

        descriptors.forEach( this::doUnschedule );

        return !descriptors.isEmpty();
    }

    public void schedule( final JobDescriptor job )
    {
        doSchedule( job );
    }

    public void reschedule( final JobDescriptor jobDescriptor )
    {
        doUnschedule( jobDescriptor );
        doSchedule( jobDescriptor );
    }

    public JobDescriptor get( final String jobName )
    {
        return this.tasks.keySet().
            stream().
            filter( desc -> desc.getName().equals( jobName ) ).
            findFirst().
            orElse( null );
    }

    public JobDescriptors list( final String jobNamePattern )
    {
        final JobDescriptors jobDescriptors = new JobDescriptors();

        if ( jobNamePattern == null || jobNamePattern.isBlank() )
        {
            jobDescriptors.addAll( this.tasks.keySet() );
        }
        else
        {
            this.tasks.keySet().
                stream().
                filter( job -> job.getName().matches( jobNamePattern ) ).
                forEach( jobDescriptors::add );
        }
        return jobDescriptors;
    }

    private void doSchedule( final JobDescriptor descriptor )
    {
        final JobExecutionCommand command = new JobExecutionCommand( descriptor, this::rerunCommand, this::removeCommand );

        final RecurringJob recurringJob = this.scheduler.schedule( command, descriptor.nextExecution() );

        this.tasks.put( descriptor, recurringJob );

        LOG.info( "Added job: " + descriptor.getDescription() );
    }

    private void doUnschedule( final JobDescriptor descriptor )
    {
        Optional.ofNullable( tasks.remove( descriptor ) ).ifPresent( RecurringJob::cancel );

        LOG.info( "Job is stopped: " + descriptor.getDescription() );
    }

    private void rerunCommand( final JobExecutionCommand command )
    {
        final RecurringJob recurringJob = this.scheduler.schedule( command, command.getDescriptor().nextExecution() );
        this.tasks.put( command.getDescriptor(), recurringJob );
    }

    private void removeCommand( final JobExecutionCommand command )
    {
        this.tasks.remove( command.getDescriptor() );
    }
}
