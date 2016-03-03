package com.example.android.animationsdemo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by derekchang on 2015/7/30.
 */
public class SharedPrefsData {
    private static final String KEY_EMAIL = "SharedPrefsData.KEY_EMAIL";

    public static void saveEmail(Activity activity, String email){
        SharedPreferences spref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = spref.edit();
        editor.putString(KEY_EMAIL, email);
        editor.commit();
    }

    public static String getEmail(Activity activity){
        SharedPreferences spref = activity.getPreferences(Context.MODE_PRIVATE);
        return spref.getString(KEY_EMAIL, "");
    }
}
