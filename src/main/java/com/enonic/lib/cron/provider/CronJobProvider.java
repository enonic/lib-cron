package com.enonic.lib.cron.provider;

import java.util.List;

import com.enonic.lib.cron.mapper.JobDescriptorMapper;
import com.enonic.lib.cron.mapper.JobDescriptorsMapper;
import com.enonic.lib.cron.model.JobDescriptor;
import com.enonic.lib.cron.model.JobDescriptorFactory;
import com.enonic.lib.cron.model.params.ListJobsParams;
import com.enonic.lib.cron.model.params.ScheduleParams;
import com.enonic.lib.cron.scheduler.JobScheduler;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.context.Context;
import com.enonic.xp.security.SecurityService;

public class CronJobProvider
{
    private final JobDescriptorFactory jobDescriptorFactory;

    private final JobScheduler jobScheduler;

    public CronJobProvider( final ApplicationKey applicationKey, final Context context, final SecurityService securityService )
    {
        this.jobDescriptorFactory = new JobDescriptorFactory( securityService, context );
        this.jobScheduler = new JobScheduler( applicationKey );
    }

    public void schedule( final ScheduleParams params )
    {
        final JobDescriptor jobDescriptor = jobDescriptorFactory.create( params );
        jobScheduler.schedule( jobDescriptor );
    }

    public void unschedule( final String jobName )
    {
        jobScheduler.unschedule( jobName );
    }

    public void deactivate()
    {
        this.jobScheduler.deactivate();
    }

    public JobDescriptorMapper get( final String jobName )
    {
        final JobDescriptor jobDescriptor = this.jobScheduler.get( jobName );
        return jobDescriptor != null ? new JobDescriptorMapper( jobDescriptor ) : null;
    }

    public JobDescriptorsMapper list( final ListJobsParams params )
    {
        final List<JobDescriptor> jobDescriptors = this.jobScheduler.list( params.getPattern() );
        return new JobDescriptorsMapper( jobDescriptors );
    }
}
