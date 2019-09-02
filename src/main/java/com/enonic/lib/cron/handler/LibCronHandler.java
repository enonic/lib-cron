package com.enonic.lib.cron.handler;

import com.enonic.lib.cron.provider.CronJobProvider;
import com.enonic.lib.cron.mapper.JobDescriptorMapper;
import com.enonic.lib.cron.mapper.JobDescriptorsMapper;
import com.enonic.lib.cron.model.params.ListJobsParams;
import com.enonic.lib.cron.model.params.ScheduleParams;
import com.enonic.xp.context.Context;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class LibCronHandler
    implements ScriptBean
{
    private CronJobProvider cronJobProvider;

    @Override
    public void initialize( final BeanContext context )
    {
        this.cronJobProvider = context.getService( CronJobProvider.class ).get();
        this.cronJobProvider.setContext( context.getBinding( Context.class ).get() );
    }

    public void schedule( final ScheduleParams params )
    {
        cronJobProvider.schedule( params );
    }

    public void unschedule( final String jobName )
    {
        cronJobProvider.unschedule( jobName );
    }

    public JobDescriptorMapper get( final String jobName )
    {
        return cronJobProvider.get( jobName );
    }

    public JobDescriptorsMapper list( final ListJobsParams params )
    {
        return cronJobProvider.list( params );
    }

    public ScheduleParams newParams()
    {
        return new ScheduleParams();
    }

    public ListJobsParams listParams()
    {
        return new ListJobsParams();
    }

}
