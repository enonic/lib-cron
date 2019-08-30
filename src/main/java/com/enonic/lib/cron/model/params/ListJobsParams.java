package com.enonic.lib.cron.model.params;

public class ListJobsParams
{
    protected String pattern;

    public ListJobsParams()
    {
    }

    public String getPattern()
    {
        return pattern;
    }

    public void setPattern( final String pattern )
    {
        this.pattern = pattern;
    }
}
