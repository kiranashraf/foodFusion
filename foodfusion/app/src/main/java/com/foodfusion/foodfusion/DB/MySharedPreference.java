package com.foodfusion.foodfusion.DB;

import android.content.Context;
import android.content.SharedPreferences;

import com.foodfusion.foodfusion.Model.CategoryModel;
import com.foodfusion.foodfusion.Model.UserModel;
import com.foodfusion.foodfusion.Util.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by Rameez on 2/16/2018.
 */

public class MySharedPreference {
    private SharedPreferences sharedPreferences;
    private Context context;

    public MySharedPreference(Context context) {
        this.context = context;
        initializeSharedDb();

    }

    private void initializeSharedDb() {

        sharedPreferences = context.getSharedPreferences(Constants.MySharedPreference, Context.MODE_PRIVATE);

    }

//    public String getSelectedLanguage() {
//        if (getSharedPreferences().contains(Constants.SP_LANGUAGE)) {
//            return getSharedPreferences().getString(Constants.SP_LANGUAGE, "");
//        }
//        return null;
//    }

//    public void setSelectedLanguage(String language) {
//        setStringValue(Constants.SP_LANGUAGE, language);
//    }

    private void setStringValue(String Key, String Value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(Key, Value);
        editor.commit();
    }

    private void setIntegerValue(String Key, int Value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putInt(Key, Value);
        editor.commit();
    }

    public void setBooleanValue(String Key, boolean Value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(Key, Value);
        editor.commit();
    }

    public boolean isUserLogin() {
        return getSharedPreferences().getBoolean(Constants.SP_ISLOGGEDIN, false);

    }
    public boolean isFirstTimeBoarding() {
        return getSharedPreferences().getBoolean(Constants.SP_ISBOARDING, false);

    }

    public boolean isSocialUserLogin() {
        return getSharedPreferences().getBoolean(Constants.SP_ISSOCIALLOGGEDIN, false);

    }

    public UserModel getUserInfo() {
        Gson gson = new Gson();
        String json = "";

        return gson.fromJson(getSharedPreferences().getString(Constants.SP_USER, "{}"), UserModel.class);
    }
    public void setUserModel(String userInfo) {
        setStringValue(Constants.SP_USER, userInfo);
    }

    public void setUserPic(String url) {
        setStringValue(Constants.SP_USERPIC, url);
    }
    public String getUserPic() {

        return getSharedPreferences().getString(Constants.SP_USERPIC, "{}");
    }
    public void setCurrentRadio(String url) {
        setStringValue(Constants.SP_CURRENT_RADIO, url);
    }
    public String getCurrentRadio() {

        return getSharedPreferences().getString(Constants.SP_CURRENT_RADIO, "");
    }

    public void setCategories(String categories) {
        setStringValue(Constants.SP_CATEGORY, categories);
    }
    public void setTempUserId(Integer userId) {
        setIntegerValue(Constants.SP_TEMP_USERID, userId);
    }
    public List<CategoryModel> getCategories() {
        Gson gson = new Gson();
        String json = "";

        json = getSharedPreferences().getString(Constants.SP_CATEGORY, "");
        TypeToken<List<CategoryModel>> token = new TypeToken<List<CategoryModel>>() {};
        final List<CategoryModel> responce = gson.fromJson(json, token.getType());
        return responce;
    }

    public Integer getTempUserId() {
        return getSharedPreferences().getInt(Constants.SP_TEMP_USERID, 0);
    }

//    public void deleteUserInfo() {
//        removeKey(Constants.SP_USER);
//        Util.getInstance().deleteAllEvents(context);
//    }

    private void removeKey(String key) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.remove(key);
        editor.commit();
    }


    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }


