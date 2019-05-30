package com.foodfusion.foodfusion.Custom;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.foodfusion.foodfusion.Util.NetworkUtil;

/**
 * Created by Rameez on 5/6/2018.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {
    Context mContext;
    static int count=0;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        String status = NetworkUtil.getConnectivityStatusString(context);
        final SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        Intent intent1 = new Intent("NetworkConnection");

        Log.e("Receiver ", "" + status);

        if (status.equals("Not connected to Internet")) {
            count++;
            if(count<=1) {
//                Intent inte = new Intent(context, ConnectionlostActivity.class);
//                context.startActivity(inte);

                prefsEditor.putBoolean("isNetworkConnection", false);
                prefsEditor.commit();
                context.sendBroadcast(intent1);
            }

            Log.e("Receiver ", "not connection");// your code when internet lost


        } else {
            count=0;
            prefsEditor.putBoolean("isNetworkConnection", true);
            prefsEditor.commit();
            context.sendBroadcast(intent1);
            Log.e("Receiver ", "connected to internet");//your code when internet connection come back
        }

    }
}