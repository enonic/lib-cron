package com.enonic.lib.cron.provider;

import com.enonic.lib.cron.mapper.JobDescriptorMapper;
import com.enonic.lib.cron.mapper.JobDescriptorsMapper;
import com.enonic.lib.cron.model.params.ListJobsParams;
import com.enonic.lib.cron.model.params.ScheduleParams;


import com.enonic.xp.context.Context;

public interface CronJobProvider
{

    void schedule( final ScheduleParams params );

    void unschedule( final String jobName );

    JobDescriptorMapper get( final String jobName );

    JobDescriptorsMapper list( final ListJobsParams params );

    void setContext( final Context context);

}
