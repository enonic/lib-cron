package com.enonic.lib.cron.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.enonic.lib.cron.scheduler.JobExecutorService;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.security.auth.AuthenticationToken;
import com.enonic.xp.testing.ScriptTestSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LibCronHandlerTest
    extends ScriptTestSupport
{
    private SecurityService securityService;

    private Context defaultContext;

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

        this.defaultContext = ContextBuilder.create()
            .branch( Branch.from( "draft" ) )
            .repositoryId( RepositoryId.from( "com.enonic.cms.default" ) )
            .authInfo( AuthenticationInfo.unAuthenticated() )
            .build();
    }

    @Test
    public void testScheduleWithDelay()
    {
        defaultContext.runWith( () -> runFunction( "/test/LibCronHandlerTest.js", "scheduleWithDelay" ) );
    }

    @Test
    public void testScheduleWithCron()
    {
        defaultContext.runWith( () -> runFunction( "/test/LibCronHandlerTest.js", "scheduleWithCron" ) );
    }

    @Test
    public void testScheduleWithContext()
    {
        final AuthenticationInfo authUser = AuthenticationInfo.create()
            .user( User.create()
                       .login( "test-user" )
                       .email( "test-user@example.no" )
                       .key( PrincipalKey.from( "user:system:test-user" ) )
                       .build() )
            .principals( PrincipalKey.from( "role:system.authenticated" ), PrincipalKey.from( "role:system.everyone" ) )
            .build();

        when( this.securityService.authenticate( Mockito.isA( AuthenticationToken.class ) ) ).thenReturn( authUser );

        defaultContext.runWith( () -> runFunction( "/test/LibCronHandlerTest.js", "scheduleWithContext" ) );

        final ArgumentCaptor<AuthenticationToken> captor = ArgumentCaptor.forClass( AuthenticationToken.class );
        verify( this.securityService ).authenticate( captor.capture() );
        assertEquals( IdProviderKey.from( "system" ), captor.getValue().getIdProvider());
    }

    @Test
    public void scheduleWithContextLegacy()
    {
        final AuthenticationInfo authUser = AuthenticationInfo.create()
            .user( User.create()
                       .login( "test-user" )
                       .email( "test-user@example.no" )
                       .key( PrincipalKey.from( "user:system:test-user" ) )
                       .build() )
            .principals( PrincipalKey.from( "role:system.authenticated" ), PrincipalKey.from( "role:system.everyone" ) )
            .build();

        when( this.securityService.authenticate( Mockito.isA( AuthenticationToken.class ) ) ).thenReturn( authUser );

        defaultContext.runWith( () -> runFunction( "/test/LibCronHandlerTest.js", "scheduleWithContextLegacy" ) );

        final ArgumentCaptor<AuthenticationToken> captor = ArgumentCaptor.forClass( AuthenticationToken.class );
        verify( this.securityService ).authenticate( captor.capture() );
        assertEquals( IdProviderKey.from( "system" ), captor.getValue().getIdProvider());
    }

    @Test
    public void testUnschedule()
    {
        defaultContext.runWith( () -> runFunction( "/test/LibCronHandlerTest.js", "unschedule" ) );
    }

    @Test
    public void testList()
    {
        defaultContext.runWith( () -> runFunction( "/test/LibCronHandlerTest.js", "list" ) );
    }
}
