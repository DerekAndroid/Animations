package com.example.android.animationsdemo.paypal;

import org.json.JSONObject;

/**
 * Created by derekchang on 2016/2/3.
 */
public class PayPalResponse {
    // for response type
    public String type = "";

    // for response state
    public String state = "";
    public String id = "";
    public String create_time = "";
    public String intent = "";

    // for clinet information
    public String platform = "";
    public String paypal_sdk_version = "";
    public String product_name = "";
    public String environment = "";

    // for detail description
    public String short_description = "";
    public String amount = "";
    public String currency_code = "";

    public void parseConfirmData(JSONObject data){
        type                = data.optString("response_type");
        JSONObject response = data.optJSONObject("response");
        state               = response.optString("state");
        id                  = response.optString("id");
        create_time         = response.optString("create_time");
        intent              = response.optString("intent");
        JSONObject client = data.optJSONObject("client");
        platform            = client.optString("platform");
        paypal_sdk_version = client.optString("paypal_sdk_version");
        product_name        = client.optString("product_name");
        environment         = client.optString("environment");
    }

    public void parsePaymentData(JSONObject data){
        short_description   = data.optString("short_description");
        amount              = data.optString("amount");
        intent              = data.optString("intent");
        currency_code       = data.optString("currency_code");
    }

    public String toString(){
        return environment + ":" + short_description + " "
                + intent + ": " + currency_code + " " + amount;
    }

}
