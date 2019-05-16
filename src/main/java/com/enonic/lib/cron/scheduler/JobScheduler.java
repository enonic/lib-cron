package com.enonic.lib.cron.scheduler;

import com.enonic.lib.cron.model.JobDescriptor;
import com.enonic.lib.cron.model.JobDescriptors;

public interface JobScheduler
{
    void schedule( JobDescriptor job );

    void schedule( JobDescriptors jobs );

    void reschedule( JobDescriptor job );

    void unschedule( String name );
}
