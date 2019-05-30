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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.foodfusion.foodfusion.Custom.NetworkChangeReceiver;
import com.foodfusion.foodfusion.DB.MySharedPreference;
import com.foodfusion.foodfusion.Model.SignUpModel;
import com.foodfusion.foodfusion.Util.DataCenter;
import com.foodfusion.foodfusion.Util.Utility;
import com.google.gson.Gson;

public class SignUpActivity extends AppCompatActivity {

    Activity currentActivity;
    EditText mFname,mLname,mUserName;
    EditText mEmail;
    EditText mPassword, cPassword;
    MySharedPreference mMySharedPreference;

    String id,name,email;
    LinearLayout network;
    LinearLayout data,noData;
    NetworkChangeReceiver mReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
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
        mFname=(EditText)findViewById(R.id.etFname);
        mLname=(EditText)findViewById(R.id.etLname);
        mUserName=(EditText)findViewById(R.id.etUserName);
        mEmail=(EditText)findViewById(R.id.etEmail);
        mPassword=(EditText)findViewById(R.id.etPassword);
        cPassword=(EditText)findViewById(R.id.etConfirmPassword);
        checkIfNet();
    }

    public void BackButton(View v){
       // Intent intent=new Intent(SignUpActivity.this, DashboardActivity.class);
       // startActivity(intent);
        finish();
    }
    public void BtnSignupClick(View v){
        if(checkIfNet()) {
            if (mEmail.getText().toString().length() > 0 && mPassword.getText().toString().length() > 0 &&
                    mFname.getText().toString().length() > 0 && mLname.getText().toString().length() > 0 &&
                    mUserName.getText().toString().length() > 0 && cPassword.getText().toString().length() > 0) {

                if (Utility.isValidEmail(mEmail.getText().toString())) {

                    if (mPassword.getText().toString().equals(cPassword.getText().toString())) {
                        try {
                            Utility.showLoader(this);
                            DataCenter.SignUp(this, "signup", mFname.getText().toString(), mLname.getText().toString(), mUserName.getText().toString(), mEmail.getText().toString(), mPassword.getText().toString());
                            //  new Server(this, Constants.POST_LOGIN_API, this, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new String[]{"login",mEmail.getText().toString(), mPassword.getText().toString()});
                        } catch (Exception ex) {
                            String abc = ex.toString();
                        }
                    } else {
                        Utility.showMessage(this, getString(R.string.checkPassword), getString(R.string.alert));
//                        Toast.makeText(this,getString(R.string.checkPassword),Toast.LENGTH_LONG).show();
                    }

                } else {
                    Utility.showMessage(this, getString(R.string.checkEmailField), getString(R.string.alert));
//                    Toast.makeText(this,getString(R.string.checkEmailField),Toast.LENGTH_LONG).show();
                    //Util.getInstance().showDefaultAlertDialog(SigninActivity.this, ,);
                }

            } else {
                Utility.showMessage(this, getString(R.string.checkEmptyField), getString(R.string.alert));
//                Toast.makeText(this,getString(R.string.checkEmptyField),Toast.LENGTH_LONG).show();
                // Util.getInstance().showDefaultAlertDialog(SigninActivity.this, getString(R.string.alert), getString(R.string.checkEmptyField));
            }
        } else {
            Toast.makeText(this,R.string.stillNoConnection,Toast.LENGTH_SHORT).show();
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
        this.registerReceiver(mMessageReceiver, new IntentFilter("UserSignUp"));
        this.registerReceiver(mMessageReceiver, new IntentFilter("NetworkConnection"));
        checkIfNet();

    }

    @Override
    public void onPause() {
        unregisterReceiver(mReceiver);
        super.onPause();
        this.unregisterReceiver(mMessageReceiver);
    }
    public void refreshScreen() {
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = "";
        if (appSharedPrefs.contains("UserSignUpData")){
            json = appSharedPrefs.getString("UserSignUpData", "");
            SignUpModel responce = gson.fromJson(json, SignUpModel.class);
            if(responce!=null){
                Utility.hideLoader(this);
                if(responce.getStatus()==1){
                    Intent i = new Intent(this, DashboardActivity.class);
                    Utility.StartActivity(this, i);

                } else {
//                Toast.makeText(this,"SignUp Failed: "+responce.getMessage(),Toast.LENGTH_LONG).show();
                    Utility.showMessage(this, "SignUp failed: "+responce.getMessage(), "Info");
                }

            }
            else {
                Utility.hideLoader(this);
//                Toast.makeText(this,responce.getMessage(),Toast.LENGTH_LONG).show();
                Utility.showMessage(this, responce.getMessage(),"Info");
            }
        }
    }
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("UserSignUp")){
                refreshScreen();

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
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
//            Intent intent=new Intent(SignUpActivity.this, DashboardActivity.class);
//            startActivity(intent);
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
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
