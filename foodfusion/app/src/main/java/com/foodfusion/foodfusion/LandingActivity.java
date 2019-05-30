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
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.foodfusion.foodfusion.Custom.NetworkChangeReceiver;
import com.foodfusion.foodfusion.DB.MySharedPreference;
import com.foodfusion.foodfusion.Model.GenericModel;
import com.foodfusion.foodfusion.Util.DataCenter;
import com.foodfusion.foodfusion.Util.Utility;
import com.google.gson.Gson;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;

public class LandingActivity extends AppCompatActivity {

    Activity currentActivity;
    TextView sign_up_with2;
    MySharedPreference mMySharedPreference;
    CallbackManager callbackManager;
    RelativeLayout rl_fb;
    LoginButton loginButton;

    TwitterLoginButton loginButton_twitter;

    LinearLayout network;
    LinearLayout data,noData;
    NetworkChangeReceiver mReceiver;
    String id,name,email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        mMySharedPreference=new MySharedPreference(this);
        if(mMySharedPreference.isUserLogin()) {
            Intent i = new Intent(this, DashboardActivity.class);
            Utility.StartActivity(this, i);
        }
        currentActivity = this;
        network=(LinearLayout)findViewById(R.id.network);
        data=(LinearLayout)findViewById(R.id.data);
        noData=(LinearLayout)findViewById(R.id.noData);
        RelativeLayout rootLayout = (RelativeLayout) findViewById(R.id.rl1);
        sign_up_with2=(TextView)findViewById(R.id.sign_up_with2);
        sign_up_with2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkIfNet()) {
                    loginButton.performClick();
                } else{
                    Toast.makeText(currentActivity,R.string.stillNoConnection,Toast.LENGTH_SHORT).show();
                }

            }
        });
        Utility.overrideFonts(currentActivity, rootLayout);

        //Initializing Facebook
        InitializeFaceBook();

        //Initializing Twitter
        InitializeTwitter();
        checkIfNet();
    }

    public void InitializeTwitter(){

        // for twitter login
        loginButton_twitter = (TwitterLoginButton) findViewById(R.id.login_button2);
        loginButton_twitter.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // Do something with result, which provides a TwitterSession for making API calls

                TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
               // final UserModel userModel = new UserModel();
                //userModel.twitterId=session.getUserId();
//                TwitterAuthClient authClient = new TwitterAuthClient();
//                authClient.requestEmail(session, new Callback<String>() {
//                    @Override
//                    public void success(Result<String> result2) {
//                        String email=result2.data;
//                        // Do something with the result, which provides the email address
//                    }
//
//                    @Override
//                    public void failure(TwitterException exception) {
//                        String ahd="fail";
//                        // Do something on failure
//                    }
//                });


                Call<User> userResult =TwitterCore.getInstance().getApiClient(session).getAccountService().verifyCredentials(true,false,true);
                userResult.enqueue(new Callback<User>() {

                    @Override
                    public void failure(TwitterException e) {
                    }

                    @Override
                    public void success(Result<User> userResult) {

                        User user = userResult.data;



                        try {
                            Utility.showLoader(currentActivity);
                            mMySharedPreference.setUserPic(user.profileImageUrl.replace("_normal",""));
                            DataCenter.LoginTwitter(currentActivity,"login",user.idStr,user.name,user.email);
                            Log.d("imageurl", user.profileImageUrl);
                            Log.d("name", user.name);
                            Log.d("email",user.email);
                            Log.d("des", user.description);
                            Log.d("followers ", String.valueOf(user.followersCount));
                            Log.d("createdAt", user.createdAt);
                        } catch (Exception e) {
                            Utility.hideLoader(currentActivity);
                            Toast.makeText(currentActivity, R.string.TwitterError + " " +e.toString(), Toast.LENGTH_LONG).show();

                            e.printStackTrace();
                        }


                    }

                });

                TwitterAuthToken authToken = session.getAuthToken();
                String token = authToken.token;
                String secret = authToken.secret;
            }

            @Override
            public void failure(TwitterException exception) {
                String ad="sfsf";
                Toast.makeText(currentActivity, R.string.TwitterError + " " +exception.toString(), Toast.LENGTH_LONG).show();

                // Do something on failure
            }
        });

    }

    public void InitializeFaceBook(){
        LoginManager.getInstance().logOut();
        callbackManager = CallbackManager.Factory.create();
        rl_fb=(RelativeLayout) findViewById(R.id.rl_fb);
        loginButton = (LoginButton) findViewById(R.id.login_button);

        List< String > permissionNeeds = Arrays.asList("email", "public_profile", "AccessToken");

        loginButton.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {@Override
                public void onSuccess(LoginResult loginResult) {
                    try {
                    System.out.println("onSuccess");

                    String accessToken = loginResult.getAccessToken()
                            .getToken();
                    Log.i("accessToken", accessToken);

                    GraphRequest request = GraphRequest.newMeRequest(
                            loginResult.getAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {@Override
                            public void onCompleted(JSONObject object,
                                                    GraphResponse response) {

                                Log.i("LoginActivity",
                                        response.toString());
                                try {
                                    if (object.has("id"))
                                        id = object.getString("id");
                                    try {
                                        URL profile_pic = new URL(
                                                "http://graph.facebook.com/" + id + "/picture?type=large");
                                        mMySharedPreference.setUserPic(profile_pic.toString());
                                        Log.i("profile_pic",
                                                profile_pic + "");

                                    } catch (MalformedURLException e) {
                                        Utility.hideLoader(currentActivity);
                                        e.printStackTrace();
                                    }
                                    if (object.has("name"))
                                        name = object.getString("name");
                                    if (object.has("email"))
                                        email = object.getString("email");
                                    Utility.showLoader(currentActivity);
                                    DataCenter.LoginFB(currentActivity, "login", id, name, email);
                                }
                                catch (JSONException ex){
                                    Utility.hideLoader(currentActivity);
                                }


                            }
                            });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields",
                            "id,name,email");
                    request.setParameters(parameters);
                    request.executeAsync();
                    } catch (Exception e) {
                        Utility.hideLoader(currentActivity);
                        e.printStackTrace();
                        Toast.makeText(currentActivity, R.string.FbError, Toast.LENGTH_LONG).show();
                    }
                }

                    @Override
                    public void onCancel() {
                        System.out.println("onCancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                    try {
                        System.out.println("onError");
                        Log.v("LoginActivity", exception.getCause().toString());
                    }
                    catch (Exception ex){

                    }
                    }
                });

    }
    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent data) {
        try {
            super.onActivityResult(requestCode, responseCode, data);
            callbackManager.onActivityResult(requestCode, responseCode, data);
            // data.getAction().equals(

            loginButton_twitter.onActivityResult(requestCode, responseCode, data);
        }
        catch (Exception ex){

        }
    }

    public void FacebookSignUp(View v) {
        if(checkIfNet()) {
            loginButton.performClick();
        } else{
            Toast.makeText(this,R.string.stillNoConnection,Toast.LENGTH_SHORT).show();
        }

    }
   public void  TwitterSignUp(View v){
       try {
           if(checkIfNet()) {
               loginButton_twitter.performClick();
           }
            else{
               Toast.makeText(this,R.string.stillNoConnection,Toast.LENGTH_SHORT).show();
           }
       } catch(Exception ex)
       {
           Utility.hideLoader(currentActivity);
       }
    }
    public void BackButton(View v){
        Intent intent=new Intent(LandingActivity.this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }
    public void BtnLoginClick(View v){
        Intent intent = new Intent(LandingActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        this.mReceiver = new NetworkChangeReceiver();
        registerReceiver(
                this.mReceiver,
                new IntentFilter(
                        ConnectivityManager.CONNECTIVITY_ACTION));
        super.onResume();

        this.registerReceiver(mMessageReceiver, new IntentFilter("FBUser"));
        this.registerReceiver(mMessageReceiver, new IntentFilter("TwitterUser"));
        this.registerReceiver(mMessageReceiver, new IntentFilter("NetworkConnection"));
        checkIfNet();
    }
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals("FBUser")){
                try {
                    Utility.hideLoader(currentActivity);
                    SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
                    Gson gson = new Gson();
                    String json = "";
                    if (appSharedPrefs.contains("FBUserData")) {
                        json = appSharedPrefs.getString("FBUserData", "");
                        GenericModel responce = gson.fromJson(json, GenericModel.class);
                        if(responce!=null) {
                            if (responce.getStatus() == 2) {
                                Toast.makeText(currentActivity, responce.getMessage(), Toast.LENGTH_LONG).show();
                            } else {
                                Intent intent1 = new Intent(LandingActivity.this, DashboardActivity.class);
                                startActivity(intent1);
                            }
                        } else {
                            Utility.hideLoader(currentActivity);
                            Toast.makeText(currentActivity, R.string.FbError, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Utility.hideLoader(currentActivity);
                        Toast.makeText(currentActivity, R.string.FbError, Toast.LENGTH_LONG).show();
                    }
                }
                catch(Exception ex){
                    Utility.hideLoader(currentActivity);
                    Toast.makeText(currentActivity, R.string.FbError, Toast.LENGTH_LONG).show();
                }
            }
            if(intent.getAction().equals("TwitterUser")){
                try {
                    Utility.hideLoader(currentActivity);
                    SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
                    Gson gson = new Gson();
                    String json = "";
                    if (appSharedPrefs.contains("TwitterUserData")) {
                        json = appSharedPrefs.getString("TwitterUserData", "");
                        GenericModel responce = gson.fromJson(json, GenericModel.class);
                        if(responce != null){
                        if (responce.getStatus() == 2) {
                            Toast.makeText(currentActivity, responce.getMessage(), Toast.LENGTH_LONG).show();
                        }else {
                            Intent intent1 = new Intent(LandingActivity.this, DashboardActivity.class);
                            startActivity(intent1);
                        }} else {
                            Utility.hideLoader(currentActivity);
                            Toast.makeText(currentActivity, R.string.TwitterError, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Utility.hideLoader(currentActivity);
                        Toast.makeText(currentActivity, R.string.TwitterError, Toast.LENGTH_LONG).show();
                    }
                }
                catch(Exception ex){
                    Utility.hideLoader(currentActivity);
                    Toast.makeText(currentActivity, R.string.TwitterError, Toast.LENGTH_LONG).show();
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
            // refreshScreen();
        }
    };

    @Override
    public void onPause() {
        unregisterReceiver(mReceiver);
        super.onPause();
        this.unregisterReceiver(mMessageReceiver);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            Intent intent=new Intent(LandingActivity.this, DashboardActivity.class);
            startActivity(intent);
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
