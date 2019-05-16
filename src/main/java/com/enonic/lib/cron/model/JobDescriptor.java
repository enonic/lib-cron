package com.enonic.lib.cron.model;

import java.time.Duration;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.ResourceKey;

public interface JobDescriptor
{
    String getName();

    ScriptObjectMirror getScript();

    ApplicationKey getApplicationKey();

    String getDescription();

    Duration nextExecution();
}
