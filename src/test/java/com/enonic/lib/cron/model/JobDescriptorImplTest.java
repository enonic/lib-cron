package com.enonic.lib.cron.model;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.junit.Test;

import com.enonic.xp.context.ContextAccessor;

import static org.junit.Assert.*;

public class JobDescriptorImplTest
{
    @Test
    public void testBuilder() throws Exception
    {
        final JobDescriptorImpl.Builder builder = JobDescriptorImpl.builder();
        builder.name( "myJob" );
        builder.cron( "* * * * *" );

        final ScriptEngine engine = new ScriptEngineManager().getEngineByName( "nashorn");
        builder.script( () -> engine.eval( "(function() { try { require('./invalid'); } catch (ex) { return ex.code; } })" ) );
        builder.context( ContextAccessor.current() );

        final JobDescriptor descriptor = builder.build();
        assertEquals( "myJob", descriptor.getName() );
        assertEquals( "* * * * *", descriptor.getCron() );
        assertEquals( "myJob @ * * * * * (every minute)", descriptor.getDescription() );
        assertEquals( "myJob", descriptor.toString() );
        assertNotNull( descriptor.nextExecution() );
    }
}
