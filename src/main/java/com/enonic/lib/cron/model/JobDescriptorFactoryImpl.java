package com.enonic.lib.cron.model;

import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

@Component(immediate = true)
public final class JobDescriptorFactoryImpl
    implements JobDescriptorFactory
{
    private final static Logger LOG = LoggerFactory.getLogger( JobDescriptorFactoryImpl.class );

    @Override
    public JobDescriptor create( final String name, final String cron, final ScriptObjectMirror script )
    {
        return parseJob( name, cron, script);
    }

    private JobDescriptor parseJob( final String name, final String cron, final ScriptObjectMirror script )
    {
        if ( Strings.isNullOrEmpty( name ) )
        {
            LOG.error( "Failed to create job descriptor, name is empty");
            return null;
        }

        if ( Strings.isNullOrEmpty( cron ) )
        {
            LOG.error( "Failed to create job descriptor, cron string is empty");
            return null;
        }

        if ( script == null )
        {
            LOG.error( "Failed to create job descriptor, callback script is empty");
            return null;
        }

        return JobDescriptorImpl.builder().
            name( name ).
            cron( cron ).
            script(script).
            build();
    }
}
