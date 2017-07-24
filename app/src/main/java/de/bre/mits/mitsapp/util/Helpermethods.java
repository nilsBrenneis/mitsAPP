package de.bre.mits.mitsapp.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.view.View;


public class Helpermethods {

    private static boolean initialized;
    private static SharedPreferences pref;
    private static SharedPreferences.Editor editor;
    private static long BACK_PRESSED_TIMESTAMP;

    /**
     * set up shared prefrences
     * @param act reference to calling activity
     */
    public static void initialize(Activity act) {
        if (!initialized) {
            pref = act.getSharedPreferences("Delicious", Context.MODE_PRIVATE);
            editor = pref.edit();
            initialized = true;
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            getEditor().putBoolean("terminate-server", false);
            getEditor().commit();
        }
    }

    public static SharedPreferences.Editor getEditor() {
        return editor;
    }

    public static SharedPreferences getpref() {
        return pref;
    }

    public static boolean isConnectedViaWifi(View v) {

        ConnectivityManager connectivityManager = (ConnectivityManager) v.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return netInfo.isConnected();
    }

    public static long getBackPressedTimestamp() {
        return BACK_PRESSED_TIMESTAMP;
    }

    public static void setBackPressedTimestamp(long backPressedTimestamp) {
        BACK_PRESSED_TIMESTAMP = backPressedTimestamp;
    }
}
