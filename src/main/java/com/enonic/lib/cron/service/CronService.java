package com.enonic.lib.cron.service;

import java.util.function.Supplier;

import com.enonic.lib.cron.model.JobDescriptorFactory;
import com.enonic.lib.cron.model.JobDescriptor;
import com.enonic.lib.cron.model.JobDescriptors;
import com.enonic.lib.cron.scheduler.JobScheduler;
import com.enonic.lib.cron.service.mapper.JobDescriptorMapper;
import com.enonic.lib.cron.service.mapper.JobDescriptorsMapper;
import com.enonic.lib.cron.service.params.ListJobsParams;
import com.enonic.lib.cron.service.params.ScheduleParams;
import com.enonic.xp.context.Context;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class CronService
    implements ScriptBean
{
    private JobDescriptorFactory JobDescriptorFactory;

    private JobScheduler jobScheduler;

    private Supplier<Context> context;

    public void schedule( final ScheduleParams params )
    {
        final JobDescriptor jobDescriptor = JobDescriptorFactory.create( params, context.get() );

        if ( jobDescriptor == null )
        {
            throw new RuntimeException( String.format( "Cannot create a job 'name: %s, cron:%s'", params.getName(), params.getCron() ) );
        }

        this.jobScheduler.schedule( jobDescriptor );

    }

    public void unschedule( final String jobName )
    {
        this.jobScheduler.unschedule( jobName );
    }

    public JobDescriptorMapper get( final String jobName )
    {
        final JobDescriptor jobDescriptor = this.jobScheduler.get( jobName );

        return jobDescriptor != null ? new JobDescriptorMapper( jobDescriptor ) : null;
    }

    public JobDescriptorsMapper list( final ListJobsParams params )
    {
        final JobDescriptors jobDescriptors = this.jobScheduler.list( params.getPattern() );

        return jobDescriptors != null ? new JobDescriptorsMapper( jobDescriptors ) : null;
    }

    public ScheduleParams newParams()
    {
        return new ScheduleParams();
    }

    public ListJobsParams listParams()
    {
        return new ListJobsParams();
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.JobDescriptorFactory = context.getService( JobDescriptorFactory.class ).get();
        this.jobScheduler = context.getService( JobScheduler.class ).get();
        this.context = context.getBinding( Context.class );
    }
}
