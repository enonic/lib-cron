package com.enonic.lib.cron.model;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.junit.jupiter.api.Test;

import com.enonic.xp.context.ContextAccessor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JobDescriptorImplTest
{
    @Test
    public void testBuilder()
        throws Throwable
    {
        final JobDescriptorImpl.Builder builder = JobDescriptorImpl.builder();
        builder.name( "myJob" );
        builder.cron( "* * * * *" );

        final ScriptEngine engine = new ScriptEngineManager().getEngineByName( "nashorn" );
        builder.script( () -> {
            try
            {
                engine.eval( "(function() { try { require('./invalid'); } catch (ex) { return ex.code; } })" );
            }
            catch ( ScriptException e )
            {
                e.printStackTrace();
            }
        } );
        builder.context( ContextAccessor.current() );

        final JobDescriptor descriptor = builder.build();
        assertEquals( "myJob", descriptor.getName() );
        assertEquals( "* * * * *", descriptor.getCron() );
        assertEquals( "myJob @ * * * * * (every minute)", descriptor.getDescription() );
        assertEquals( "myJob", descriptor.toString() );
        assertNotNull( descriptor.nextExecution() );
    }

    @Test
    public void testCronDescription1()
        throws Throwable
    {
        final JobDescriptorImpl.Builder builder = JobDescriptorImpl.builder();
        builder.name( "myJob" );
        builder.cron( "1 1 1 1 1" );
        builder.script( () -> {
        } );

        final JobDescriptor jobDescriptor = builder.build();
        assertEquals( "myJob @ 1 1 1 1 1 (at 01:01 at 1 day at Jan month at Mon day)", jobDescriptor.getDescription() );

    }

    @Test
    public void testCronDescription2()
        throws Throwable
    {
        final JobDescriptorImpl.Builder builder = JobDescriptorImpl.builder();
        builder.name( "myJob" );
        builder.cron( "2-32/2 */2 1,2,3 1 sun" );
        builder.script( () -> {
        } );

        final JobDescriptor jobDescriptor = builder.build();
        assertEquals(
            "myJob @ 2-32/2 */2 1,2,3 1 sun (every 2 minutes between 2 and 32 every 2 hours at 1, 2 and 3 days at Jan month at Sun day)",
            jobDescriptor.getDescription() );

    }

    @Test
    public void testCronDescription3()
        throws Throwable
    {
        final JobDescriptorImpl.Builder builder = JobDescriptorImpl.builder();
        builder.name( "myJob" );
        builder.cron( "0-18 1-2 * 3-4 1-5" );
        builder.script( () -> {
        } );

        final JobDescriptor jobDescriptor = builder.build();
        assertEquals(
            "myJob @ 0-18 1-2 * 3-4 1-5 (every minute between 0 and 18 every hour between 1 and 2 every month between Mar and Apr every day between Mon and Fri)",
            jobDescriptor.getDescription() );

    }
}
