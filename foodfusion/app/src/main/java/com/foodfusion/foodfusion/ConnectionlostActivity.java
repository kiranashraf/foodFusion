package com.foodfusion.foodfusion;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.foodfusion.foodfusion.Custom.NetworkChangeReceiver;
import com.foodfusion.foodfusion.Util.Utility;
import com.google.gson.Gson;

/**
 * Created by Rameez on 5/6/2018.
 */

public class ConnectionlostActivity extends AppCompatActivity {

    Activity currentActivity;
    NetworkChangeReceiver mReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentActivity = this;

        setContentView(R.layout.activity_connectionlost);
        LinearLayout rootLayout = (LinearLayout)findViewById(R.id.ll1);
        Utility.overrideFonts(currentActivity, rootLayout);


    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {

            //finish();
            Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            intent.putExtra("EXIT", true);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onResume() {
        this.mReceiver = new NetworkChangeReceiver();
        registerReceiver(
                this.mReceiver,
                new IntentFilter(
                        ConnectivityManager.CONNECTIVITY_ACTION));
        super.onResume();
        this.registerReceiver(mMessageReceiver, new IntentFilter("NetworkConnection"));
    }
    @Override
    public void onPause() {
        unregisterReceiver(mReceiver);
        super.onPause();
        this.unregisterReceiver(mMessageReceiver);
    }
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            if(intent.getAction().equals("NetworkConnection")){
                try {
                    checkIfNet();
                }
                catch(Exception ex){
                    // Toast.makeText(currentActivity, R.string.FavouriteMakeError, Toast.LENGTH_LONG).show();
                }
            }
        }
    };
public void Retry(View v){
        if(checkIfNet()){
            Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            intent.putExtra("EXIT", true);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(currentActivity, R.string.stillNoConnection, Toast.LENGTH_SHORT).show();
        }
}
    Boolean checkIfNet(){
        try {

            SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(currentActivity);
            Gson gson = new Gson();
            Boolean json = false;
            if (appSharedPrefs.contains("isNetworkConnection")) {
                json = appSharedPrefs.getBoolean("isNetworkConnection", false);


            } else {
                //Toast.makeText(currentActivity, R.string.FavouriteMakeError, Toast.LENGTH_LONG).show();
            }
            return json;
        }
        catch(Exception ex){
            // Toast.makeText(currentActivity, R.string.FavouriteMakeError, Toast.LENGTH_LONG).show();
            return false;
        }
    }
    public void BackButton(View v){
        Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            intent.putExtra("EXIT", true);
        startActivity(intent);
        finish();
    }
}
