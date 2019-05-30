package com.foodfusion.foodfusion;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.foodfusion.foodfusion.Custom.NetworkChangeReceiver;
import com.foodfusion.foodfusion.DB.MySharedPreference;
import com.foodfusion.foodfusion.Model.GenericModel;
import com.foodfusion.foodfusion.Util.DataCenter;
import com.foodfusion.foodfusion.Util.Utility;
import com.google.gson.Gson;

/**
 * Created by Rameez on 4/8/2018.
 */

public class ForgetPasswordResetActivity extends AppCompatActivity {

    Activity currentActivity;
    EditText mNewPass,mConfirmPass;
    MySharedPreference mMySharedPreference;
    LinearLayout network;
    LinearLayout data,noData;
    NetworkChangeReceiver mReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpassword_reset);
        mMySharedPreference=new MySharedPreference(this);
        if(mMySharedPreference.isUserLogin()) {
            Intent i = new Intent(this, DashboardActivity.class);
            Utility.StartActivity(this, i);
        }
        currentActivity = this;
        network=(LinearLayout)findViewById(R.id.network);
        data=(LinearLayout)findViewById(R.id.data);
        noData=(LinearLayout)findViewById(R.id.noData);
        LinearLayout rootLayout = (LinearLayout) findViewById(R.id.ll1);
        Utility.overrideFonts(currentActivity, rootLayout);
        mNewPass=(EditText)findViewById(R.id.etNewPass);
        mConfirmPass=(EditText)findViewById(R.id.etConfirmPass);

        TextView tv_heading=(TextView) findViewById(R.id.tv_heading);
        tv_heading.setTypeface(tv_heading.getTypeface(), Typeface.BOLD);

        checkIfNet();

    }

    public void BackButton(View v){
        finish();
    }
    public void BtnResetClick(View v){
        if(checkIfNet()) {
            if (mNewPass.getText().toString().length() > 0 &&
                    mConfirmPass.getText().toString().length() > 0) {

                if (mConfirmPass.getText().toString().equals(mNewPass.getText().toString())) {
                    try {
                        Utility.showLoader(this);
                        DataCenter.ForgotPassReset(this, "reset_password", mNewPass.getText().toString());
                        //  new Server(this, Constants.POST_LOGIN_API, this, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new String[]{"login",mEmail.getText().toString(), mPassword.getText().toString()});
                    } catch (Exception ex) {
                        String abc = ex.toString();
                    }
                } else {
                    Utility.showMessage(this, getString(R.string.checkPassword), getString(R.string.alert));
                }


            } else {
                Utility.showMessage(this, getString(R.string.checkEmptyField), getString(R.string.alert));
                // Util.getInstance().showDefaultAlertDialog(SigninActivity.this, getString(R.string.alert), getString(R.string.checkEmptyField));
            }
        } else{
            Toast.makeText(currentActivity,R.string.stillNoConnection,Toast.LENGTH_SHORT).show();
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
        this.registerReceiver(mMessageReceiver, new IntentFilter("ForgotPassReset"));
        this.registerReceiver(mMessageReceiver, new IntentFilter("NetworkConnection"));
        checkIfNet();
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
            if(intent.getAction().equals("ForgotPassReset")){
                SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
                Gson gson = new Gson();
                String json = "";
                if (appSharedPrefs.contains("ForgotPassResetData")) {
                    json = appSharedPrefs.getString("ForgotPassResetData", "");
                    GenericModel responce = gson.fromJson(json, GenericModel.class);
                    if(responce!=null){
                        if(responce.getStatus()==1) {
                            Toast.makeText(context, "Success: "+responce.getMessage(), Toast.LENGTH_LONG).show();
                            Intent i = new Intent(context, SigninActivity.class);
                            Utility.StartActivity(context, i);
                            Utility.hideLoader(context);
                        } else {
                            Utility.hideLoader(context);
                            Utility.showMessage(context, getResources().getString(R.string.ResetError) + " "+responce.getMessage(),"Info");
                        }
                    }
                    else {
                        Utility.hideLoader(context);
                        Utility.showMessage(context, responce.getMessage(),"Info");
                    }
                }



            }
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

                if (json) {

                    network.setVisibility(View.GONE);


                } else{

                    network.setVisibility(View.VISIBLE);


                }
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
