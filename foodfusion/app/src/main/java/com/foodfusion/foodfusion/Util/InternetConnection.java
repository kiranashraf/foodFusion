package com.foodfusion.foodfusion.Util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Rameez on 5/6/2018.
 */

public class InternetConnection {
    public boolean isConnectedToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }
}
