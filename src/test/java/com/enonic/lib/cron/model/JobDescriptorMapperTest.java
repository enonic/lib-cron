package com.enonic.lib.cron.model;

import java.text.ParseException;
import java.time.format.DateTimeFormatter;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.lib.cron.mapper.JobDescriptorMapper;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.script.serializer.JsonMapGenerator;

public class JobDescriptorMapperTest
{
    @Test
    public void testSerialize()
        throws ParseException
    {
        final JobDescriptorImpl.Builder builder = JobDescriptorImpl.builder().
            name( "myJob" ).
            cron( "* * * * *" ).
            applicationKey( "appKey" ).
            context( ContextAccessor.current() );

        final ScriptEngine engine = new ScriptEngineManager().getEngineByName( "nashorn" );
        builder.script( () -> {
            try
            {
                engine.eval( "(function() { try { require('./invalid'); } catch (ex) { return ex.code; } })" );
            }
            catch ( ScriptException e )
            {
                e.printStackTrace();
            }
        } );

        final JobDescriptorImpl jobDescriptor = builder.build();

        final JsonMapGenerator jsonGenerator = new JsonMapGenerator();

        new JobDescriptorMapper( jobDescriptor ).serialize( jsonGenerator );

        final ObjectNode objectNode = (ObjectNode) jsonGenerator.getRoot();

        final DateTimeFormatter formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;

        Assertions.assertNotNull( formatter.parse( objectNode.get( "nextExecTime" ).textValue() ) );
    }

}
