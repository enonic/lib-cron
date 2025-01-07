package com.enonic.lib.cron.scheduler;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.lib.cron.model.JobDescriptor;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
public class JobExecutionCommandTest
{
    @Mock
    Appender mockAppender;

    @BeforeEach
    public void setUp()
    {
        Logger logger = Logger.getLogger( JobExecutionCommand.class.getName() );
        logger.addAppender( mockAppender );
    }

    @Test
    public void runCommand()
        throws Exception
    {
        final JobDescriptor descriptor = Mockito.mock( JobDescriptor.class );

        final Runnable script = Mockito.mock( Runnable.class );

        Mockito.when( descriptor.getScript() ).thenReturn( script );
        Mockito.when( descriptor.getContext() ).thenReturn( ContextAccessor.current() );

        new JobExecutionCommand( descriptor,  command -> {
        }, command -> true ).run();

        Mockito.verify( script, Mockito.times( 1 ) ).run();
    }

    @Test
    public void runWithException()
        throws Exception
    {
        ArgumentCaptor<LoggingEvent> eventArgumentCaptor = ArgumentCaptor.forClass( LoggingEvent.class );

        final JobDescriptor descriptor = Mockito.mock( JobDescriptor.class );
        final Context context = Mockito.mock( Context.class );

        final Runnable script = Mockito.mock( Runnable.class );
        Mockito.when( descriptor.getScript() ).thenReturn( script );
        Mockito.when( descriptor.getName() ).thenReturn( "myJob" );
        Mockito.when( descriptor.getContext() ).thenReturn( context );

        Mockito.doThrow( new RuntimeException() ).when( context ).runWith( script );

        new JobExecutionCommand( descriptor, command -> {
        }, command -> true ).run();
        Mockito.verify( mockAppender, Mockito.times( 2 ) ).doAppend( eventArgumentCaptor.capture() );

        assertEquals( "Executing job [myJob]", eventArgumentCaptor.getAllValues().get( 0 ).getMessage() );
        assertEquals( "Error while running job [myJob]", eventArgumentCaptor.getAllValues().get( 1 ).getMessage() );
    }

    @Test
    public void runWithThrowable()
        throws Exception
    {
        ArgumentCaptor<LoggingEvent> eventArgumentCaptor = ArgumentCaptor.forClass( LoggingEvent.class );

        final JobDescriptor descriptor = Mockito.mock( JobDescriptor.class );
        final Context context = Mockito.mock( Context.class );

        final Runnable script = Mockito.mock( Runnable.class );
        Mockito.when( descriptor.getScript() ).thenReturn( script );
        Mockito.when( descriptor.getName() ).thenReturn( "myJob" );
        Mockito.when( descriptor.getContext() ).thenReturn( context );

        Mockito.doThrow( new Error() ).when( context ).runWith( script );

        try
        {
            new JobExecutionCommand( descriptor, command -> {
            }, command -> true ).run();
        }
        catch ( Throwable e )
        {
            Mockito.verify( mockAppender, Mockito.times( 2 ) ).doAppend( eventArgumentCaptor.capture() );
            assertEquals( "Executing job [myJob]", eventArgumentCaptor.getAllValues().get( 0 ).getMessage() );
            assertEquals( "Error while running job [myJob], no further attempts will be made",
                          eventArgumentCaptor.getAllValues().get( 1 ).getMessage() );
        }

    }
}
