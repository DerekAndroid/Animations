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

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Demonstrates a "screen-slide" animation using a {@link ViewPager}. Because {@link ViewPager}
 * automatically plays such an animation when calling {@link ViewPager#setCurrentItem(int)}, there
 * isn't any animation-specific code in this sample.
 *
 * <p>This sample shows a "next" button that advances the user to the next step in a wizard,
 * animating the current screen out (to the left) and the next screen in (from the right). The
 * reverse animation is played when the user presses the "previous" button.</p>
 *
 * @see ScreenSlidePageFragment
 */
public class GirdPagerSearchBarActivity extends FragmentActivity implements View.OnClickListener{
    public static final String TAG = "GirdPagerSearchBarActivity";
    private Context context = GirdPagerSearchBarActivity.this;
    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int NUM_PAGES = 4;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private SlideFirstViewPager mInnerPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private ScreenSlidePagerAdapter mPagerAdapter;

    private Button mPrev, mNext;
    private HeaderGridView mHeaderGridView;
    private String[] mPlaces = {
            "cat", "flower", "hippo", "monkey", "mushro", "panda", "rabbit", "raccon",
            "cat", "flower", "hippo", "monkey", "mushro", "panda", "rabbit", "raccon",
            "cat", "flower", "hippo", "monkey", "mushro", "panda", "rabbit", "raccon",
            "cat", "flower", "hippo", "monkey", "mushro", "panda", "rabbit", "raccon",
            "cat", "flower", "hippo", "monkey", "mushro", "panda", "rabbit", "raccon",
            "cat", "flower", "hippo", "monkey", "mushro", "panda", "rabbit", "raccon",
            "cat", "flower", "hippo", "monkey", "mushro", "panda", "rabbit", "raccon",
    };
    private final static String[] VIDEO_PATHS = {
            "https://s3-us-west-2.amazonaws.com/swapub-8080/Uploads/Banner/banner/360.mp4",
            "http://sample-videos.com/video/mp4/720/big_buck_bunny_720p_1mb.mp4",
            "http://sample-videos.com/video/mp4/480/big_buck_bunny_480p_1mb.mp4",
            "https://s3-us-west-2.amazonaws.com/swapub-8080/Uploads/Banner/banner/360.mp4",
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_slide);
        LayoutInflater inflater = getLayoutInflater();
        // Inner ViewPager
        View headerview = inflater.inflate(R.layout.gridview_header, null);
        mInnerPager = (SlideFirstViewPager) headerview.findViewById(R.id.inner_pager);
        mPrev = (Button) findViewById(R.id.prev);
        mNext = (Button) findViewById(R.id.next);

        mHeaderGridView = (HeaderGridView) findViewById(R.id.gridView);
        ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();

        for(int i=0; i<mPlaces.length; i++){
            HashMap<String,String> item = new HashMap<String,String>();
            item.put( "food", mPlaces[i]);
            item.put( "place",mPlaces[i] );
            list.add( item );
        }
        SimpleAdapter adapter = new SimpleAdapter(
                this,
                list,
                android.R.layout.simple_list_item_2,
                new String[] { "food","place" },
                new int[] { android.R.id.text1, android.R.id.text2 } );
        TextAdapter mAdapter = new TextAdapter();

        mPrev.setOnClickListener(this);
        mNext.setOnClickListener(this);

        mPagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager(), VIDEO_PATHS);
        mInnerPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                DKLog.d(TAG, Trace.getCurrentMethod() + position);
                mPagerAdapter.setFocus(position);
                invalidateOptionsMenu();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        mInnerPager.setAdapter(mPagerAdapter);
        mInnerPager.setOffscreenPageLimit(NUM_PAGES);
        TextView mTextView = new TextView(getApplicationContext());
        mTextView.setText("OOOOOOOOOOOOOOOOOOOOO");
        mTextView.setTextSize(10);
        mTextView.setSingleLine(true);
        mHeaderGridView.addHeaderView(headerview, "ViewPager");
        mHeaderGridView.setAdapter(mAdapter);
        mHeaderGridView.setNumColumns(3);

        // Start Download
//        for(int i = 0 ; i < VIDEO_PATHS.length ; i++) {
//            DownloadService.downloadVideo(context, VIDEO_PATHS[i], new DownloadReceiver(new Handler()));
//        }
    }

    private class TextAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return mPlaces.length;
        }

        @Override
        public Object getItem(int i) {
            return mPlaces[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView mTextView;
            if (convertView == null) {
                mTextView = new TextView(getApplicationContext());
                mTextView.setTextSize(30);
            } else {
                mTextView = (TextView) convertView;
            }
            mTextView.setText(mPlaces[position]);
            return mTextView;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_screen_slide, menu);

        menu.findItem(R.id.action_previous).setEnabled(mInnerPager.getCurrentItem() > 0);
        mPrev.setEnabled(mInnerPager.getCurrentItem() > 0);

        // Add either a "next" or "finish" button to the action bar, depending on which page
        // is currently selected.
        MenuItem item = menu.add(Menu.NONE, R.id.action_next, Menu.NONE,
                (mInnerPager.getCurrentItem() == mPagerAdapter.getCount() - 1)
                        ? R.string.action_finish
                        : R.string.action_next);
        mNext.setEnabled(!(mInnerPager.getCurrentItem() == mPagerAdapter.getCount() - 1));
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Navigate "up" the demo structure to the launchpad activity.
                // See http://developer.android.com/design/patterns/navigation.html for more.
                NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
                return true;

            case R.id.action_previous:
                // Go to the previous step in the wizard. If there is no previous step,
                // setCurrentItem will do nothing.
                mInnerPager.setCurrentItem(mInnerPager.getCurrentItem() - 1, true);
                return true;

            case R.id.action_next:
                // Advance to the next step in the wizard. If there is no next step, setCurrentItem
                // will do nothing.
                mInnerPager.setCurrentItem(mInnerPager.getCurrentItem() + 1, true);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.prev:
                mInnerPager.setCurrentItem(mInnerPager.getCurrentItem() - 1);
                break;
            case R.id.next:
                mInnerPager.setCurrentItem(mInnerPager.getCurrentItem() + 1);
                break;
        }
    }


    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        String[] paths = {};
        Map<Integer, Fragment> mPageReferenceMap = new HashMap<Integer, Fragment>();
        int currentFocusPos = 0;
        public ScreenSlidePagerAdapter(FragmentManager fm, String[] paths) {
            super(fm);
            this.paths = paths;
        }

        @Override
        public Fragment getItem(int position) {
            DKLog.e(TAG, Trace.getCurrentMethod() + position);
            Fragment f = mPageReferenceMap.get(position);
            if(f == null){
                f = ScreenSlidePageFragment.create(position, paths[position]);
                mPageReferenceMap.put(position, f);
            }
            // init pager focus
            if(position == currentFocusPos){
                ((ScreenSlidePageFragment) f).isFocus(true);
            }
            return f;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            mPageReferenceMap.remove(position);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        public void setFocus(int pos){
            DKLog.d(TAG, Trace.getCurrentMethod() + pos);
            currentFocusPos = pos;
            for(int i = 0 ; i < getCount() ; i ++) {
                Fragment f = mPageReferenceMap.get(i);
                if (i == pos) {
                    if(f == null)return;
                    ((ScreenSlidePageFragment) f).isFocus(true);
                }else{
                    if(f == null)return;
                    ((ScreenSlidePageFragment) f).isFocus(false);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //DownloadService.stopDownloadvideo(context);
        DownloadService.deleteBannerVideo(context);
    }

}
