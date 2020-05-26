package com.enonic.lib.cron.provider;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.lib.cron.mapper.JobDescriptorMapper;
import com.enonic.lib.cron.mapper.JobDescriptorsMapper;
import com.enonic.lib.cron.model.JobDescriptor;
import com.enonic.lib.cron.model.JobDescriptorFactory;
import com.enonic.lib.cron.model.JobDescriptors;
import com.enonic.lib.cron.model.params.ListJobsParams;
import com.enonic.lib.cron.model.params.ScheduleParams;
import com.enonic.lib.cron.scheduler.JobScheduler;
import com.enonic.xp.context.Context;

@Component(immediate = true)
public class CronJobProviderImpl
    implements CronJobProvider
{

    private final static Logger LOG = LoggerFactory.getLogger( CronJobProviderImpl.class );

    private JobDescriptorFactory jobDescriptorFactory;

    private JobScheduler jobScheduler;

    private Context context;

    @Override
    public void schedule( final ScheduleParams params )
    {
        final JobDescriptor jobDescriptor = jobDescriptorFactory.create( params, context );

        if ( jobDescriptor == null )
        {
            throw new RuntimeException( String.format( "Cannot create a job 'name: %s, cron:%s'", params.getName(), params.getCron() ) );
        }

        jobScheduler.schedule( jobDescriptor );
    }

    @Override
    public void unschedule( final String jobName )
    {
        jobScheduler.unschedule( jobName );
    }

    @Override
    public JobDescriptorMapper get( final String jobName )
    {
        final JobDescriptor jobDescriptor = this.jobScheduler.get( jobName );

        return jobDescriptor != null ? new JobDescriptorMapper( jobDescriptor ) : null;
    }

    @Override
    public JobDescriptorsMapper list( final ListJobsParams params )
    {
        final JobDescriptors jobDescriptors = this.jobScheduler.list( params.getPattern() );

        return jobDescriptors != null ? new JobDescriptorsMapper( jobDescriptors ) : null;
    }

    @Override
    public void setContext( final Context context )
    {
        this.context = context;
    }

    @Reference
    public void setJobDescriptorFactory( final JobDescriptorFactory jobDescriptorFactory )
    {
        this.jobDescriptorFactory = jobDescriptorFactory;
    }

    @Reference
    public void setJobScheduler( final JobScheduler jobScheduler )
    {
        this.jobScheduler = jobScheduler;
    }

}
