package com.example.android.animationsdemo;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

/**
 * This is a subclass of {@link Application} used to provide shared objects for this app, such as
 * the {@link Tracker}.
 */
public class AnalyticsApplication extends Application {
    private GATracker mTracker;

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    synchronized public GATracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            analytics.setLocalDispatchPeriod(1);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            Tracker tracker = analytics.newTracker(R.xml.global_tracker);
            mTracker = new GATracker(tracker);
        }
        return mTracker;
    }
}