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
}
