package com.enonic.lib.cron.model;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.ResourceBundle;

import com.cronutils.descriptor.refactor.TimeDescriptor;
import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;

import com.enonic.lib.cron.descriptor.CronDescriptor;

final class CronTrigger
{
    private final static CronDefinition DEFINITION = CronDefinitionBuilder.instanceDefinitionFor( CronType.UNIX );

    private final static CronParser PARSER = new CronParser( DEFINITION );

    private final static CronDescriptor DESCRIPTOR =
        new CronDescriptor( ResourceBundle.getBundle( "properties/CronUtilsI18N", Locale.UK ) );

    private final Cron cron;

    private final ExecutionTime executionTime;

    private CronTrigger( final Cron cron )
    {
        this.cron = cron;
        this.executionTime = ExecutionTime.forCron( this.cron );
    }

    Duration nextExecution()
    {
        final ZonedDateTime now = ZonedDateTime.now();
        return this.executionTime.timeToNextExecution( now ).get();
    }

    @Override
    public String toString()
    {
        return DESCRIPTOR.describe( this.cron );
    }

    static CronTrigger from( final String cron )
    {
        return new CronTrigger( PARSER.parse( cron ) );
    }
}
