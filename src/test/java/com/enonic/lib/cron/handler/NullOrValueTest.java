package com.enonic.lib.cron.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.lib.cron.scheduler.JobExecutorService;
import com.enonic.lib.cron.scheduler.JobScheduler;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.testing.ScriptTestSupport;

import static org.mockito.Mockito.mock;

public class NullOrValueTest
    extends ScriptTestSupport
{
    private Context defaultContext;

    @BeforeEach
    public void initialize()
        throws Exception
    {
        super.initialize();

        addService( SecurityService.class, mock( SecurityService.class ) );
        addService( JobScheduler.class, new JobScheduler( mock( JobExecutorService.class ) ) );

        this.defaultContext = ContextBuilder.create()
            .branch( Branch.from( "draft" ) )
            .repositoryId( RepositoryId.from( "com.enonic.cms.default" ) )
            .authInfo( AuthenticationInfo.unAuthenticated() )
            .build();
    }

    @Test
    public void undefinedBecomesNull()
    {
        defaultContext.runWith( () -> runFunction( "/test/NullOrValueTest.js", "undefinedBecomesNull" ) );
    }

    @Test
    public void nullStaysNull()
    {
        defaultContext.runWith( () -> runFunction( "/test/NullOrValueTest.js", "nullStaysNull" ) );
    }

    @Test
    public void valuesPassThrough()
    {
        defaultContext.runWith( () -> runFunction( "/test/NullOrValueTest.js", "valuesPassThrough" ) );
    }
}
