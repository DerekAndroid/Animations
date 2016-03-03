package com.example.android.animationsdemo;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by derekchang on 2015/7/21.
 */
public class SlideFirstViewPager extends ViewPager {
    public static final String TAG = "SlideFirstViewPager";
    public SlideFirstViewPager(Context context) {
        super(context);
    }

    public SlideFirstViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

//    public boolean onInterceptTouchEvent(MotionEvent event) {
//        int action = event.getAction();
//        switch(action) {
//            case MotionEvent.ACTION_DOWN:
//                DKLog.i(TAG, Trace.getCurrentMethod() + "InterceptAction = DOWN");
//                break;
//            case MotionEvent.ACTION_MOVE:
//                DKLog.i(TAG, Trace.getCurrentMethod() + "InterceptAction = MOVE");
//                break;
//            case MotionEvent.ACTION_CANCEL:
//                DKLog.i(TAG, Trace.getCurrentMethod() + "InterceptAction = CANCEL");
//                return false;
//        }
//        return true;
//        //returning true tells your main Activity that you want the custom GridView to handle this TouchEvent;
//        // It will then send the TouchEvent to your GridView's onTouchEvent() method for handling.
//    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch(action) {
            case MotionEvent.ACTION_MOVE:
//                int xCoord = (int) event.getX();
//                int yCoord = (int) event.getY();
//                Log.i(TAG, "MOVE EVENT;" + "\n" + "Touch X = " + Integer.toString(xCoord) + "\n" +
//                        "Touch Y = " + Integer.toString(yCoord));
                break;
        }
        return super.onTouchEvent(event);
    }


}
