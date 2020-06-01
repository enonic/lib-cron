package com.enonic.lib.cron.model;

import java.time.Duration;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.context.Context;

public interface JobDescriptor
{
    String getName();

    String getCron();

    String getCronDescription();

    Runnable getScript();

    ApplicationKey getApplicationKey();

    String getDescription();

    Context getContext();

    Duration nextExecution();

    int getTimes();

    int getDelay();

    int getFixedDelay();
}
