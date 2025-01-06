package com.enonic.lib.cron.scheduler;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;

import com.enonic.lib.cron.model.JobDescriptor;

final class JobExecutionCommand
    implements Runnable
{
    private static final Logger LOG = LoggerFactory.getLogger( JobExecutionCommand.class );

    private final JobDescriptor descriptor;

    private final Consumer<JobExecutionCommand> rerunCallback;

    private final Predicate<JobExecutionCommand> runCheckFunction;

    JobExecutionCommand( final JobDescriptor descriptor, final Consumer<JobExecutionCommand> rerunCallback,
                         final Predicate<JobExecutionCommand> runCheckFunction )
    {
        this.descriptor = descriptor;
        this.rerunCallback = rerunCallback;
        this.runCheckFunction = runCheckFunction;
    }

    @Override
    public void run()
    {
        try
        {
            if ( runCheckFunction.test( this ) )
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
