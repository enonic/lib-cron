package com.enonic.lib.cron.model.params;

import java.util.Map;

import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.security.PrincipalKey;

public final class ContextParams
{
    protected String repository;

    protected String branch;

    protected String username;

    protected String userStore;

    protected PrincipalKey[] principals;

    protected Map<String, Object> attributes;

    public void setRepository( final String repository )
    {
        this.repository = repository;
    }

    public void setBranch( final String branch )
    {
        this.branch = branch;
    }

    public void setUsername( final String username )
    {
        this.username = username;
    }

    public void setUserStore( final String userStore )
    {
        this.userStore = userStore;
    }

    public void setPrincipals( final String[] principals )
    {
        if ( principals == null )
        {
            this.principals = null;
        }
        else
        {
            this.principals = new PrincipalKey[principals.length];
            for ( int i = 0; i < principals.length; i++ )
            {
                this.principals[i] = PrincipalKey.from( principals[i] );
            }
        }
    }

    public void setAttributes( final ScriptValue attributes )
    {
        this.attributes = attributes.getMap();
    }

    public String getRepository()
    {
        return repository;
    }

    public String getBranch()
    {
        return branch;
    }

    public String getUsername()
    {
        return username;
    }

    public String getUserStore()
    {
        return userStore;
    }

    public PrincipalKey[] getPrincipals()
    {
        return principals;
    }

    public Map<String, Object> getAttributes()
    {
        return attributes;
    }
}
