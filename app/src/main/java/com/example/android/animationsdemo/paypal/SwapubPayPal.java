package com.example.android.animationsdemo.paypal;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;

import java.math.BigDecimal;

/**
 * Created by derekchang on 2016/2/2.
 */
public class SwapubPayPal {
    private static final String TAG = "SwapubPaypal";
    /**
     * - Set to PayPalConfiguration.ENVIRONMENT_PRODUCTION to move real money.
     *
     * - Set to PayPalConfiguration.ENVIRONMENT_SANDBOX to use your test credentials
     * from https://developer.paypal.com
     *
     * - Set to PayPalConfiguration.ENVIRONMENT_NO_NETWORK to kick the tires
     * without communicating to PayPal's servers.
     */
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
    // note that these credentials will differ between live & sandbox environments.
    private static final String CONFIG_CLIENT_ID = "AdBKN3lyFpoWrL8kMkJpXK754_Hvav3S4Uc4EJxcXHPV0ckolMfNuIA1H43Xjb1TrdX5wfYQ6UcBP2mW";
    public static final int REQUEST_CODE_PAYMENT = 1;
    public static final int REQUEST_CODE_FUTURE_PAYMENT = 2;
    public static final int REQUEST_CODE_PROFILE_SHARING = 3;

    public static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID)
                    // The following are only used in PayPalFuturePaymentActivity.
            .merchantName("Swapub Merchant")
            .merchantPrivacyPolicyUri(Uri.parse("http://www.swapub.com/privacy?Lang=en"))
            .merchantUserAgreementUri(Uri.parse("http://www.swapub.com/terms?Lang=en"));


    public static void startPayPayService(Context context){
        Intent intent = new Intent(context, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        context.startService(intent);
    }

    public static PayPalPayment makePayPalPayment(String cost, String dollar, String name) {
        return new PayPalPayment(new BigDecimal(cost), dollar, name, PayPalPayment.PAYMENT_INTENT_SALE);
    }

}
