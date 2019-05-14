package com.enonic.lib.cron.model;

import java.time.Duration;
import java.util.Objects;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.ResourceKey;

final class JobDescriptorImpl
    implements JobDescriptor
{
    private final String name;

    private final CronTrigger trigger;

    private final ScriptObjectMirror script;

    private JobDescriptorImpl( final Builder builder )
    {
        this.name = builder.name;
        this.trigger = CronTrigger.from( builder.cron );
        this.script = builder.script;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public Duration nextExecution()
    {
        return this.trigger.nextExecution();
    }

    @Override
    public ScriptObjectMirror getScript()
    {
        return this.script;
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
        return Objects.equals( name, that.name ) && Objects.equals( trigger, that.trigger ) &&
            Objects.equals( script, that.script );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( name, trigger, script );
    }

    static final class Builder
    {
        private String name;

        private String cron;

        private ScriptObjectMirror script;

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

        Builder script( final ScriptObjectMirror script )
        {
            this.script = script;
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
