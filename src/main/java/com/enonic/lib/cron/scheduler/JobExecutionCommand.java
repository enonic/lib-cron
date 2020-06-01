package com.enonic.lib.cron.scheduler;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;

import com.enonic.lib.cron.model.JobDescriptor;

final class JobExecutionCommand
    implements Runnable
{
    private final static Logger LOG = LoggerFactory.getLogger( JobExecutionCommand.class );

    private final JobDescriptor descriptor;

    private final AtomicInteger runCount;

    private final Consumer<JobExecutionCommand> rerunCallback;

    private final Consumer<JobExecutionCommand> finishedCallback;

    private final Predicate<JobDescriptor> runCheckFunction;

    JobExecutionCommand( final JobDescriptor descriptor, final Consumer<JobExecutionCommand> rerunCallback,
                         final Consumer<JobExecutionCommand> finishedCallback )
    {
        this.descriptor = descriptor;
        this.finishedCallback = finishedCallback;
        this.rerunCallback = this.descriptor.getFixedDelay() == 0 ? rerunCallback : command -> {
        };
        this.runCount = new AtomicInteger( 0 );

        final boolean isEndless = descriptor.getTimes() == 0;
        this.runCheckFunction = isEndless ? job -> true : job -> this.runCount.getAndIncrement() < job.getTimes();
    }

    @Override
    public void run()
    {
        try
        {
            if ( runCheckFunction.test( this.descriptor ) )
            {
                try
                {
                    this.doRun();
                }
                catch ( Exception e )
                {
                    this.rerunCallback.accept( this );
                    throw e;
                }

                this.rerunCallback.accept( this );
            }
            else
            {
                this.finishedCallback.accept( this );
            }
        }
        catch ( Exception e )
        {
            LOG.warn( "Error while running job [{}]", this.descriptor.getName(), e );
        }
        catch ( Throwable t )
        {
            LOG.error( "Error while running job [{}], no further attempts will be made", this.descriptor.getName(), t );
            throw t;
        }
    }

    private void doRun()
    {
        LOG.debug( "Executing job [{}]", this.descriptor.getName() );

        final Stopwatch stopwatch = Stopwatch.createStarted();
        this.descriptor.getContext().runWith( this.descriptor.getScript() );

        LOG.debug( "Executed job [{}] in {} ms", this.descriptor.getName(), stopwatch.elapsed( TimeUnit.MILLISECONDS ) );
    }

    public JobDescriptor getDescriptor()
    {
        return descriptor;
    }
}
