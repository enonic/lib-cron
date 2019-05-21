package com.enonic.lib.cron.runner;

import java.util.concurrent.Callable;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import jdk.nashorn.api.scripting.NashornScriptEngine;

import com.enonic.lib.cron.model.JobDescriptor;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.portal.script.PortalScriptService;

import static org.junit.Assert.*;

public class JobRunnerImplTest
{
    private JobRunnerImpl runner;

    private ScriptEngine engine;

    @Before
    public void setup()
    {
        this.runner = new JobRunnerImpl();
        final ScriptEngineManager engineManager = new ScriptEngineManager();
        engine = engineManager.getEngineByName( "nashorn" );
    }

    @Test
    public void testRun()
        throws Exception
    {
        final Callable script = () -> engine.eval( "(function() { try { require('./invalid'); } catch (ex) { return ex.code; } })" );

        final JobDescriptor descriptor = Mockito.mock( JobDescriptor.class );
        Mockito.when( descriptor.getScript() ).thenReturn( script );
        Mockito.when( descriptor.getContext() ).thenReturn( ContextAccessor.current() );

        this.runner.run( descriptor );
    }

    @Test
    public void testRun_exception()
        throws Exception
    {
        final Callable script = () -> {
            String message = null;
            try
            {
                engine.eval( "(error)" );
            }
            catch ( ScriptException e )
            {
                message = e.getMessage();
            }

            assertEquals( "ReferenceError: \"error\" is not defined in <eval> at line number 1", message );

            return null;
        };

        final JobDescriptor descriptor = Mockito.mock( JobDescriptor.class );
        Mockito.when( descriptor.getScript() ).thenReturn( script );
        Mockito.when( descriptor.getContext() ).thenReturn( ContextAccessor.current() );

        this.runner.run( descriptor );
    }
}
