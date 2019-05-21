package com.enonic.lib.cron.model;

import java.time.Duration;
import java.util.concurrent.Callable;


import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.context.Context;

public interface JobDescriptor
{
    String getName();

   Callable<Object> getScript();

    ApplicationKey getApplicationKey();

    String getDescription();

    Context getContext();

    Duration nextExecution();
}
