package com.enonic.lib.cron.model;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.Callable;


import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.context.Context;

public interface JobDescriptor
{
    String getName();

    String getCron();

   Callable<Object> getScript();

    ApplicationKey getApplicationKey();

    Optional<Integer> getTimes();

    String getDescription();

    Context getContext();

    Duration nextExecution();
}
