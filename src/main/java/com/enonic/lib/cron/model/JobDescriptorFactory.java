package com.enonic.lib.cron.model;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

public interface JobDescriptorFactory
{
    JobDescriptor create( final String name, final String cron, final ScriptObjectMirror script);
}
