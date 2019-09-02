package com.enonic.lib.cron.mapper;

import com.enonic.lib.cron.model.JobDescriptors;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public final class JobDescriptorsMapper
    implements MapSerializable
{
    private final JobDescriptors jobDescriptors;

    public JobDescriptorsMapper( final JobDescriptors jobDescriptors )
    {
        this.jobDescriptors = jobDescriptors;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.array( "jobs" );
        if ( jobDescriptors != null && jobDescriptors.size() > 0 )
        {
            jobDescriptors.forEach( jobDescriptor -> {
                gen.map();
                new JobDescriptorMapper( jobDescriptor ).serialize( gen );
                gen.end();
            } );
        }
        gen.end();

    }
}