//    public void setIntialImage(String key, String image) {
//        if (getSharedPreferences().getString(Constants.SP_INTIALIMAGE, "").length() > 0) {
//            removeKey(Constants.SP_INTIALIMAGE);
//        }
//        setStringValue(Constants.SP_INTIALIMAGE, key + ":::" + image);
//    }
//
//    public String getIntialImage(String key) {
//        if (getSharedPreferences().getString(Constants.SP_INTIALIMAGE, "").length() > 0) {
//            if (key.equalsIgnoreCase(getSharedPreferences().getString(Constants.SP_INTIALIMAGE, "").split(":::")[0])) {
//                return getSharedPreferences().getString(Constants.SP_INTIALIMAGE, ":::").split(":::")[1];
//            }
//        }
//        return "";
//    }

//    public void setStoreUserInfo(boolean status) {//,String password){
//        setBooleanValue(Constants.SP_REMEMBERME, status);
//        //setStringValue(Constants.SP_PASS,password);
//    }

//    public String getPassword(){
//        return getSharedPreferences().getString(Constants.SP_PASS, "");
//    }

//    public boolean isStoreUserInfo() {
//        return getSharedPreferences().getBoolean(Constants.SP_REMEMBERME, false);
//    }

//    public void setStoreBarberInfo(boolean status) {//,String password){
//        setBooleanValue(Constants.SP_BARBERREMEMBERME, status);
//        //setStringValue(Constants.SP_PASS,password);
//    }

//    public String getPassword(){
//        return getSharedPreferences().getString(Constants.SP_PASS, "");
//    }

//    public boolean isStoreBarberInfo() {
//        return getSharedPreferences().getBoolean(Constants.SP_BARBERREMEMBERME, false);
//    }
//
//    public void setBarberModel(String barberInfo) {
//        setStringValue(Constants.SP_BARBER, barberInfo);
//    }
//
//    public BarberLoginModel getBarberInfo() {
//        return Util.getInstance().getGson().fromJson(getSharedPreferences().getString(Constants.SP_BARBER, "{}"), BarberLoginModel.class);
//    }
//
//    public void deleteBarberInfo() {
//        removeKey(Constants.SP_BARBER);
//        Util.getInstance().deleteAllEvents(context);
//    }

//    public void setBarberLoggedIn(boolean status) {
//        setBooleanValue(Constants.SP_ISBARBERLOGGEDIN, status);
//    }
//
//    public boolean isBarberLogin() {
//        return getSharedPreferences().getBoolean(Constants.SP_ISBARBERLOGGEDIN, false);
//
//    }

    public void setUserLoggedIn(boolean status) {
        setBooleanValue(Constants.SP_ISLOGGEDIN, status);
    }

    public void setSocialUserLoggedIn(boolean status) {
        setBooleanValue(Constants.SP_ISSOCIALLOGGEDIN, status);
    }

    public void setFirstTimeBoarding(boolean status) {
        setBooleanValue(Constants.SP_ISBOARDING, status);
    }

//    public String getGCMRegistrationId() {
//        return getSharedPreferences().getString(Constants.SP_GCMREGISTRATIONID, "");
//    }
//
//    public void setGCMRegistrationId(String REGID) {
//        setStringValue(Constants.SP_GCMREGISTRATIONID, REGID);
//    }
//
//    public int getAppVersion() {
//        return getSharedPreferences().getInt(Constants.SP_APPVERSION, 0);
//    }
//
//    public void setAppVersion(int appVersion) {
//        setIntegerValue(Constants.SP_APPVERSION, appVersion);
//    }
//
//    public boolean getShowHelpScreen() {
//        return getSharedPreferences().getBoolean(Constants.SP_SHOW_HELP, true);
//    }
//
//    public void setShowHelpScreen(boolean showHelpScreen) {
//        setBooleanValue(Constants.SP_SHOW_HELP, showHelpScreen);
//    }
//
//    public int getPushNotificationsCount() {
//        return getSharedPreferences().getInt(Constants.SP_PUSH_NOTIFICATIONS_COUNT, 0);
//    }
//
//    public void setPushNotificationsCount(int count) {
//        setIntegerValue(Constants.SP_PUSH_NOTIFICATIONS_COUNT, count);
//    }
}