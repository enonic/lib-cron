package com.enonic.lib.cron.model;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CronTriggerTest
{
    @Test
    public void testCreate()
    {
        final CronTrigger trigger = CronTrigger.from( "* * * * *" );

        final Duration duration = trigger.nextExecution();
        assertTrue( duration.getSeconds() >= 0 );

        assertEquals( "every minute", trigger.toString() );
    }
}
