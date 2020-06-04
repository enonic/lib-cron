package com.enonic.lib.cron.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.security.auth.AuthenticationToken;
import com.enonic.xp.testing.ScriptTestSupport;

public class LibCronHandlerTest
    extends ScriptTestSupport
{
    private SecurityService securityService;

    @BeforeEach
    public void initialize()
        throws Exception
    {
        super.initialize();

        this.securityService = Mockito.mock( SecurityService.class );
        addService( SecurityService.class, this.securityService );
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
        Mockito.when( this.securityService.authenticate( Mockito.isA( AuthenticationToken.class ) ) ).
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
