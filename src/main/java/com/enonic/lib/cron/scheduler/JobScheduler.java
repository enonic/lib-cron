package com.enonic.lib.cron.scheduler;

import com.enonic.lib.cron.model.JobDescriptor;
import com.enonic.lib.cron.model.JobDescriptors;
import com.enonic.xp.app.ApplicationKey;

public interface JobScheduler
{
    void schedule( JobDescriptor job );

    void schedule( JobDescriptors jobs );

    void reschedule( JobDescriptor jobDescriptor);

    boolean unschedule( String name );

    boolean unscheduleByKey( ApplicationKey key );

    JobDescriptor get( String name );

    JobDescriptors list( String name );
}
