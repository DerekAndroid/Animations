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
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.VideoView;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Locale;

public class RecentQueryActivity extends FragmentActivity implements View.OnClickListener{
    public static final String TAG = "RecentQueryActivity";
    CallbackManager callbackManager;
    Button mButton1, mButton2, mCheckButton;
    EditText latEditText, lngEditText;
    TextView resultTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 1.initial
        FacebookSdk.sdkInitialize(this);
        callbackManager = CallbackManager.Factory.create();
        // 2.setView
        setContentView(R.layout.activity_recent_query);
        // 3.get ID
        mButton1 = (Button) findViewById(R.id.checkbutton1);
        mButton2 = (Button) findViewById(R.id.checkbutton2);
        mCheckButton = (Button) findViewById(R.id.check_button);
        latEditText = (EditText) findViewById(R.id.lat_edittext);
        lngEditText = (EditText) findViewById(R.id.lng_edittext);
        resultTextView = (TextView) findViewById(R.id.geo_result);

        mButton1.setSelected(true);
        mButton2.setSelected(false);
        mButton1.setOnClickListener(this);
        mButton2.setOnClickListener(this);
        mCheckButton.setOnClickListener(this);

        VideoView videoView = (VideoView)findViewById(R.id.videoView);
        String src = "https://s3-us-west-2.amazonaws.com/swapub-8080/Uploads/Product/4635629248/5513c7167344110c5420838f/0/5513c7167344110c5420838f.mp4";
        //videoView.setVideoURI(Uri.parse(src));
        //videoView.setMediaController(new MediaController(RecentQueryActivity.this));
        //videoView.requestFocus();
        //videoView.start();

        //saveView(mButton1);

        DKLog.d(TAG, SharedPrefsData.getEmail(this));
    }

    public void saveView(View view){
        view.setDrawingCacheEnabled(true);
        Bitmap bmScreen = view.getDrawingCache();
        saveImage(bmScreen);
    }

    protected void saveImage(Bitmap bmScreen2) {
        // TODO Auto-generated method stub

        // String fname = "Upload.png";
        File saved_image_file = new File(
                Environment.getExternalStorageDirectory()
                        + "/captured_Bitmap.png");
        if (saved_image_file.exists())
            saved_image_file.delete();
        try {
            FileOutputStream out = new FileOutputStream(saved_image_file);
            bmScreen2.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            DKLog.e(TAG, Trace.getCurrentMethod() + e.toString());
        }

    }


    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.checkbutton1:
                mButton1.setSelected(true);
                mButton2.setSelected(false);
                String msg = latEditText.getText().toString();
                DKLog.d(TAG, Trace.getCurrentMethod() + msg.trim());
                break;
            case R.id.checkbutton2:
                mButton1.setSelected(false);
                mButton2.setSelected(true);
                break;
            case R.id.check_button:
                Location l = new Location("Fake");
                l.setLatitude(Double.valueOf(latEditText.getText().toString()));
                l.setLongitude(Double.valueOf(lngEditText.getText().toString()));
                getAddressByLocation(l);
                break;
        }
    }

    public void getAddressByLocation(Location location) {
        String result = "No Result";
        try {
            if (location != null) {
                Double longitude = location.getLongitude();	//取得經度
                Double latitude = location.getLatitude();	//取得緯度

                //建立Geocoder物件: Android 8 以上模疑器測式會失敗
                Geocoder gc = new Geocoder(this, Locale.TRADITIONAL_CHINESE); 	//地區:台灣
                //自經緯度取得地址
                List<Address> lstAddress = gc.getFromLocation(latitude, longitude, 5);
                if(lstAddress.size() > 0){
                    result = "";
                    for(int i = 0 ; i < lstAddress.size() ; i++){
                        result = result + i + " : " + "\n";
                        result = result + "Address: "   + lstAddress.get(0).getAddressLine(0)   + "\n";
                        result = result + "Locality: "  + lstAddress.get(0).getLocality()       + "\n";
                        result = result + "AdminArea: " + lstAddress.get(0).getAdminArea()      + "\n";
                        result = result + "Country: "   + lstAddress.get(0).getCountryName()    + "\n";
                        result = result + "PostalCode: " + lstAddress.get(0).getPostalCode()    + "\n";
                        result = result + "Feature: "   + lstAddress.get(0).getFeatureName()    + "\n\n";

                        //result = result + lstAddress.get(i).getAddressLine(0) + "\n";
                        //result = result + lstAddress.get(i).getPostalCode() + "\n";
                    }
                }
                resultTextView.setText(result);
            }
        }catch(Exception e) {
            DKLog.e(TAG, Trace.getCurrentMethod() + e.toString());
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
