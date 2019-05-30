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
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.foodfusion.foodfusion.Model.UserModel;
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

public class SigninActivity extends Activity implements View.OnClickListener {

    private ImageView mBack;

    private EditText mEmail;
    private EditText mPassword;

    private Button mLogin;
    Activity currentActivity;
    MySharedPreference mMySharedPreference;

    LoginButton loginButton;
    CallbackManager callbackManager;

    TwitterLoginButton loginButton_twitter;
    String id,name,email,screenName;

    TextView tv_clickReset;

    String contextBack="";

    LinearLayout network;
    LinearLayout data,noData;
    NetworkChangeReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        currentActivity = this;
        network=(LinearLayout)findViewById(R.id.network);
        data=(LinearLayout)findViewById(R.id.data);
        noData=(LinearLayout)findViewById(R.id.noData);
        mMySharedPreference=new MySharedPreference(this);
        if(mMySharedPreference.isUserLogin()) {
            Intent i = new Intent(this, DashboardActivity.class);
            Utility.StartActivity(this, i);
        }
        //back button
        mBack = (ImageView) findViewById(R.id.backBtn);
        mBack.setOnClickListener(this);

        mEmail = (EditText) findViewById(R.id.etEmail);
        mPassword = (EditText) findViewById(R.id.etPassword);

        mLogin = (Button) findViewById(R.id.Login);
        mLogin.setOnClickListener(this);

        LinearLayout rootLayout = (LinearLayout) findViewById(R.id.ll1);
        Utility.overrideFonts(currentActivity, rootLayout);

        InitializeTwitter();
        InitializeFaceBook();

        tv_clickReset=(TextView)findViewById(R.id.tv_clickReset);
        tv_clickReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SigninActivity.this, ForgetPasswordEmailActivity.class);
                startActivity(intent);
            }
        });
        tv_clickReset.setTypeface(tv_clickReset.getTypeface(),Typeface.BOLD);

        TextView heading_login=(TextView) findViewById(R.id.heading_login);
        heading_login.setTypeface(heading_login.getTypeface(), Typeface.BOLD);
if(getIntent().getExtras()!=null) {
    String cont = getIntent().getExtras().getString("context", "");
    if (cont != "") {
        contextBack = cont;
    } else {
        contextBack = "";
    }
}
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
                            screenName=user.screenName;
                            Log.d("imageurl", user.profileImageUrl);
                            Log.d("name", user.name);
                            Log.d("email",user.email);
                            Log.d("des", user.description);
                            Log.d("followers ", String.valueOf(user.followersCount));
                            Log.d("createdAt", user.createdAt);
                        } catch (Exception e) {
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
            new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject object,
                                        GraphResponse response) {
                    try {
                        Log.i("LoginActivity",
                                response.toString());

                        if (object.has("id"))
                            id = object.getString("id");
                        try {
                            URL profile_pic = new URL(
                                    "http://graph.facebook.com/" + id + "/picture?type=large");
                            mMySharedPreference.setUserPic(profile_pic.toString());
                            Log.i("profile_pic",
                                    profile_pic + "");

                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        if (object.has("name"))
                            name = object.getString("name");
                        if (object.has("email"))
                            email = object.getString("email");
                        Utility.showLoader(currentActivity);
                        DataCenter.LoginFB(currentActivity, "login", id, name, email);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(currentActivity, R.string.FbError, Toast.LENGTH_LONG).show();
                    }


                }
            });
    Bundle parameters = new Bundle();
    parameters.putString("fields",
            "id,name,email");
    request.setParameters(parameters);
    request.executeAsync();
}
catch (Exception ex){

}
                }

                    @Override
                    public void onCancel() {
                        System.out.println("onCancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        System.out.println("onError");
                        Log.v("LoginActivity", exception.getCause().toString());
                    }
                });

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                if(contextBack!="") {
                    finish();
                }
                else {
                    Intent intent = new Intent(SigninActivity.this, DashboardActivity.class);
                    startActivity(intent);
                    finish();
                }
                break;
