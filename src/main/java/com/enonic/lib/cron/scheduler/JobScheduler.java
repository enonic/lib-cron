package com.enonic.lib.cron.scheduler;

import java.util.List;

import com.enonic.lib.cron.model.JobDescriptor;
import com.enonic.lib.cron.model.JobDescriptors;

public interface JobScheduler
{
    void schedule( JobDescriptor job );

    void schedule( JobDescriptors jobs );

    void unschedule( String name );

    JobDescriptor get( String name );

    List<JobDescriptor> list( String name );
}
