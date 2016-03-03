package com.example.android.animationsdemo;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by derekchang on 2015/9/22.
 */
public class GATracker {
    public static final String TAG = "GATracker";
    private Tracker tracker = null;
    boolean isInited = false;

    public GATracker(Tracker tracker){
        this.tracker = tracker;
        isInited = true;
    }


    public void sendEvents(String category, String action){
        DKLog.d(TAG, Trace.getCurrentMethod()
                + "category: "  + category  + " | "
                + "action: "    + action);
        if(isInited){
            // Build and send an Event.
            tracker.send(new HitBuilders.EventBuilder()
                    .setCategory(category)
                    .setAction(action)
                    .build());
        }
    }

    public void sendEvents(String category, String action, String label){
        DKLog.d(TAG, Trace.getCurrentMethod()
                + "category: "  + category  + " | "
                + "action: "    + action    + " | "
                + "label: "     + label);
        if(isInited){
            // Build and send an Event.
            tracker.send(new HitBuilders.EventBuilder()
                    .setCategory(category)
                    .setAction(action)
                    .setLabel(label)
                    .build());
        }
    }

    public void sendEvents(String category, String action, String label, Long value){
        DKLog.d(TAG, Trace.getCurrentMethod()
                + "category: "  + category  + " | "
                + "action: "    + action    + " | "
                + "label: "     + label     + " | "
                + "value: "     + value);
        if(isInited){
            // Build and send an Event.
            tracker.send(new HitBuilders.EventBuilder()
                    .setCategory(category)
                    .setAction(action)
                    .setLabel(label)
                    .setValue(value)
                    .build());
        }
    }

    public void setScreenName(String name){
        DKLog.d(TAG, Trace.getCurrentMethod() + name);
        if(isInited){
            tracker.setScreenName(name);
            tracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }


}
