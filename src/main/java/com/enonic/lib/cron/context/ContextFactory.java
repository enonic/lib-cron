package com.enonic.lib.cron.context;

import java.util.Map;
import java.util.concurrent.Callable;

import com.enonic.lib.cron.model.params.ContextParams;
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


public final class ContextFactory
{
    private final SecurityService securityService;

    private final Context defaultContext;

    public ContextFactory( final SecurityService securityService, final Context context )
    {
        this.securityService = securityService;
        this.defaultContext = context != null ? context : ContextAccessor.current();
    }

    public Context create( final ContextParams params )
    {
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
}
