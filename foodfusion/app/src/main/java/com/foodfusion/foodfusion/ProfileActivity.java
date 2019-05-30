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
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.foodfusion.foodfusion.Custom.NetworkChangeReceiver;
import com.foodfusion.foodfusion.DB.MySharedPreference;
import com.foodfusion.foodfusion.Model.UserModel;
import com.foodfusion.foodfusion.Util.Utility;
import com.google.gson.Gson;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;

/**
 * Created by Rameez on 3/29/2018.
 */

public class ProfileActivity extends Activity implements View.OnClickListener {

    private ImageView mBack;

    private TextView mEmail;
    private TextView mMobile;

    private TextView mName;

    private Button mChangePass;

    Activity currentActivity;
    MySharedPreference mMySharedPreference;

    LinearLayout network;
    LinearLayout data,noData;
    NetworkChangeReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        currentActivity = this;
        network=(LinearLayout)findViewById(R.id.network);
        data=(LinearLayout)findViewById(R.id.data);
        noData=(LinearLayout)findViewById(R.id.noData);
        mMySharedPreference=new MySharedPreference(this);
        if(!mMySharedPreference.isUserLogin()) {
            Intent i = new Intent(this, DashboardActivity.class);
            Utility.StartActivity(this, i);
        } else {
            UserModel user = (UserModel) mMySharedPreference.getUserInfo();
            //back button
            mBack = (ImageView) findViewById(R.id.backBtn);
            mBack.setOnClickListener(this);

            mEmail = (TextView) findViewById(R.id.email);
            mMobile = (TextView) findViewById(R.id.mobile);
            mName = (TextView) findViewById(R.id.name);

            mEmail.setText("Email address: " + (user.getEmail() == null ? "N/A" : user.getEmail()));
            mMobile.setText("User name: " + (user.getUsername() == null ? "N/A" : user.getUsername()));
            mName.setText(user.getDisplayName().replace("&", "and").replace("#038;", ""));
            mChangePass = (Button) findViewById(R.id.changePass);
            mChangePass.setOnClickListener(this);

            CircleImageView userPic = (CircleImageView) findViewById(R.id.icon_profile);
            if (mMySharedPreference.isSocialUserLogin()) {
                if (mMySharedPreference.getUserPic() != null) {
//                userPic.setBorderWidth(10);
//                userPic.setBorderColor(getResources().getColor(R.color.black_transparent));
//                userPic.setBackgroundColor(getResources().getColor(R.color.appColor));
                    Global.picassoWithCache.with(this)
                            .load(mMySharedPreference.getUserPic()).error(R.drawable.profile_female_icon).resize(120, 120).centerCrop().into(userPic);
                } else {
                    Global.picassoWithCache.with(this)
                            .load(R.drawable.profile_female_icon).error(R.drawable.profile_female_icon).resize(120, 120).centerCrop().into(userPic);

                }
                mChangePass.setVisibility(GONE);

            } else {
                mChangePass.setVisibility(View.VISIBLE);
                Global.picassoWithCache.with(this)
                        .load(R.drawable.profile_female_icon).error(R.drawable.profile_female_icon).resize(120, 120).centerCrop().into(userPic);

            }

            WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);

            Display display = windowManager.getDefaultDisplay();
            int width = display.getWidth();
            int height = display.getHeight();
            userPic.getLayoutParams().height = height / 5;
            userPic.getLayoutParams().width = width / 2 + width / 3;
//        mLogin = (Button) findViewById(R.id.Login);
//        mLogin.setOnClickListener(this);

            LinearLayout rootLayout = (LinearLayout) findViewById(R.id.ll1);
            Utility.overrideFonts(currentActivity, rootLayout);
        }
        checkIfNet();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                finish();
                break;
            case R.id.changePass:
                startActivity(new Intent(ProfileActivity.this, ResetPasswordActivity.class));
//                finish();
                break;
//


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
