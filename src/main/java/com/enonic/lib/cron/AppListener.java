package com.enonic.lib.cron;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;

import com.enonic.lib.cron.model.JobDescriptors;
import com.enonic.lib.cron.scheduler.JobScheduler;
import com.enonic.xp.app.ApplicationKey;

@Component(immediate = true)
public final class AppListener
    implements BundleTrackerCustomizer<JobDescriptors>
{
    private BundleTracker<JobDescriptors> tracker;

    private JobScheduler jobScheduler;

    @Activate
    public void activate( final BundleContext context )
    {
        this.tracker = new BundleTracker<>( context, Bundle.ACTIVE, this );
        this.tracker.open();
    }

    @Deactivate
    public void deactivate( final BundleContext context )
    {
        this.tracker.close();

        stopBundleJobs( context.getBundle() );
    }

    @Override
    public JobDescriptors addingBundle( final Bundle bundle, final BundleEvent event )
    {
        // Do nothing
        return null;
    }

    @Override
    public void modifiedBundle( final Bundle bundle, final BundleEvent event, final JobDescriptors jobs )
    {
        // Do nothing
    }

    @Override
    public void removedBundle( final Bundle bundle, final BundleEvent event, final JobDescriptors jobs )
    {
        stopBundleJobs( bundle );
    }

    private void stopBundleJobs( final Bundle bundle )
    {
        final ApplicationKey key = ApplicationKey.from( bundle );
        this.jobScheduler.unscheduleByKey( key );
    }

    @Reference
    public void setJobScheduler( final JobScheduler jobScheduler )
    {
        this.jobScheduler = jobScheduler;
    }
}
