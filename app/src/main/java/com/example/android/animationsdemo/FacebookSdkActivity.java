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
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.security.cert.TrustAnchor;
import java.util.Arrays;
import java.util.List;

public class FacebookSdkActivity extends FragmentActivity implements View.OnClickListener{
    public static final String TAG = "FacebookSdkActivity";
    CallbackManager callbackManager;
    Button mLoginButton;
    LoginButton fbLoginButton;
    AccessToken mAccessToken;
    EditText mEmail;
    Button mCheckButton;
    Button mShareButton;
    WebView mWebView;

    static {
        System.loadLibrary("testlibrary");
    }
    public native byte[] downloadUrl(String url);
    public native byte[] postUrl(String field);
    public native String getStringFromNative();

    public void testCurl(){
        new Thread(){
            public void run(){
                String url = "https://fb.me/410173292515376 ";
                Log.i(TAG, "Requesting URL to download: " + url);
                byte[] result = downloadUrl(url);
                String contentString = result == null ? "Null" : new String(result);
                Log.i(TAG, contentString);
                Document doc = Jsoup.parse(contentString);
                Elements elements = doc.select("META");
                for(Element element : elements){
                    String prop = element.attr("property");
                    if(prop.equals("al:android:url")){
                        String content = element.attr("content");
                        DKLog.d(TAG, Trace.getCurrentMethod() + content);
                        if(content.contains("fbSwapub") && content.contains("ProductID")){
                            String productID = content.substring(content.lastIndexOf("/") + 1);
                            DKLog.d(TAG, Trace.getCurrentMethod() + productID);
                        }
                    }
                }
            }
        }.start();
    }

    public void genAppLink(){
        new Thread(){
            public void run(){
                JSONArray androidJSONArray = new JSONArray();
                JSONArray iosJSONArray = new JSONArray();
                JSONObject webJSONObject = new JSONObject();
                try {
                    // set android field
                    JSONObject androidJSONObject = new JSONObject();
                    androidJSONObject.put("url", "fbSwapub://productID/95276");
                    androidJSONObject.put("package", "com.gamania.swapub");
                    androidJSONObject.put("app_name", "Swapub");
                    androidJSONArray.put(androidJSONObject);

                    // set ios field
                    JSONObject iosJSONObject = new JSONObject();
                    iosJSONObject.put("url", "fbSwapub://productID/95276");
                    iosJSONObject.put("app_store_id", 956767433);
                    iosJSONObject.put("app_name", "Swapub");
                    iosJSONArray.put(iosJSONObject);

                    // set web field
                    webJSONObject.put("should_fallback", false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String field = "&access_token=" + "404933316372707|ea8383d6cb2eedada9ad1618cc8d582b";
                field += "&name=Android App Link Object";
                field += "&android=" + androidJSONArray.toString();
                field += "&ios=" + iosJSONArray.toString();
                field += "&web=" + webJSONObject.toString();
                Log.i(TAG, Trace.getCurrentMethod() + "Field = " + field);
                byte[] result = postUrl(field);
                String contentString = result == null ? "Null" : new String(result);
                Log.i(TAG, contentString);

                try {
                    // result
                    JSONObject resJSON = new JSONObject(contentString);
                    String id = resJSON.optString("id", "ERROR_ID");
                    DKLog.d(TAG, Trace.getCurrentMethod() + "https://fb.me/" + id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void testJNI(){
        DKLog.d(TAG, Trace.getCurrentMethod() + getStringFromNative());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 1.initial
        FacebookSdk.sdkInitialize(this);
        //testJNI();
        testCurl();

        callbackManager = CallbackManager.Factory.create();
        // 2.setView
        setContentView(R.layout.activity_facebook);
        // 3.get ID
        fbLoginButton   = (LoginButton) findViewById(R.id.login_button);
        mLoginButton    = (Button) findViewById(R.id.fb_custom_login);
        mEmail          = (EditText) findViewById(R.id.email);
        mCheckButton    = (Button) findViewById(R.id.checkbutton);
        mShareButton    = (Button) findViewById(R.id.share_button);
        mWebView        = (WebView)findViewById(R.id.webView);
        mWebView.setWebViewClient(mWebViewClient);
        mWebView.loadUrl("http://www.swapub.com/Terms/TermsEN");

        mLoginButton.setOnClickListener(this);
        //fbLoginButton.setReadPermissions(Arrays.asList("email"));
        // Callback registration
        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                mAccessToken = loginResult.getAccessToken();
                DKLog.d(TAG, Trace.getCurrentMethod() + mAccessToken.toString());
                getEmail(mAccessToken);
            }

            @Override
            public void onCancel() {
                // App code
                DKLog.e(TAG, Trace.getCurrentMethod() + "loginButton");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                DKLog.e(TAG, Trace.getCurrentMethod() + exception.toString());
            }
        });
        mCheckButton.setOnClickListener(this);
        mShareButton.setOnClickListener(this);
        DKLog.d(TAG, SharedPrefsData.getEmail(this));
    }

    WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    };

    public void getEmail(AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object,GraphResponse response) {
                        // Application code
                        DKLog.e(TAG, Trace.getCurrentMethod() + response.getRawResponse());
                        DKLog.d(TAG, Trace.getCurrentMethod() + object.toString());
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, name, link, email");
        request.setParameters(parameters);
        request.executeAsync();
    }


    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.share_button:
                DKLog.d(TAG, Trace.getCurrentMethod() + mAccessToken);
                genAppLink();
                break;
            case R.id.checkbutton:
                if(Patterns.EMAIL_ADDRESS.matcher(mEmail.getText()).matches()){
                    Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "GG", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.fb_custom_login:
                String email = "derekchang@gamania.com";
                SharedPrefsData.saveEmail(this, email);
                DKLog.d(TAG, Trace.getCurrentMethod() + Patterns.EMAIL_ADDRESS.matcher(email).matches());
                LoginManager.getInstance().logInWithReadPermissions(this,
                        Arrays.asList("public_profile",
                                "user_friends",
                                "user_location",
                                "user_birthday",
                                "user_likes",
                                "publish_actions",
                                "user_photos"));
                LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        mAccessToken = loginResult.getAccessToken();
                        DKLog.d(TAG,Trace.getCurrentMethod() + mAccessToken.getPermissions().toString());
                        //send request and call graph api
                        GraphRequest request = GraphRequest.newMeRequest(
                                mAccessToken,
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        Log.d(TAG, "name:" + object.optString("name"));
                                        Log.d(TAG, "link:" + object.optString("link"));
                                        Log.d(TAG, "id:" + object.optString("id"));
                                        Log.d(TAG, "Email:" + object.optString("email"));
                                        Log.d(TAG, "about:" + object.optString("about"));
                                        Log.d(TAG, "birthday:" + object.optString("birthday"));
                                    }
                                });


                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id, name, email, link, about, birthday");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }


                    @Override
                    public void onCancel() {
                        // App code
                        DKLog.e(TAG, Trace.getCurrentMethod() + "CustomLoginButton");
                    }


                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        DKLog.e(TAG, Trace.getCurrentMethod() + exception.toString());
                    }
                });
                break;
        }
    }

    private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
    private boolean pendingPublishReauthorization = false;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        DKLog.d(TAG, Trace.getCurrentMethod() +
                String.format("requestCode: %s, resultCode: %s",
                        requestCode, resultCode));
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }



}
