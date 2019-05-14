package com.enonic.lib.cron.model;

import javax.script.ScriptEngineManager;

import org.junit.Test;

import jdk.nashorn.api.scripting.NashornScriptEngine;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import com.enonic.xp.app.ApplicationKey;

import static org.junit.Assert.*;

public class JobDescriptorImplTest
{
    @Test
    public void testBuilder() throws Exception
    {
        final JobDescriptorImpl.Builder builder = JobDescriptorImpl.builder();
        builder.name( "myJob" );
        builder.cron( "* * * * *" );

        NashornScriptEngine engine = (NashornScriptEngine) new ScriptEngineManager().getEngineByName( "nashorn");
        builder.script( (ScriptObjectMirror) engine.eval( "(function() { try { require('./invalid'); } catch (ex) { return ex.code; } })" ) );

        final JobDescriptor descriptor = builder.build();
        assertEquals( "myJob", descriptor.getName() );
        assertEquals( "function() { try { require('./invalid'); } catch (ex) { return ex.code; } }", descriptor.getScript().toString() );
        assertEquals( "myJob @ every minute", descriptor.getDescription() );
        assertEquals( "myJob", descriptor.toString() );
        assertNotNull( descriptor.nextExecution() );
    }
}
