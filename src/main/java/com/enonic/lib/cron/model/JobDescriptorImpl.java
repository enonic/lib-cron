package com.enonic.lib.cron.model;

import java.time.Duration;
import java.util.Optional;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.context.Context;

final class JobDescriptorImpl
    implements JobDescriptor
{
    private final String name;

    private final Runnable script;

    private final ApplicationKey applicationKey;

    private final Context context;

    private final String cron;

    private final int times;

    private final int delay;

    private final int fixedDelay;

    private final CronTrigger trigger;

    private JobDescriptorImpl( final Builder builder )
    {
        this.name = builder.name;
        this.cron = builder.cron;
        this.trigger = Optional.ofNullable( builder.cron ).map( CronTrigger::from ).orElse( null );
        this.script = builder.script;
        this.applicationKey = builder.applicationKey;
        this.times = builder.times;
        this.context = builder.context;
        this.delay = builder.delay;
        this.fixedDelay = builder.fixedDelay;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public String getCron()
    {
        return cron;
    }

    @Override
    public String getCronDescription()
    {
        return Optional.ofNullable( this.trigger ).map( CronTrigger::toString ).orElse( null );
    }

    @Override
    public Duration nextExecution()
    {
        return Optional.ofNullable( this.trigger ).map( CronTrigger::nextExecution ).orElse( null );
    }

    @Override
    public Runnable getScript()
    {
        return this.script;
    }

    @Override
    public ApplicationKey getApplicationKey()
    {
        return applicationKey;
    }

    public int getTimes()
    {
        return times;
    }

    public Context getContext()
    {
        return context;
    }

    @Override
    public String getDescription()
    {
        if ( this.cron != null )
        {
            return this.name + " @ " + this.cron + " (" + this.getCronDescription() + ")";
        }
        else
        {
            return this.name + " @ " + " delay: " + delay + "ms, fixedDelay: " + this.fixedDelay + "ms";
        }
    }

    @Override
    public int getDelay()
    {
        return delay;
    }

    @Override
    public int getFixedDelay()
    {
        return fixedDelay;
    }

    @Override
    public String toString()
    {
        return this.name;
    }

    static final class Builder
    {
        private ApplicationKey applicationKey;

        private String name;

        private String cron;

        private Runnable script;

        private Context context;

        private int times = 0;

        private int delay = 0;

        private int fixedDelay = 0;

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

        Builder script( final Runnable script )
        {
            this.script = script;
            return this;
        }

        public Builder applicationKey( final String applicationKey )
        {
            this.applicationKey = ApplicationKey.from( applicationKey );
            return this;
        }

        public Builder times( final Integer times )
        {
            if ( times != null )
            {
                Preconditions.checkArgument( times > 0, "`times` must be bigger then 0." );
                this.times = times;
            }
            return this;
        }

        public Builder context( final Context context )
        {
            this.context = context;
            return this;
        }

        public Builder delay( final Integer delay )
        {
            if ( delay != null )
            {
                this.delay = delay;
            }
            return this;
        }

        public Builder fixedDelay( final Integer fixedDelay )
        {
            if ( fixedDelay != null )
            {
                this.fixedDelay = fixedDelay;
            }
            return this;
        }

        private void validate()
        {
            Preconditions.checkArgument( !Strings.isNullOrEmpty( name ), "Job name must be set" );
            Preconditions.checkArgument( script != null, "Script callback must be set" );
            Preconditions.checkArgument( ( ( cron == null && fixedDelay > 0 ) || ( cron != null && fixedDelay == 0 ) ),
                                         "Job cron or fixedDelay bigger then `0` must be set, but not both." );
            Preconditions.checkArgument( cron == null || delay == 0, "delay cannot be set with cron." );
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
