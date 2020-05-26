package com.enonic.lib.cron.model;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JobDescriptorsTest
{
    @Test
    public void testSimple()
    {
        final JobDescriptor descriptor = Mockito.mock( JobDescriptor.class );

        final JobDescriptors descriptors = new JobDescriptors();
        descriptors.add( descriptor );
        assertEquals( 1, descriptors.size() );
    }
}
