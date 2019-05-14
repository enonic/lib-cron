package com.enonic.lib.cron.runner;

import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import jdk.nashorn.api.scripting.NashornScriptEngine;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import com.enonic.lib.cron.model.JobDescriptor;
import com.enonic.xp.portal.script.PortalScriptService;

public class JobRunnerImplTest
{
    private JobRunnerImpl runner;

    private NashornScriptEngine engine;

    @Before
    public void setup()
    {
        this.runner = new JobRunnerImpl();
        engine = (NashornScriptEngine) new ScriptEngineManager().getEngineByName( "nashorn" );
    }

    @Test
    public void testRun()
        throws ScriptException
    {
        final ScriptObjectMirror script =
            (ScriptObjectMirror) engine.eval( "(function() { try { require('./invalid'); } catch (ex) { return ex.code; } })" );

        final JobDescriptor descriptor = Mockito.mock( JobDescriptor.class );
        Mockito.when( descriptor.getScript() ).thenReturn( script );

        this.runner.run( descriptor );
    }

    @Test(expected = ScriptException.class)
    public void testRun_exception()
        throws ScriptException
    {
        final ScriptObjectMirror script = (ScriptObjectMirror) engine.eval( "return 'result';" );

        final JobDescriptor descriptor = Mockito.mock( JobDescriptor.class );
        Mockito.when( descriptor.getScript() ).thenReturn( script );

        this.runner.run( descriptor );
    }
}
