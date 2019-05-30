package com.foodfusion.foodfusion;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.foodfusion.foodfusion.Custom.NetworkChangeReceiver;
import com.foodfusion.foodfusion.DB.MySharedPreference;
import com.foodfusion.foodfusion.Util.DataCenter;
import com.foodfusion.foodfusion.Util.Utility;
import com.google.gson.Gson;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Splash extends AppCompatActivity {

    Activity currentActivity;

    NetworkChangeReceiver mReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        currentActivity = this;

        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i("Key", "printHashKey() Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e("Key", "printHashKey()", e);
        } catch (Exception e) {
            Log.e("Key", "printHashKey()", e);
        }
        RelativeLayout rootLayout = (RelativeLayout) findViewById(R.id.mainRL);
        Utility.overrideFonts(currentActivity, rootLayout);
        TextView weare=(TextView)findViewById(R.id.weare);
        TextView cookfun=(TextView) findViewById(R.id.cookfun);
        weare.setTypeface(weare.getTypeface(), Typeface.BOLD);
        cookfun.setTypeface(cookfun.getTypeface(), Typeface.BOLD);
        DataCenter.GetCategories(this,"get-categories");


        //initialize FB
        FacebookSdk.sdkInitialize(getApplicationContext());


        //intitialize Twitter login
        //intitialize Twitter login
        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(getResources().getString(R.string.twitter_consumer_key),getResources().getString(R.string.twitter_consumer_secret)))
                .debug(true)
                .build();
        Twitter.initialize(config);


        AppEventsLogger.activateApp(this);
        new SplashScreenTask().execute();

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.foodfusion.foodfusion",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    private class SplashScreenTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {

            SystemClock.sleep(4000);
            return 1;
        }

        @Override
        protected void onPostExecute(Integer result) {

            LaunchActivity();
        }
    }

    public void LaunchActivity() {
        try {
            MySharedPreference sharedPreference = new MySharedPreference(this);
            if (!sharedPreference.isFirstTimeBoarding()) {
                sharedPreference.setFirstTimeBoarding(true);
                Intent intent = new Intent(currentActivity, OnboardingActivity.class);
                startActivity(intent);
                finish();
            } else {
                if (checkIfNet()) {
                    Intent intent = new Intent(currentActivity, DashboardActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(currentActivity, ConnectionlostActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }
        catch(Exception ex){

        }
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
}
