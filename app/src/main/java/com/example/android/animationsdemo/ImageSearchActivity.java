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
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.HashMap;

public class ImageSearchActivity extends FragmentActivity implements
        View.OnClickListener,
        BaseSliderView.OnSliderClickListener,
        ViewPagerEx.OnPageChangeListener{
    public static final String TAG = "ImageSearchActivity";
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mLsvDrawerMenu;
    private Button mSave;
    private Button mLoad;
    private ArrayList<SAMPLE> mSampleList = new ArrayList<>();
    private SliderLayout mDemoSlider;
    class SAMPLE{
        public int id;
        public String name;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DKLog.d(TAG, Trace.getCurrentMethod());
        // 2.setView
        setContentView(R.layout.activity_image_slider);
        // 3.get ID
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drw_layout);
        mLsvDrawerMenu = (ListView) findViewById(R.id.lsv_drawer_menu);
        mSave = (Button) findViewById(R.id.save_button);
        mLoad = (Button) findViewById(R.id.load_button);
        mDemoSlider = (SliderLayout)findViewById(R.id.slider);

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

        // 設定 image slider
        HashMap<String,String> url_maps = new HashMap<String, String>();
        url_maps.put("Hannibal", "http://static2.hypable.com/wp-content/uploads/2013/12/hannibal-season-2-release-date.jpg");
        url_maps.put("Big Bang Theory", "http://tvfiles.alphacoders.com/100/hdclearart-10.png");
        url_maps.put("House of Cards", "http://cdn3.nflximg.net/images/3093/2043093.jpg");
        url_maps.put("Game of Thrones", "http://images.boomsbeat.com/data/images/full/19640/game-of-thrones-season-4-jpg.jpg");
        for(String name : url_maps.keySet()){
            OnlyTextSliderView textSliderView = new OnlyTextSliderView(this);
            // initialize a SliderLayout
            textSliderView
                    .description(name)
                    .image(url_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);
            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra",name);

            mDemoSlider.addSlider(textSliderView);
        }
    }

    public class OnlyTextSliderView extends BaseSliderView {
        public OnlyTextSliderView(Context context) {
            super(context);
        }

        public View getView() {
            View v = LayoutInflater.from(this.getContext()).inflate(com.daimajia.slider.library.R.layout.render_type_text, (ViewGroup)null);
            ImageView target = (ImageView)v.findViewById(com.daimajia.slider.library.R.id.daimajia_slider_image);
            LinearLayout textBackground = (LinearLayout)v.findViewById(com.daimajia.slider.library.R.id.description_layout);
            TextView description = (TextView)v.findViewById(com.daimajia.slider.library.R.id.description);
            description.setText(this.getDescription());
            textBackground.setVisibility(View.GONE);
            this.bindEventAndShow(v, target);
            return v;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        mDemoSlider.stopAutoCycle();
        super.onStop();
    }

    /**
     *
     * @param question image search term
     * @param ua user agent of the browser
     * @return
     */
    private String findImage(String question, String ua) {
        String finRes = "";
        try {
            String googleUrl = "https://www.google.com/search?tbm=isch&q=" + question.replace(",", "");
            Document doc1 = Jsoup.connect(googleUrl).userAgent(ua).timeout(10 * 1000).get();
            DKLog.d(TAG, Trace.getCurrentMethod() + doc1);
            Element media = doc1.select("[data-src]").first();
            String finUrl = media.attr("abs:data-src");
            finRes= "<a href=\"http://images.google.com/search?tbm=isch&q=" + question + "\"><img src=\"" + finUrl.replace("&quot", "") + "\" border=1/></a>";
            DKLog.d(TAG, Trace.getCurrentMethod() + finRes);
        } catch (Exception e) {
            DKLog.e(TAG, Trace.getCurrentMethod() + e.toString());
        }

        return finRes;
    }

    class AyncsFindImageTask extends AsyncTask<Void,Void,Void> {
        String mKeyword = "";
        public AyncsFindImageTask(String mKeyword){
            this.mKeyword = mKeyword;
        }
        @Override
        protected Void doInBackground(Void... params) {
            findImage(mKeyword, "");
            return null;
        }
    }

    private static final String FILE_LOCATION_HISTORY = "LocationHistory";
    private static final String KEY_LOCATION_HISTORY = "SharedPrefsData.KEY_LOCATION_HISTORY";
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.navi_category:

                break;
            case R.id.save_button:
                new AyncsFindImageTask("nba").execute();
                break;
            case R.id.load_button:


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

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {

    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Toast.makeText(this, slider.getBundle().get("extra") + "", Toast.LENGTH_SHORT).show();
    }
}
