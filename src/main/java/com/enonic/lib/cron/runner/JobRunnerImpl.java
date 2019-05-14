package com.enonic.lib.cron.runner;

import java.util.concurrent.TimeUnit;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.context.Context;
import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.ScriptExports;
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
            executeInContext( job.getScript() );

            LOG.info( "Executed job [" + job + "] in " + ( stopwatch.elapsed( TimeUnit.MILLISECONDS ) ) + " ms" );
        }
        catch ( final Exception e )
        {
            LOG.error( "Error executing job [" + job + "]", e );
        }
    }

    private void executeInContext( final ScriptObjectMirror script )
    {
        final Context context = ContentConstants.CONTEXT_MASTER;
        context.runWith( () -> script.call( null ) );
    }
}
