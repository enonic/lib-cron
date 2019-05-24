package com.enonic.lib.cron.context;

import com.enonic.lib.cron.service.params.ContextParams;
import com.enonic.xp.context.Context;

public interface ContextFactory
{
    Context create( final ContextParams params, final Context defaultContext );
}
