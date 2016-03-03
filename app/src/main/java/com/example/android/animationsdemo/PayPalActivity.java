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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.animationsdemo.paypal.PayPalCurrency;
import com.example.android.animationsdemo.paypal.PayPalResponse;
import com.example.android.animationsdemo.paypal.SwapubPayPal;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;


public class PayPalActivity extends FragmentActivity implements View.OnClickListener{
    public static final String TAG = "PayPalActivity";
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mLsvDrawerMenu;
    private Button mGetClientTokenButton;
    private Button mBuy;
    private EditText mPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DKLog.d(TAG, Trace.getCurrentMethod());
        // 1.init
        SwapubPayPal.startPayPayService(this);

        // 2.setView
        setContentView(R.layout.activity_paypal);
        // 3.get ID
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drw_layout);
        mLsvDrawerMenu = (ListView) findViewById(R.id.lsv_drawer_menu);
        mGetClientTokenButton = (Button) findViewById(R.id.get_client_token_button);
        mBuy = (Button) findViewById(R.id.buy_button);
        mPayment = (EditText) findViewById(R.id.pay_editText);

        // 設定 Drawer 的影子
        //mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        mGetClientTokenButton.setOnClickListener(this);
        mBuy.setOnClickListener(this);

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
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void buySomething(String cost, String dollar, String name){
        PayPalPayment item = SwapubPayPal.makePayPalPayment(cost, dollar, name);

        Intent intent = new Intent(PayPalActivity.this, PaymentActivity.class);
        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, SwapubPayPal.config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, item);
        startActivityForResult(intent, SwapubPayPal.REQUEST_CODE_PAYMENT);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.get_client_token_button:

                break;
            case R.id.buy_button:
                buySomething(mPayment.getText().toString(), PayPalCurrency.USD, "Swapub交易");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        DKLog.d(TAG, Trace.getCurrentMethod() +
                String.format("requestCode: %s, resultCode: %s",
                        requestCode, resultCode));
        if (requestCode == SwapubPayPal.REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm =
                        data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        // 交易細節
                        Log.i(TAG, confirm.toJSONObject().toString(4));
                        // 交易資訊
                        Log.i(TAG, confirm.getPayment().toJSONObject().toString(4));

                        PayPalResponse mPayPalResponse = new PayPalResponse();
                        mPayPalResponse.parseConfirmData(confirm.toJSONObject());
                        mPayPalResponse.parsePaymentData(confirm.getPayment().toJSONObject());
                        /**
                         *  TODO: send 'confirm' (and possibly confirm.getPayment() to your server for verification
                         * or consent completion.
                         * See https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
                         * for more details.
                         *
                         * For sample mobile backend interactions, see
                         * https://github.com/paypal/rest-api-sdk-python/tree/master/samples/mobile_backend
                         */
                        Toast.makeText(
                                getApplicationContext(),
                                mPayPalResponse.toString(), Toast.LENGTH_LONG)
                                .show();

                    } catch (JSONException e) {
                        Log.e(TAG, "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i(TAG, "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i(TAG,"An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void asyncGetClientToken(){
//        AsyncHttpClient client = new AsyncHttpClient();
//        client.get("https://www.google.com", new AsyncHttpResponseHandler() {
//
//            @Override
//            public void onStart() {
//                // called before request is started
//                DKLog.d(TAG, Trace.getCurrentMethod());
//            }
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
//                // called when response HTTP status is "200 OK"
//                DKLog.d(TAG, Trace.getCurrentMethod() + response);
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
//                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
//                DKLog.d(TAG, Trace.getCurrentMethod() + errorResponse);
//            }
//
//            @Override
//            public void onRetry(int retryNo) {
//                // called when request is retried
//                DKLog.d(TAG, Trace.getCurrentMethod() + retryNo);
//            }
//        });
    }

}
