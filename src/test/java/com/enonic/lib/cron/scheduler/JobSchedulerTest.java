package com.enonic.lib.cron.scheduler;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.lib.cron.model.JobDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.context.ContextAccessor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JobSchedulerTest
    extends BundleBasedTest
{
    private JobScheduler scheduler;

    private ScriptEngine engine;

    @BeforeEach
    public void setup()
        throws Exception
    {
        super.setup();
        final JobExecutorService executorService = new JobExecutorService();
        executorService.activate( getBundleContext() );
        getBundleContext().registerService( JobExecutorService.class, executorService, null );
        this.scheduler = new JobScheduler( executorService );

        final ScriptEngineManager engineManager = new ScriptEngineManager();
        engine = engineManager.getEngineByName( "nashorn" );
    }

    @AfterEach
    public void deactivate()
    {
        this.scheduler.deactivate();
    }

    @Test
    public void testEmptyList()
        throws Exception
    {
        List<JobDescriptor> expected = List.of();

        assertEquals( expected, this.scheduler.list( null ) );
        assertEquals( expected, this.scheduler.list( "" ) );
        assertEquals( expected, this.scheduler.list( ".+" ) );
        assertEquals( expected, this.scheduler.list( "jobName\\d+" ) );

        final JobDescriptor job = mockJob( "jobName1", Duration.ofMillis( 100000 ) );
        this.scheduler.schedule( job );

        expected = List.of( job );

        assertEquals( expected, this.scheduler.list( null ) );
        assertEquals( expected, this.scheduler.list( "" ) );
        assertEquals( expected, this.scheduler.list( ".+" ) );
        assertEquals( expected, this.scheduler.list( "jobName\\d+" ) );
    }

    @Test
    public void testRun()
        throws Exception
    {
        final JobDescriptor job = mockJob( "jobName1", Duration.ofMillis( 100000 ) );

        this.scheduler.schedule( job );

        Thread.sleep( 1000 );

        assertEquals( job, this.scheduler.get( "jobName1" ) );
    }

    @Test
    public void testRunWithFixedDelayAndTimes()
        throws Exception
    {
        final AtomicInteger callCount = new AtomicInteger( 0 );
        final JobDescriptor job = mockJob( "jobName1", 50, 100, 5, callCount::incrementAndGet );

        this.scheduler.schedule( job );

        Thread.sleep( 1000 );

        assertEquals( 5, callCount.get() );
    }

    @Test
    public void testRunWithFixedDelay()
        throws Exception
    {
        final AtomicInteger callCount = new AtomicInteger( 0 );
        final JobDescriptor job = mockJob( "jobName1", 100, 100, 0, callCount::incrementAndGet );

        this.scheduler.schedule( job );

        Thread.sleep( 1000 );

        assertTrue(  callCount.get() >= 8 );
    }

    @Test
    public void testRunWithFixedDelay_long()
        throws Exception
    {
        final AtomicInteger callCount = new AtomicInteger( 0 );
        final JobDescriptor job = mockJob( "jobName1", 2000, 100, 0, callCount::incrementAndGet );

        this.scheduler.schedule( job );

        Thread.sleep( 1000 );

        assertEquals( 0, callCount.get() );
    }

    @Test
    public void testRunWithFixedDelayZeroInitial()
        throws Exception
    {
        final AtomicInteger callCount = new AtomicInteger( 0 );
        final JobDescriptor job = mockJob( "jobName1", 0, 100, 0, callCount::incrementAndGet );

        this.scheduler.schedule( job );

        Thread.sleep( 1000 );

        assertTrue(  callCount.get() >= 8 );
    }

    @Test
    public void testRunMultipleTimes()
        throws Exception
    {
        final Runnable script1 = Mockito.mock( Runnable.class );
        final Runnable script2 = Mockito.mock( Runnable.class );

        final JobDescriptor job1 = mockJob( "jobName1", Duration.ofMillis( 10 ), 3, script1 );
        final JobDescriptor job2 = mockJob( "jobName2", Duration.ofMillis( 10 ), 1, script2 );

        this.scheduler.schedule( job1 );
        this.scheduler.schedule( job2 );

        Thread.sleep( 1000 );

        Mockito.verify( script1, Mockito.times( 3 ) ).run();
        Mockito.verify( script2, Mockito.times( 1 ) ).run();
    }

    @Test
    public void testStop()
        throws Exception
    {
        final JobDescriptor job1 = mockJob( "jobName1", Duration.ofMillis( 100000 ) );
        final JobDescriptor job2 = mockJob( "jobName2", Duration.ofMillis( 100000 ) );
        final JobDescriptor job3 = mockJob( "jobName3", Duration.ofMillis( 100000 ) );

        this.scheduler.schedule( job1 );
        this.scheduler.schedule( job2 );
        this.scheduler.schedule( job3 );

        Thread.sleep( 1000 );

        this.scheduler.unschedule( job1.getName() );

        assertEquals( 2, this.scheduler.list( "jobName\\d+" ).size() );
        this.scheduler.deactivate();
        assertEquals( 0, this.scheduler.list( "jobName\\d+" ).size() );
    }

    @Test
    public void testStopMultipleWithSameName()
        throws Exception
    {
        final JobDescriptor job1 = mockJob( "jobName1", Duration.ofMillis( 100000 ) );
        final JobDescriptor job2 = mockJob( "jobName1", Duration.ofMillis( 100000 ) );
        final JobDescriptor job3 = mockJob( "jobName1", Duration.ofMillis( 100000 ) );
        final JobDescriptor job4 = mockJob( "jobName2", Duration.ofMillis( 100000 ) );

        this.scheduler.schedule( job1 );
        this.scheduler.schedule( job2 );
        this.scheduler.schedule( job3 );
        this.scheduler.schedule( job4 );

        Thread.sleep( 1000 );

        this.scheduler.unschedule( job1.getName() );

        assertEquals( 1, this.scheduler.list( "jobName\\d+" ).size() );
    }

    @Test
    public void testStopAlreadyFinished()
        throws Exception
    {
        final JobDescriptor job = mockJob( "jobName", Duration.ofMillis( 10 ) );

        this.scheduler.schedule( job );

        Thread.sleep( 1000 );

        this.scheduler.unschedule( job.getName() );
    }


    @Test
    public void testRunInvalidScript()
        throws Exception
    {
        final Runnable script = () -> {
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
        };

        final JobDescriptor job = mockJob( "jobName1", Duration.ofMillis( 10 ), 1, script );

        this.scheduler.schedule( job );

        Thread.sleep( 1000 );
    }


    private JobDescriptor mockJob( final String jobName, final Duration nextExec )
    {
        return this.mockJob( jobName, nextExec, 1, () -> {
        } );
    }

    private JobDescriptor mockJob( final String jobName, final Duration nextExec, final int times, final Runnable script )
    {
        final JobDescriptor job = Mockito.mock( JobDescriptor.class );
        Mockito.when( job.getCron() ).thenReturn( "* * * * *" );
        Mockito.when( job.nextExecution() ).thenReturn( nextExec );
        Mockito.when( job.getName() ).thenReturn( jobName );
        Mockito.when( job.getTimes() ).thenReturn( times );
        Mockito.when( job.getContext() ).thenReturn( ContextAccessor.current() );
        Mockito.when( job.getScript() ).thenReturn( script );

        return job;
    }

    private JobDescriptor mockJob( final String jobName, final Integer initialDelay, final Integer fixedDelay, final Integer times,
                                   final Runnable script )
    {
        final JobDescriptor job = Mockito.mock( JobDescriptor.class );
        Mockito.when( job.getDelay() ).thenReturn( initialDelay );
        Mockito.when( job.getFixedDelay() ).thenReturn( fixedDelay );
        Mockito.when( job.getName() ).thenReturn( jobName );
        Mockito.when( job.getTimes() ).thenReturn( times );
        Mockito.when( job.getContext() ).thenReturn( ContextAccessor.current() );
        Mockito.when( job.getScript() ).thenReturn( script );

        return job;
    }
}

