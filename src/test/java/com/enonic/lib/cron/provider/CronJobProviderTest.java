package com.enonic.lib.cron.provider;

import java.util.concurrent.ScheduledFuture;

import org.junit.jupiter.api.Test;

import com.enonic.lib.cron.model.params.ScheduleParams;
import com.enonic.lib.cron.scheduler.JobExecutorService;
import com.enonic.lib.cron.scheduler.JobScheduler;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.auth.AuthenticationInfo;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class CronJobProviderTest
{
    @Test
    public void registryIsSharedAcrossProviders()
    {
        final ScheduledFuture<?> firstFuture = mock( ScheduledFuture.class );
        final ScheduledFuture<?> secondFuture = mock( ScheduledFuture.class );
        final JobExecutorService executorService = mock( JobExecutorService.class );
        doReturn( firstFuture, secondFuture ).when( executorService ).scheduleWithFixedDelay( any(), anyLong(), anyLong(), any() );

        final JobScheduler jobScheduler = new JobScheduler( executorService );

        final Context context = ContextBuilder.create()
            .branch( Branch.from( "draft" ) )
            .repositoryId( RepositoryId.from( "com.enonic.cms.default" ) )
            .authInfo( AuthenticationInfo.unAuthenticated() )
            .build();
        final SecurityService securityService = mock( SecurityService.class );

        // two providers, as two script contexts of the same application create them
        final CronJobProvider provider1 = new CronJobProvider( context, securityService, jobScheduler );
        final CronJobProvider provider2 = new CronJobProvider( context, securityService, jobScheduler );

        provider1.schedule( newParams( "myJob" ) );
        assertNotNull( provider2.get( "myJob" ) );

        // a same-name schedule through another provider replaces the job instead of duplicating it
        provider2.schedule( newParams( "myJob" ) );
        verify( firstFuture ).cancel( true );

        provider2.unschedule( "myJob" );
        verify( secondFuture ).cancel( true );
        assertNull( provider1.get( "myJob" ) );
    }

    private static ScheduleParams newParams( final String name )
    {
        return new ScheduleParams().setName( name ).setFixedDelay( 100 ).setScript( () -> {
        } ).setApplicationKey( "myapplication" );
    }
}
