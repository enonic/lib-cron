package com.enonic.lib.cron.model.params;

public class ScheduleParams
{
    private String name;

    private String cron;

    private String applicationKey;

    private Integer times;

    private Runnable script;

    private ContextParams context;

    private Integer delay;

    private Integer fixedDelay;

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

    public ScheduleParams setTimes( final Integer times )
    {
        this.times = times;
        return this;
    }

    public Runnable getScript()
    {
        return script;
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

    public Integer getTimes()
    {
        return times;
    }

    public ScheduleParams setScript( final Runnable script )
    {
        this.script = script;
        return this;
    }

    public Integer getDelay()
    {
        return delay;
    }

    public ScheduleParams setDelay( final Integer delay )
    {
        this.delay = delay;
        return this;
    }

    public Integer getFixedDelay()
    {
        return fixedDelay;
    }

    public ScheduleParams setFixedDelay( final Integer fixedDelay )
    {
        this.fixedDelay = fixedDelay;
        return this;
    }

    public ContextParams getContext()
    {
        return context;
    }
}
