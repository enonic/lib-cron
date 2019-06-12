package com.enonic.lib.cron.scheduler;

import java.time.Duration;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.lib.cron.model.JobDescriptor;
import com.enonic.lib.cron.model.JobDescriptors;
import com.enonic.lib.cron.runner.JobRunner;

public class JobSchedulerImplTest
{
    private JobSchedulerImpl scheduler;

    private JobRunner runner;

    @Before
    public void setup()
    {
        this.runner = Mockito.mock( JobRunner.class );
        this.scheduler = new JobSchedulerImpl();
        this.scheduler.setRunner( this.runner );
    }

    @Test
    public void testRun()
        throws Exception
    {
        final JobDescriptor job = Mockito.mock( JobDescriptor.class );
        Mockito.when( job.nextExecution() ).thenReturn( Duration.ofMillis( 190 ) );
        Mockito.when( job.getName() ).thenReturn( "jobName" );

        final JobDescriptors jobs = new JobDescriptors();
        jobs.add( job );

        this.scheduler.schedule( jobs );

        Thread.sleep( 200 );
        Mockito.verify( this.runner, Mockito.times( 1 ) ).run( job );

        this.scheduler.unschedule( job.getName() );
        this.scheduler.deactivate();
    }

    @Test
    public void testRun_nothing()
    {
        final JobDescriptors jobs = new JobDescriptors();
        this.scheduler.schedule( jobs );
        this.scheduler.unschedule( "not_scheduled" );
        this.scheduler.deactivate();
    }

    @Test
    public void testStop()
        throws Exception
    {
        final JobDescriptor job = Mockito.mock( JobDescriptor.class );
        Mockito.when( job.nextExecution() ).thenReturn( Duration.ofMillis( 100000 ) );
        Mockito.when( job.getName() ).thenReturn( "jobName" );
        Mockito.when( job.getTimes() ).thenReturn( Optional.of( 5 ) );

        this.scheduler.schedule( job );

        Thread.sleep( 200 );

        Assert.assertTrue( this.scheduler.unschedule( job.getName() ));
        this.scheduler.deactivate();
    }

    @Test
    public void testStopFailed()
        throws Exception
    {
        final JobDescriptor job = Mockito.mock( JobDescriptor.class );
        Mockito.when( job.nextExecution() ).thenReturn( Duration.ofMillis( 100 ) );
        Mockito.when( job.getName() ).thenReturn( "jobName" );
        Mockito.when( job.getTimes() ).thenReturn( Optional.of( 5 ) );

        this.scheduler.schedule( job );

        Thread.sleep( 1000 );

        Assert.assertFalse( this.scheduler.unschedule( job.getName() ));
        this.scheduler.deactivate();
    }
}

