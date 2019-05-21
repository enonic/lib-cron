package com.enonic.lib.cron.runner;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;

import com.enonic.xp.context.Context;
import com.enonic.lib.cron.model.JobDescriptor;

@Component(immediate = true)
public final class JobRunnerImpl
    implements JobRunner
{
    private final static Logger LOG = LoggerFactory.getLogger( JobRunnerImpl.class );

    @Override
    public void run( final JobDescriptor job )
    {
        try
        {
            LOG.info( "Executing job [" + job + "]" );

            final Stopwatch stopwatch = Stopwatch.createStarted();
            executeWithContext( job.getScript(), job.getContext() );

            LOG.info( "Executed job [" + job + "] in " + ( stopwatch.elapsed( TimeUnit.MILLISECONDS ) ) + " ms" );
        }
        catch ( final Exception e )
        {
            LOG.error( "Error executing job [" + job + "]", e );
        }
    }

    private void executeWithContext( final Callable<Object> script, final Context context )
    {
        context.callWith( script );
    }
}
