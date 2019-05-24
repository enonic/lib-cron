package com.enonic.lib.cron.model;

import com.enonic.lib.cron.service.params.ScheduleParams;
import com.enonic.xp.context.Context;


public interface JobDescriptorFactory
{
    JobDescriptor create( final ScheduleParams params, final Context context);
}
