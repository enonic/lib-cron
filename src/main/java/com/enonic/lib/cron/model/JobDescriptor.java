package com.enonic.lib.cron.model;

import java.time.Duration;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

import com.enonic.xp.resource.ResourceKey;

public interface JobDescriptor
{
    String getName();

    ScriptObjectMirror getScript();

    String getDescription();

    Duration nextExecution();
}
