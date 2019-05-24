package com.enonic.lib.cron.context;

import java.util.Map;
import java.util.concurrent.Callable;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.lib.cron.service.params.ContextParams;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityConstants;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.security.auth.VerifiedUsernameAuthToken;


@Component(immediate = true)
public final class ContextFactoryImpl
    implements ContextFactory
{
    private final static Logger LOG = LoggerFactory.getLogger( ContextFactoryImpl.class );

    private SecurityService securityService;

    private Context defaultContext;

    @Override
    public Context create( final ContextParams params, final Context defaultContext )
    {
        this.defaultContext = defaultContext != null ? defaultContext : ContextAccessor.current();

        return parseContext( params );
    }

    private Context parseContext( final ContextParams params )
    {
        final ContextBuilder builder = ContextBuilder.from( this.defaultContext );

        applyRepository( builder, params.getRepository() );
        applyAuthInfo( builder, params.getUsername(), params.getUserStore(), params.getPrincipals() );
        applyBranch( builder, params.getBranch() );
        addAttributes( builder, params.getAttributes() );

        return builder.build();
    }

    private void applyRepository( final ContextBuilder builder, final String repository )
    {
        if ( repository == null )
        {
            return;
        }
        builder.repositoryId( repository );
    }

    private void applyAuthInfo( final ContextBuilder builder, final String username, final String userStore,
                                final PrincipalKey[] principals )
    {
        AuthenticationInfo authInfo = this.defaultContext.getAuthInfo();
        if ( username != null )
        {
            authInfo = runAsAuthenticated( () -> getAuthenticationInfo( username, userStore ) );
        }
        if ( principals != null )
        {
            authInfo = AuthenticationInfo.
                copyOf( authInfo ).
                principals( principals ).
                build();
        }

        builder.authInfo( authInfo );
    }

    private void applyBranch( final ContextBuilder builder, final String branch )
    {
        if ( branch == null )
        {
            return;
        }

        builder.branch( branch );
    }

    private void addAttributes( final ContextBuilder builder, final Map<String, Object> attributes )
    {
        if ( attributes != null )
        {
            for ( Map.Entry<String, Object> attribute : attributes.entrySet() )
            {
                builder.attribute( attribute.getKey(), attribute.getValue() );
            }
        }
    }

    private AuthenticationInfo getAuthenticationInfo( final String username, final String idProvider )
    {
        final VerifiedUsernameAuthToken token = new VerifiedUsernameAuthToken();
        token.setUsername( username );
        token.setIdProvider( idProvider == null ? null : IdProviderKey.from( idProvider ) );
        return this.securityService.authenticate( token );
    }

    private <T> T runAsAuthenticated( final Callable<T> runnable )
    {
        final AuthenticationInfo authInfo = AuthenticationInfo.create().principals( RoleKeys.AUTHENTICATED ).user( User.ANONYMOUS ).build();
        return ContextBuilder.from( this.defaultContext ).
            authInfo( authInfo ).
            repositoryId( SecurityConstants.SECURITY_REPO.getId() ).
            branch( SecurityConstants.BRANCH_SECURITY ).build().
            callWith( runnable );
    }

    @Reference
    public void setSecurityService( final SecurityService securityService )
    {
        this.securityService = securityService;
    }
}
