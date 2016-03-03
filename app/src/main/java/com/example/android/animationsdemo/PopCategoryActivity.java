/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.animationsdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;

public class PopCategoryActivity extends FragmentActivity implements View.OnClickListener{
    public static final String TAG = "PopCategoryActivity";
    CallbackManager callbackManager;
    TextView mNaviBar;
    FloatCategory mFloatCategory;
    GATracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DKLog.d(TAG, Trace.getCurrentMethod());

        // Obtain the shared Tracker instance.
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();


        // 1.initial
        FacebookSdk.sdkInitialize(this);
        callbackManager = CallbackManager.Factory.create();

        // 2.setView
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_category);
        // 3.get ID
        mNaviBar = (TextView) findViewById(R.id.navi_category);
        mNaviBar.setOnClickListener(this);

        mFloatCategory = FloatCategory.getInstance();
        mFloatCategory.init(this, mNaviBar);
        mFloatCategory.setOnCloseListener(mFloatDialogCloseListener);

        DKLog.d(TAG, SharedPrefsData.getEmail(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        //mTracker.setScreenName(TAG);
        //mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    FloatCategory.OnCloseListener mFloatDialogCloseListener = new FloatCategory.OnCloseListener(){
        @Override
        public void onClose() {
            DKLog.d(TAG, Trace.getCurrentMethod());
        }
    };

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.navi_category:
                //mTracker.send(new HitBuilders.EventBuilder("TESTLogin","TESTLoginOK").build());
                //mTracker.sendEvents("Login", "LoginOK");
                mFloatCategory.show();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        DKLog.d(TAG, Trace.getCurrentMethod() +
                String.format("requestCode: %s, resultCode: %s",
                        requestCode, resultCode));
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