//            case R.id.register:
//                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
//                //finish();
//                break;
//
//            case R.id.forgetPassword:
//                startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
//                //finish();
//                break;
//
            case R.id.Login:
                if(checkIfNet()){


                if (mEmail.getText().toString().length() > 0 && mPassword.getText().toString().length() > 0) {

                    if (Utility.isValidEmail(mEmail.getText().toString())) {

                       // Utility.showLoader(this);
                        //Util.getInstance().showProgressDialog(this);
                        try {
                            Utility.showLoader(this);
                            DataCenter.Login(this, "login", mEmail.getText().toString(), mPassword.getText().toString());
                            //  new Server(this, Constants.POST_LOGIN_API, this, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new String[]{"login",mEmail.getText().toString(), mPassword.getText().toString()});
                        }
                        catch(Exception ex){
                            String abc=ex.toString();
                        }
                    } else {
                        Utility.showMessage(this,  getString(R.string.checkEmailField), getString(R.string.alert));
                        //Util.getInstance().showDefaultAlertDialog(SigninActivity.this, ,);
                    }

                } else {
                    Utility.showMessage(this,  getString(R.string.checkEmptyField), getString(R.string.alert));
                   // Util.getInstance().showDefaultAlertDialog(SigninActivity.this, getString(R.string.alert), getString(R.string.checkEmptyField));
                }}
                else{
                    Toast.makeText(this,R.string.stillNoConnection,Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    public void FacebookSignUp(View v) {
        if(checkIfNet()) {
            loginButton.performClick();
        } else {
            Toast.makeText(this,R.string.stillNoConnection,Toast.LENGTH_SHORT).show();
        }

    }
    public void  TwitterSignUp(View v){

        try {
            if(checkIfNet()) {
                loginButton_twitter.performClick();
            } else{
                Toast.makeText(this,R.string.stillNoConnection,Toast.LENGTH_SHORT).show();
            }

        } catch(Exception ex)
        {
        }
    }

    public void refreshScreen() {
        try {
            SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
            Gson gson = new Gson();
            String json = "";
            if (appSharedPrefs.contains("UserLoginData")) {
                json = appSharedPrefs.getString("UserLoginData", "");
                UserModel responce = gson.fromJson(json, UserModel.class);
                if (responce != null) {
                    Utility.hideLoader(this);
                    if (responce.getStatus() == 1) {
                        Intent i = new Intent(this, DashboardActivity.class);
                        Utility.StartActivity(this, i);

                    } else {
//                Toast.makeText(this,"SignUp Failed: "+responce.getMessage(),Toast.LENGTH_LONG).show();
                        mMySharedPreference.setUserLoggedIn(false);
                        Utility.showMessage(this, "Login failed: " + responce.getMessage(), "Info");
                    }

                } else {
                    Utility.hideLoader(this);
                    Utility.showMessage(this, responce.getMessage(), "Info");
                }
            }
        }
        catch(Exception ex){
            Utility.showMessage(this, ex.getMessage(), "Info");
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
        this.registerReceiver(mMessageReceiver, new IntentFilter("UserLogin"));
        this.registerReceiver(mMessageReceiver, new IntentFilter("FBUser"));
        this.registerReceiver(mMessageReceiver, new IntentFilter("TwitterUser"));
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
try{
            if(intent.getAction().equals("UserLogin")){
                refreshScreen();

            }
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
                                Intent intent1 = new Intent(SigninActivity.this, DashboardActivity.class);
                                startActivity(intent1);
                            }
                        } else {
                            Toast.makeText(currentActivity, R.string.FbError, Toast.LENGTH_LONG).show();

                        }
                    } else {
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
                        if(responce!=null){
                        if (responce.getStatus() == 2) {
                            Toast.makeText(currentActivity, responce.getMessage(), Toast.LENGTH_LONG).show();
                        }else {
                            Intent intent1 = new Intent(SigninActivity.this, DashboardActivity.class);
                            UserModel md=(UserModel) mMySharedPreference.getUserInfo();
                            md.setUsername(screenName);
                            String json1 = gson.toJson(md);
                            mMySharedPreference.setUserModel(json1);
                            startActivity(intent1);
                        }}
                        else {
                            Toast.makeText(currentActivity, R.string.TwitterError, Toast.LENGTH_LONG).show();

                        }
                    } else {
                        Toast.makeText(currentActivity, R.string.TwitterError, Toast.LENGTH_LONG).show();
                    }
                }
                catch(Exception ex){
                    Utility.hideLoader(currentActivity);
                    Toast.makeText(currentActivity, R.string.TwitterError, Toast.LENGTH_LONG).show();
                }
            }
            if(intent.getAction().equals("NetworkConnection")) {
                try {
                    checkIfNet();
                } catch (Exception ex) {
                    // Toast.makeText(currentActivity, R.string.FavouriteMakeError, Toast.LENGTH_LONG).show();
                }
            }        }

    catch (Exception ex){
        Utility.hideLoader(currentActivity);
        //FirebaseCrash.log(ex.getMessage());
    }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent data) {
        try {
            super.onActivityResult(requestCode, responseCode, data);
            callbackManager.onActivityResult(requestCode, responseCode, data);
            // data.getAction().equals(

            loginButton_twitter.onActivityResult(requestCode, responseCode, data);
        }
        catch (Exception Ex){

        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            if(contextBack!="") {
                finish();
            }
            else {
                Intent intent = new Intent(SigninActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }
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
