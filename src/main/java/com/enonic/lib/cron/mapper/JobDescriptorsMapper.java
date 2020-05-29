package com.enonic.lib.cron.mapper;

import java.util.List;

import com.enonic.lib.cron.model.JobDescriptor;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public final class JobDescriptorsMapper
    implements MapSerializable
{
    private final List<JobDescriptor> jobDescriptors;

    public JobDescriptorsMapper( final List<JobDescriptor> jobDescriptors )
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
