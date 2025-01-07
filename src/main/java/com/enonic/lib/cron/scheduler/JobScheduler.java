package com.enonic.lib.cron.scheduler;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.lib.cron.model.JobDescriptor;

public final class JobScheduler
{
    private static final Logger LOG = LoggerFactory.getLogger( JobScheduler.class );

    private final JobExecutorService jobExecutorService;

    private final Map<String, RecurringJob> tasks = new LinkedHashMap<>();

    public JobScheduler( final JobExecutorService executorService )
    {
        this.jobExecutorService = executorService;
    }

    public synchronized void deactivate()
    {
        this.tasks.clear();
    }

    public synchronized void unschedule( final String jobName )
    {
        final RecurringJob recurringJob = tasks.remove( jobName );
        if ( recurringJob != null )
        {
            recurringJob.cancel();
            LOG.info( "Job is stopped: {}", recurringJob.getDescriptor().getDescription() );
        }
    }

    public synchronized void schedule( final JobDescriptor job )
    {
        final boolean cron = job.getCron() != null;
        Consumer<JobExecutionCommand> rerunCommandCallback = cron ? this::rerunCommandCallback : j -> {
        };

        final boolean endless = job.getTimes() == 0;
        Predicate<JobExecutionCommand> runCheckFunction = endless ? j -> true : new CountingCheck();

        final JobExecutionCommand command = new JobExecutionCommand( job, rerunCommandCallback, runCheckFunction );

        final ScheduledFuture<?> scheduledFuture = cron
            ? this.jobExecutorService.schedule( command, job.nextExecution().toMillis(), TimeUnit.MILLISECONDS )
            : this.jobExecutorService.scheduleWithFixedDelay( command, job.getDelay(), job.getFixedDelay(), TimeUnit.MILLISECONDS );

        final RecurringJob existed = this.tasks.put( job.getName(), new RecurringJob( scheduledFuture, job ) );
        if ( existed != null )
        {
            existed.cancel();
        }

        LOG.info( "Added job: {}", job.getDescription() );
    }

    private synchronized void remove( final JobExecutionCommand command )
    {
        final JobDescriptor descriptor = command.getDescriptor();

        // If tasks map does not contain the job, it means it was already unscheduled
        tasks.computeIfPresent( descriptor.getName(), ( name, job ) -> {
            if ( job.getDescriptor() == descriptor )
            {
                // Our job is still in the map, so we can stop and remove it
                job.cancel();
                return null;
            }
            else
            {
                // This is not our job in the map for the same name. Keep the existing job intact.
                return job;
            }
        } );
    }

    public synchronized JobDescriptor get( final String jobName )
    {
        final RecurringJob recurringJob = this.tasks.get( jobName );
        if ( recurringJob != null )
        {
            return recurringJob.getDescriptor();
        }
        return null;
    }

    public synchronized List<JobDescriptor> list( final String jobNamePattern )
    {
        final Predicate<JobDescriptor> filter =
            jobNamePattern == null || jobNamePattern.isEmpty() ? job -> true : job -> job.getName().matches( jobNamePattern );

        return tasks.values().stream().map( RecurringJob::getDescriptor ).filter( filter ).collect( Collectors.toUnmodifiableList() );
    }

    private synchronized void rerunCommandCallback( final JobExecutionCommand command )
    {
        final JobDescriptor descriptor = command.getDescriptor();

        // If tasks map does not contain the job, it means it was unscheduled
        this.tasks.computeIfPresent( descriptor.getName(), ( name, job ) -> {
            if ( job.getDescriptor() == descriptor )
            {
                // Our job is still in the map, so we can reschedule it
                final ScheduledFuture<?> scheduledFuture =
                    this.jobExecutorService.schedule( command, descriptor.nextExecution().toMillis(), TimeUnit.MILLISECONDS );
                return new RecurringJob( scheduledFuture, descriptor );
            }
            else
            {
                // This is not our job in the map for the same name. Keep the existing job intact.
                return job;
            }
        } );
    }

    private class CountingCheck
        implements Predicate<JobExecutionCommand>
    {
        final AtomicInteger runCount = new AtomicInteger( 0 );

        public boolean test( JobExecutionCommand job )
        {
            final boolean toRun = runCount.getAndIncrement() < job.getDescriptor().getTimes();
            if ( !toRun )
            {
                remove( job );
            }
            return toRun;
        }
    }
}
