package com.enonic.lib.cron.service;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

import com.enonic.lib.cron.model.JobDescriptorFactory;
import com.enonic.lib.cron.model.JobDescriptor;
import com.enonic.lib.cron.scheduler.JobScheduler;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class CronService
    implements ScriptBean
{
    private JobDescriptorFactory JobDescriptorFactory;

    private JobScheduler jobScheduler;

    public void schedule( final String name, final String cron, final String applicationKey, final ScriptObjectMirror script )
    {
        final JobDescriptor jobDescriptor = JobDescriptorFactory.create( name, cron, applicationKey, script );

        if ( jobDescriptor == null )
        {
            throw new RuntimeException( String.format( "Cannot create a job 'name: %s, cron:%s'", name, cron ) );
        }

        this.jobScheduler.schedule( jobDescriptor );

    }

    public void unschedule( final String jobName )
    {
        this.jobScheduler.unschedule( jobName );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.JobDescriptorFactory = context.getService( JobDescriptorFactory.class ).get();
        this.jobScheduler = context.getService( JobScheduler.class ).get();
    }
}
