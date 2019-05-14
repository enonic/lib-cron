package com.enonic.lib.cron.runner;

import com.enonic.lib.cron.model.JobDescriptor;

public interface JobRunner
{
    void run( JobDescriptor job );
}
