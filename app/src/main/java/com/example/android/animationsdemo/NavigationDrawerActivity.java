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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class NavigationDrawerActivity extends FragmentActivity implements View.OnClickListener{
    public static final String TAG = "NavigationDrawerActivity";
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mLsvDrawerMenu;
    private Button mSave;
    private Button mLoad;
    private ArrayList<SAMPLE> mSampleList = new ArrayList<>();
    class SAMPLE{
        public int id;
        public String name;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DKLog.d(TAG, Trace.getCurrentMethod());
        // 2.setView
        setContentView(R.layout.activity_navi_drawer);
        // 3.get ID
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drw_layout);
        mLsvDrawerMenu = (ListView) findViewById(R.id.lsv_drawer_menu);
        mSave = (Button) findViewById(R.id.save_button);
        mLoad = (Button) findViewById(R.id.load_button);

        for(int i = 0 ; i < 10 ; i++){
            SAMPLE sample = new SAMPLE();
            sample.id = i;
            sample.name = "Name: " + String.valueOf(i);
            mSampleList.add(sample);
        }


        // 設定 Drawer 的影子
        //mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        mSave.setOnClickListener(this);
        mLoad.setOnClickListener(this);

        mDrawerToggle = new ActionBarDrawerToggle (
                this,
                mDrawerLayout,    // 讓 Drawer Toggle 知道母體介面是誰
                R.drawable.ic_launcher, // Drawer 的 Icon
                R.string.common_open_on_phone, // Drawer 被打開時的描述
                R.string.description_zoom_touch_close // Drawer 被關閉時的描述
        ) {
            //被打開後要做的事情
            @Override
            public void onDrawerOpened(View drawerView) {
                // 將 Title 設定為自定義的文字
                //getSupportActionBar().setTitle(R.string.open_left_drawer);
            }

            //被關上後要做的事情
            @Override
            public void onDrawerClosed(View drawerView) {
                // 將 Title 設定回 APP 的名稱
                //getSupportActionBar().setTitle(R.string.app_name);
            }
        };
        // 設定清單的 Adapter，這裡直接使用 ArrayAdapter<String>
//        mLsvDrawerMenu.setAdapter(new ArrayAdapter<String>(
//                this,
//                R.layout.simple,  // 選單物件的介面
//                MENU_ITEMS                  // 選單內容
//        ));
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    private static final String FILE_LOCATION_HISTORY = "LocationHistory";
    private static final String KEY_LOCATION_HISTORY = "SharedPrefsData.KEY_LOCATION_HISTORY";
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.navi_category:

                break;
            case R.id.save_button:
                SharedPreferences spref = getSharedPreferences(FILE_LOCATION_HISTORY, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = spref.edit();
                editor.putString(KEY_LOCATION_HISTORY, mSampleList.toArray().toString());
                editor.apply();
                break;
            case R.id.load_button:
                SharedPreferences sharedPref = getSharedPreferences(FILE_LOCATION_HISTORY, Context.MODE_PRIVATE);
                String strJson = sharedPref.getString(KEY_LOCATION_HISTORY, "");

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        DKLog.d(TAG, Trace.getCurrentMethod() +
                String.format("requestCode: %s, resultCode: %s",
                        requestCode, resultCode));
        super.onActivityResult(requestCode, resultCode, data);
    }

}
