package com.enonic.lib.cron.context;

import com.enonic.xp.context.Context;
import com.enonic.lib.cron.model.params.ContextParams;

public interface ContextFactory
{
    Context create( final ContextParams params, final Context defaultContext );
}
