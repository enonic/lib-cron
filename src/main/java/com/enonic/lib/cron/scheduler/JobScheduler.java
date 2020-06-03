package com.enonic.lib.cron.scheduler;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.lib.cron.model.JobDescriptor;
import com.enonic.xp.app.ApplicationKey;

public final class JobScheduler
{
    private final static Logger LOG = LoggerFactory.getLogger( JobScheduler.class );

    private final RecurringJobScheduler scheduler;

    private final Map<JobDescriptor, RecurringJob> tasks = new LinkedHashMap<>();

    public JobScheduler( final ApplicationKey applicationKey )
    {
        this.scheduler = new RecurringJobScheduler( applicationKey + "-job-thread" );
    }

    public synchronized void deactivate()
    {
        this.scheduler.shutdownNow();
        this.tasks.clear();
    }

    public synchronized void unschedule( final String jobName )
    {
        doUnschedule( jobName );
    }

    public synchronized void schedule( final JobDescriptor job )
    {
        doSchedule( job );
    }

    public synchronized JobDescriptor get( final String jobName )
    {
        return this.tasks.keySet().
            stream().
            filter( desc -> desc.getName().equals( jobName ) ).
            findAny().
            orElse( null );
    }

    public synchronized List<JobDescriptor> list( final String jobNamePattern )
    {
        if ( jobNamePattern == null || jobNamePattern.isEmpty() )
        {
            return List.copyOf( this.tasks.keySet() );
        }
        else
        {
            return this.tasks.keySet().
                stream().
                filter( job -> job.getName().matches( jobNamePattern ) ).
                collect( Collectors.toUnmodifiableList() );
        }
    }

    private void doSchedule( final JobDescriptor descriptor )
    {
        final JobExecutionCommand command = new JobExecutionCommand( descriptor, this::rerunCommandCallback, this::removeCommandCallback );

        final RecurringJob recurringJob = this.scheduler.schedule( command );

        this.tasks.put( descriptor, recurringJob );

        LOG.info( "Added job: {}", descriptor.getDescription() );
    }

    private void doUnschedule( final String jobName )
    {
        for ( final Iterator<Map.Entry<JobDescriptor, RecurringJob>> iterator = this.tasks.entrySet().iterator(); iterator.hasNext(); )
        {
            final Map.Entry<JobDescriptor, RecurringJob> entry = iterator.next();
            final JobDescriptor jobDescriptor = entry.getKey();
            if ( jobDescriptor.getName().equals( jobName ) )
            {
                entry.getValue().cancel();
                LOG.info( "Job is stopped: {}", jobDescriptor.getDescription() );

                iterator.remove();
            }
        }
    }

    private synchronized void rerunCommandCallback( final JobExecutionCommand command )
    {
        final RecurringJob recurringJob = this.scheduler.schedule( command );
        this.tasks.put( command.getDescriptor(), recurringJob );
    }

    private synchronized void removeCommandCallback( final JobExecutionCommand command )
    {
        final RecurringJob job = this.tasks.remove( command.getDescriptor() );
        if ( job != null )
        {
            job.cancel();
        }
    }
}
