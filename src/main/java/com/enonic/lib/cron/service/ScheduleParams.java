package com.enonic.lib.cron.service;

import java.util.concurrent.Callable;

public class ScheduleParams
{
    protected String name;

    protected String cron;

    protected String applicationKey;

    protected Callable<Object> script;

    protected ContextParams context;

    public ScheduleParams()
    {
        context = new ContextParams();
    }

    public ScheduleParams setName( final String name )
    {
        this.name = name;
        return this;
    }

    public ScheduleParams setCron( final String cron )
    {
        this.cron = cron;
        return this;
    }

    public ScheduleParams setApplicationKey( final String applicationKey )
    {
        this.applicationKey = applicationKey;
        return this;
    }

    public ScheduleParams setScript( final Callable<Object> script )
    {
        this.script = script;
        return this;
    }

    public ScheduleParams setContext( final ContextParams context )
    {
        this.context = context;
        return this;
    }

    public String getName()
    {
        return name;
    }

    public String getCron()
    {
        return cron;
    }

    public String getApplicationKey()
    {
        return applicationKey;
    }

    public Callable<Object> getScript()
    {
        return script;
    }

    public ContextParams getContext()
    {
        return context;
    }
}
