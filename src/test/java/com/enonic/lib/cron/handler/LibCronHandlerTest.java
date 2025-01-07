package com.enonic.lib.cron.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.enonic.lib.cron.scheduler.JobExecutorService;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.security.auth.AuthenticationToken;
import com.enonic.xp.testing.ScriptTestSupport;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LibCronHandlerTest
    extends ScriptTestSupport
{
    private SecurityService securityService;

    @BeforeEach
    public void initialize()
        throws Exception
    {
        super.initialize();

        this.securityService = mock( SecurityService.class );
        addService( SecurityService.class, this.securityService );

        JobExecutorService jobExecutorService = mock( JobExecutorService.class );

        when( jobExecutorService.scheduleWithFixedDelay( any(), anyLong(), anyLong(), any() ) ).thenReturn( mock() );
        when( jobExecutorService.schedule( any(), anyLong(), any() ) ).thenReturn( mock() );
        addService( JobExecutorService.class, jobExecutorService );
    }

    @Test
    public void testScheduleWithDelay()
    {
        runFunction( "/test/LibCronHandlerTest.js", "scheduleWithDelay" );
    }

    @Test
    public void testScheduleWithCron()
    {
        runFunction( "/test/LibCronHandlerTest.js", "scheduleWithCron" );
    }

    @Test
    public void testScheduleWithContext()
    {
        when( this.securityService.authenticate( Mockito.isA( AuthenticationToken.class ) ) ).
            thenReturn( AuthenticationInfo.create().
                user( User.create().
                    login( "test-user" ).
                    key( PrincipalKey.from( "user:system:test-user" ) ).
                    build() ).
                build() );

        runFunction( "/test/LibCronHandlerTest.js", "scheduleWithContext" );
    }

    @Test
    public void testUnschedule()
    {
        runFunction( "/test/LibCronHandlerTest.js", "unschedule" );
    }

    @Test
    public void testList()
    {
        runFunction( "/test/LibCronHandlerTest.js", "list" );
    }
}
