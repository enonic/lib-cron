package com.enonic.lib.cron.model;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.Callable;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;


import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.context.Context;
import com.enonic.xp.resource.ResourceKey;

final class JobDescriptorImpl
    implements JobDescriptor
{
    private final String name;

    private CronTrigger trigger;

    private final Callable<Object> script;

    private final ApplicationKey applicationKey;

    private final Context context;

    private JobDescriptorImpl( final Builder builder )
    {
        this.name = builder.name;
        try
        {
            this.trigger = CronTrigger.from( builder.cron );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        this.script = builder.script;
        this.applicationKey = builder.applicationKey;
        this.context = builder.context;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public String getCron()
    {
        return trigger.toString();
    }

    @Override
    public Duration nextExecution()
    {
        return this.trigger.nextExecution();
    }

    @Override
    public Callable<Object> getScript()
    {
        return this.script;
    }

    @Override
    public ApplicationKey getApplicationKey()
    {
        return applicationKey;
    }

    public Context getContext()
    {
        return context;
    }

    @Override
    public String getDescription()
    {
        return this.name + " @ " + this.trigger.toString();
    }


    @Override
    public String toString()
    {
        return this.name;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        final JobDescriptorImpl that = (JobDescriptorImpl) o;
        return Objects.equals( name, that.name ) && Objects.equals( trigger.toString(), that.trigger.toString() ) &&
            Objects.equals( applicationKey, that.applicationKey );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( name, trigger.toString(), applicationKey );
    }

    static final class Builder
    {
        private ApplicationKey applicationKey;

        private String name;

        private String cron;

        private Callable<Object> script;

        private Context context;

        Builder name( final String name )
        {
            this.name = name;
            return this;
        }

        Builder cron( final String cron )
        {
            this.cron = cron;
            return this;
        }

        Builder script( final Callable<Object> script )
        {
            this.script = script;
            return this;
        }

        public Builder applicationKey( final String applicationKey )
        {
            this.applicationKey = ApplicationKey.from( applicationKey );
            return this;
        }

        public Builder context( final Context context )
        {
            this.context = context;
            return this;
        }

        private void validate()
        {
            Preconditions.checkArgument( !Strings.isNullOrEmpty( name ), "Job name must be set" );
            Preconditions.checkArgument( !Strings.isNullOrEmpty( cron ), "Job cron must be set" );
            Preconditions.checkArgument( script != null, "Script callback must be set" );
        }

        JobDescriptorImpl build()
        {
            validate();
            return new JobDescriptorImpl( this );
        }
    }

    static Builder builder()
    {
        return new Builder();
    }
}
