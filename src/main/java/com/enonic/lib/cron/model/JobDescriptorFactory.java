package com.enonic.lib.cron.model;

import com.enonic.lib.cron.context.ContextFactory;
import com.enonic.lib.cron.model.params.ScheduleParams;
import com.enonic.xp.context.Context;
import com.enonic.xp.security.SecurityService;


public final class JobDescriptorFactory
{
    private final ContextFactory contextFactory;

    public JobDescriptorFactory( final SecurityService securityService, final Context context )
    {
        this.contextFactory = new ContextFactory( securityService, context );
    }

    public JobDescriptor create( final ScheduleParams params )
    {
        JobDescriptorImpl.Builder builder = parseJob( params );

        final Context context = contextFactory.create( params.getContext() );

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
}
