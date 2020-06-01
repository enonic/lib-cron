package com.enonic.lib.cron.model;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.lib.cron.context.ContextFactory;
import com.enonic.lib.cron.model.params.ScheduleParams;
import com.enonic.xp.context.Context;


@Component(immediate = true)
public final class JobDescriptorFactoryImpl
    implements JobDescriptorFactory
{
    private final static Logger LOG = LoggerFactory.getLogger( JobDescriptorFactoryImpl.class );

    private ContextFactory contextFactory;

    public JobDescriptor create( final ScheduleParams params, final Context defaultContext )
    {
        JobDescriptorImpl.Builder builder = parseJob( params );

        final Context context = contextFactory.create( params.getContext(), defaultContext );

        return builder.context( context ).build();
    }

    private JobDescriptorImpl.Builder parseJob( final ScheduleParams params )
    {
        return JobDescriptorImpl.builder().
            name( params.getName() ).
            cron( params.getCron() ).
            script( params.getScript() ).
            times( params.getTimes() ).
            delay( params.getDelay() ).
            fixedDelay( params.getFixedDelay() ).
            applicationKey( params.getApplicationKey() );
    }

    @Reference
    public void setContextFactory( final ContextFactory contextFactory )
    {
        this.contextFactory = contextFactory;
    }
}
