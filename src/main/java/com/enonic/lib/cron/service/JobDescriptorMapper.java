package com.enonic.lib.cron.service;

import com.enonic.lib.cron.model.JobDescriptor;
import com.enonic.lib.cron.scheduler.JobScheduler;
import com.enonic.xp.context.Context;
import com.enonic.xp.lib.common.PrincipalMapper;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

public final class JobDescriptorMapper
    implements MapSerializable
{
    private final JobDescriptor jobDescriptor;

    public JobDescriptorMapper( final JobDescriptor jobDescriptor )
    {
        this.jobDescriptor = jobDescriptor;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "name", this.jobDescriptor.getName() );
        gen.value( "cron", this.jobDescriptor.getCron() );
        gen.value( "applicationKey", this.jobDescriptor.getApplicationKey().toString() );
        serializeContext( gen, this.jobDescriptor.getContext() );
    }

    private void serializeContext( final MapGenerator gen, final Context context )
    {
        if ( context == null )
        {
            return;
        }

        gen.map( "context" );
        new ContextMapper( context ).serialize( gen );
        gen.end();
    }
}
