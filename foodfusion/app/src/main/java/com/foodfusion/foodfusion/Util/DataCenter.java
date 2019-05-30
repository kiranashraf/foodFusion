package com.foodfusion.foodfusion.Util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.foodfusion.foodfusion.DB.MySharedPreference;
import com.foodfusion.foodfusion.Model.CategoryModel;
import com.foodfusion.foodfusion.Model.ForgotPassEmailModel;
import com.foodfusion.foodfusion.Model.GenericModel;
import com.foodfusion.foodfusion.Model.RecipeDetailModel;
import com.foodfusion.foodfusion.Model.RecipeModel;
import com.foodfusion.foodfusion.Model.SignUpModel;
import com.foodfusion.foodfusion.Model.TwitterLoginModel;
import com.foodfusion.foodfusion.Model.UserModel;
import com.foodfusion.foodfusion.Network.RetrofitClient;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Rameez on 2/26/2018.
 */

public class DataCenter {
    private static RetrofitClient.APIService mAPIService;
    private static MySharedPreference mMySharedPreference;

    public static void Login(final Context context,final String action, final String email, final String pwd) {
        try {
            mMySharedPreference=new MySharedPreference(context);
            final SharedPreferences appSharedPrefs = PreferenceManager
                    .getDefaultSharedPreferences(context);

            mAPIService = ApiUtils.getAPIService();
            mAPIService.Login(action, email, pwd).enqueue(new Callback<UserModel>()  {
                @Override
                public void onResponse(Call<UserModel> loginResponse, Response<UserModel> response) {
                    try {
                        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
                        Gson gson = new Gson();
                        String json = gson.toJson(response.body());
                        mMySharedPreference.setUserModel(json);
                        mMySharedPreference.setUserLoggedIn(true);
                        Intent intent = new Intent("UserLogin");
                        prefsEditor.putString("UserLoginData", json);
                        prefsEditor.commit();
                        context.sendBroadcast(intent);
                    }
                    catch (Exception ex){

                    }
                }

                @Override
                public void onFailure(Call<UserModel> call, Throwable t) {
                    Utility.hideLoader(context);
                    Utility.showMessage(context, "Login failed: Could not complete your request", "Info");
                }

            });
        }
        catch (Exception ex){
            Utility.hideLoader(context);
            Utility.showMessage(context,"Login failed: Could not complete your request","Info");
        }
    }
    public static void LoginFB(final Context context,final String action,final String id, final String name, final String email) {
        try {
            mMySharedPreference=new MySharedPreference(context);
            final SharedPreferences appSharedPrefs = PreferenceManager
                    .getDefaultSharedPreferences(context);

            mAPIService = ApiUtils.getAPIService();
            mAPIService.LoginFB(action,id,name,email).enqueue(new Callback<TwitterLoginModel>()  {
                @Override
                public void onResponse(Call<TwitterLoginModel> loginResponse, Response<TwitterLoginModel> response) {
                    try {
                    SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(response.body());
//                    if(json.equals("null")){
//                        UserModel mod = new UserModel();
//                        mod.setDisplayName(name);
//                        mod.setUserLogin(name);
//                        mod.setID(Long.parseLong(id));
//                        if(email!=null)
//                            mod.setEmail(email);
//
//                        String json1 = gson.toJson(mod);
//                        mMySharedPreference.setUserModel(json1);
//                        mMySharedPreference.setUserLoggedIn(true);
//                    } else
                    if(response.body().getStatus()==1) {

                        UserModel mod = new UserModel();
                        mod.setDisplayName(name);
                        mod.setUserLogin(name);
                        mod.setUsername(name);
                        mod.setID(response.body().getUser_id());
                        if(email!=null)
                        mod.setEmail(email);

                        String json1 = gson.toJson(mod);
                        mMySharedPreference.setUserModel(json1);
                        mMySharedPreference.setSocialUserLoggedIn(true);
                        mMySharedPreference.setUserLoggedIn(true);
                    }
                    else {
                        //Utility.showMessage(context, "SignUp failed: "+response.body().getMessage(), "Info");
                    }
                    Intent intent = new Intent("FBUser");
                    prefsEditor.putString("FBUserData", json);
                    prefsEditor.commit();
                    context.sendBroadcast(intent);
                    }
                    catch (Exception ex){

                    }
                }

                @Override
                public void onFailure(Call<TwitterLoginModel> call, Throwable t) {
                    Utility.hideLoader(context);
                    Utility.showMessage(context, "SignUp failed: Could not complete your request", "Info");
                }

            });
        }
        catch (Exception ex){
            Utility.hideLoader(context);
            Utility.showMessage(context,"SignUp failed: Could not complete your request","Info");
        }
    }

    public static void LoginTwitter(final Context context,final String action,final String id, final String name, final String email) {
        try {
            mMySharedPreference=new MySharedPreference(context);
            final SharedPreferences appSharedPrefs = PreferenceManager
                    .getDefaultSharedPreferences(context);

            mAPIService = ApiUtils.getAPIService();
            mAPIService.LoginTwitter(action,id,name,email).enqueue(new Callback<TwitterLoginModel>()  {
                @Override
                public void onResponse(Call<TwitterLoginModel> loginResponse, Response<TwitterLoginModel> response) {
                    try{
                    SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(response.body());

//                    if(json.equals("null")){
//                        UserModel mod = new UserModel();
//                        mod.setDisplayName(name);
//                        mod.setUserLogin(name);
//                        mod.setID(Long.parseLong(id));
//                        if(email!=null)
//                            mod.setEmail(email);
//
//                        String json1 = gson.toJson(mod);
//                        mMySharedPreference.setUserModel(json1);
//                        mMySharedPreference.setUserLoggedIn(true);
//                    } else
                    if(response.body().getStatus()==1) {

                        UserModel mod = new UserModel();
                        mod.setDisplayName(name);
                        mod.setUserLogin(name);
                        mod.setID(response.body().getUser_id());
                        if(email!=null)
                            mod.setEmail(email);

                        String json1 = gson.toJson(mod);
                        mMySharedPreference.setUserModel(json1);
                        mMySharedPreference.setUserLoggedIn(true);
                        mMySharedPreference.setSocialUserLoggedIn(true);
                    }
                    else {
                        //Utility.showMessage(context, "SignUp failed: "+response.body().getMessage(), "Info");
                    }
                    Intent intent = new Intent("TwitterUser");
                    prefsEditor.putString("TwitterUserData", json);
                    prefsEditor.commit();
                    context.sendBroadcast(intent);
                    }
                    catch (Exception ex){

                    }
                }

                @Override
                public void onFailure(Call<TwitterLoginModel> call, Throwable t) {
                    Utility.hideLoader(context);
                    Utility.showMessage(context, "SignUp failed: Could not complete your request", "Info");
                }

            });
        }
        catch (Exception ex){
            Utility.hideLoader(context);
            Utility.showMessage(context,"SignUp failed: Could not complete your request","Info");
        }
    }

    public static void SignUp(final Context context,final String action,final String fname, final String lname, final String uname, final String email, final String pwd) {
        try {
            mMySharedPreference=new MySharedPreference(context);
            final SharedPreferences appSharedPrefs = PreferenceManager
                    .getDefaultSharedPreferences(context);

            mAPIService = ApiUtils.getAPIService();
            mAPIService.SignUp(action,fname,lname,uname, email, pwd).enqueue(new Callback<SignUpModel>()  {
                @Override
                public void onResponse(Call<SignUpModel> loginResponse, Response<SignUpModel> response) {
                    try {
                    SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(response.body());
                    if(response.body().getStatus()==1) {

                        UserModel mod = new UserModel();
                        mod.setDisplayName(fname + " " + lname);
                        mod.setID(response.body().getID().longValue());
                        mod.setEmail(email);
                        mod.setUsername(uname);
                        mod.setUserLogin(uname);

                        String json1 = gson.toJson(mod);
                        mMySharedPreference.setUserModel(json1);
                        mMySharedPreference.setUserLoggedIn(true);
                    }
                    else {
//                        Utility.showMessage(context, "SignUp failed: "+response.body().getMessage(), "Info");
                    }
                        Intent intent = new Intent("UserSignUp");
                        prefsEditor.putString("UserSignUpData", json);
                        prefsEditor.commit();
                        context.sendBroadcast(intent);
                    }
                    catch (Exception ex){

                    }
                }

                @Override
                public void onFailure(Call<SignUpModel> call, Throwable t) {
                    Utility.hideLoader(context);
                    Utility.showMessage(context, "SignUp failed: Could not complete your request", "Info");
                }

            });
        }
        catch (Exception ex){
            Utility.hideLoader(context);
            Utility.showMessage(context,"SignUp failed: Could not complete your request","Info");
        }
    }

    public static void HomeTypeRecipe(final Context context,final String action) {
        try {
            mMySharedPreference=new MySharedPreference(context);
            final SharedPreferences appSharedPrefs = PreferenceManager
                    .getDefaultSharedPreferences(context);
            mAPIService = ApiUtils.getAPIService();
            Long userId= Long.valueOf(0);
            if(mMySharedPreference.isUserLogin()){
                userId=mMySharedPreference.getUserInfo().getID();
            }
            mAPIService.HomeTypeRecipe(action,userId).enqueue(new Callback<List<RecipeModel>>()  {
                @Override
                public void onResponse(Call<List<RecipeModel>> callResponse, Response<List<RecipeModel>> response) {
                    try {
                    SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(response.body());
                    Intent intent=new Intent();
                    if(action=="home-all-recipes"){
                        intent = new Intent("HomeAllRecipe");
                        prefsEditor.putString("HomeAllRecipeData", json);
                    } else if(action=="home-trending-recipes"){
                        intent = new Intent("HomeTrendingRecipe");
                        prefsEditor.putString("HomeTrendingRecipeData", json);
                    } else if(action=="home-more-recipes"){
                        intent = new Intent("HomeMayLikeRecipe");
                        prefsEditor.putString("HomeMayLikeRecipeData", json);
                    } else if(action=="home-recent-recipes"){
                        intent = new Intent("HomeRecentRecipe");
                        prefsEditor.putString("HomeRecentRecipeData", json);
                    }else if(action=="home-fav-recipes"){
                        intent = new Intent("HomeFavRecipe");
                        prefsEditor.putString("HomeFavRecipeData", json);
                    }else if(action=="home-view-recipes"){
                        intent = new Intent("HomeViewRecipe");
                        prefsEditor.putString("HomeViewRecipeData", json);
                    }

                    prefsEditor.commit();
                    context.sendBroadcast(intent);
                    }
                    catch (Exception ex){

                    }
                }

                @Override
                public void onFailure(Call<List<RecipeModel>> call, Throwable t) {
                    Utility.hideLoader(context);
                    if(appSharedPrefs.contains("HomeAllRecipeData") || appSharedPrefs.contains("HomeTrendingRecipeData")||appSharedPrefs.contains("HomeMayLikeRecipeData")
                            ||appSharedPrefs.contains("HomeRecentRecipeData") ||  appSharedPrefs.contains("HomeFavRecipeData")
                            || appSharedPrefs.contains("HomeViewRecipeData")){

                    }else {
                        Utility.showMessage(context, "API Call failed "+action+" : Could not complete your request", "Info");

                    }
                    }

            });
        }
        catch (Exception ex){
            Utility.hideLoader(context);
            Utility.showMessage(context,"API Call failed: Could not complete your request","Info");
        }
    }

    public static void DetailRecipe(final Context context,final String action, final int post_id) {
        try {
            mMySharedPreference=new MySharedPreference(context);
            final SharedPreferences appSharedPrefs = PreferenceManager
                    .getDefaultSharedPreferences(context);
            mAPIService = ApiUtils.getAPIService();
            Long userId= Long.valueOf(0);
            if(mMySharedPreference.isUserLogin()){
                userId=mMySharedPreference.getUserInfo().getID();
            }
            mAPIService.DetailRecipe(action,post_id,userId).enqueue(new Callback<RecipeDetailModel>()  {
                @Override
                public void onResponse(Call<RecipeDetailModel> callResponse, Response<RecipeDetailModel> response) {
                    try {
                    SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(response.body());
                    Intent intent = new Intent("RecipeDetail");
                    prefsEditor.putString("RecipeDetailData", json);
                    prefsEditor.commit();
                    context.sendBroadcast(intent);
                    }
                    catch (Exception ex){

                    }
                }

                @Override
                public void onFailure(Call<RecipeDetailModel> call, Throwable t) {
                    Utility.hideLoader(context);
                    Utility.showMessage(context, "API Call failed: Could not complete your request", "Info");
                }

            });
        }
        catch (Exception ex){
            Utility.hideLoader(context);
            Utility.showMessage(context,"API Call failed: Could not complete your request","Info");
        }
    }

    public static void GetAllRecipe(final Context context,final String action, final int paged) {
        try {
            mMySharedPreference=new MySharedPreference(context);
            final SharedPreferences appSharedPrefs = PreferenceManager
                    .getDefaultSharedPreferences(context);
            mAPIService = ApiUtils.getAPIService();
            Long userId= Long.valueOf(0);
            if(mMySharedPreference.isUserLogin()){
                userId=mMySharedPreference.getUserInfo().getID();
            }
            mAPIService.GetAllRecipe(action, paged,userId).enqueue(new Callback<List<RecipeModel>>()  {
                @Override
                public void onResponse(Call<List<RecipeModel>> callResponse, Response<List<RecipeModel>> response) {
                    try {
                    SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(response.body());
                    Intent intent=new Intent();
                    if(action=="get-all-trending-recipes" ){
                        intent = new Intent("TrendingAllRecipe");
                        prefsEditor.putString("TrendingAllRecipeData", json);
                    } else if (action=="get-all-more-recipes"){
                        intent = new Intent("GetAllMoreRecipe");
                        prefsEditor.putString("GetAllMoreRecipeData", json);
                    }else if(action=="show_fav_recipes"){
                        intent = new Intent("FavAllRecipe");
                        prefsEditor.putString("FavAllRecipeData", json);
                    }
                    if(action=="get_product_by_category"){
                        intent = new Intent("CategoryAllRecipe");
                        prefsEditor.putString("CategoryAllRecipeData", json);
                    }

                    prefsEditor.commit();
                    context.sendBroadcast(intent);
                    }
                    catch (Exception ex){

                    }
                }

                @Override
                public void onFailure(Call<List<RecipeModel>> call, Throwable t) {
                    Utility.hideLoader(context);
                    Utility.showMessage(context,"No recipes found", "Info");
                }

            });
        }
        catch (Exception ex){
            Utility.hideLoader(context);
            Utility.showMessage(context,"API Call failed: Could not complete your request","Info");
        }
    }

    public static void GetSearch(final Context context,final String action, final int paged, final String search, final String category_name) {
        try {
            mMySharedPreference=new MySharedPreference(context);
            final SharedPreferences appSharedPrefs = PreferenceManager
                    .getDefaultSharedPreferences(context);
            mAPIService = ApiUtils.getAPIService();
            Long userId= Long.valueOf(0);
            if(mMySharedPreference.isUserLogin()){
                userId=mMySharedPreference.getUserInfo().getID();
            }
            mAPIService.GetSearch(action, paged,userId,search,category_name).enqueue(new Callback<List<RecipeModel>>()  {
                @Override
                public void onResponse(Call<List<RecipeModel>> callResponse, Response<List<RecipeModel>> response) {
                    try {
                    SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(response.body());
                    Intent intent=new Intent();
                    if(action=="search"){
                        intent = new Intent("SearchRecipe");
                        prefsEditor.putString("SearchRecipeData", json);
                    }

                    prefsEditor.commit();
                    context.sendBroadcast(intent);
                    }
                    catch (Exception ex){

                    }
                }

                @Override
                public void onFailure(Call<List<RecipeModel>> call, Throwable t) {
                    Utility.hideLoader(context);
                   // Utility.showMessage(context, "API Call failed: Could not complete your request", "Info");
                }

            });
        }
        catch (Exception ex){
            Utility.hideLoader(context);
           // Utility.showMessage(context,"API Call failed: Could not complete your request","Info");
        }
    }

    public static void GetSearchDetail(final Context context,final String action, final int paged, final String search, final String category_name ) {
        try {
            mMySharedPreference=new MySharedPreference(context);
            final SharedPreferences appSharedPrefs = PreferenceManager
                    .getDefaultSharedPreferences(context);
            mAPIService = ApiUtils.getAPIService();
            Long userId= Long.valueOf(0);
            if(mMySharedPreference.isUserLogin()){
                userId=mMySharedPreference.getUserInfo().getID();
            }
            mAPIService.GetSearch(action, paged,userId,search,category_name ).enqueue(new Callback<List<RecipeModel>>()  {
                @Override
                public void onResponse(Call<List<RecipeModel>> callResponse, Response<List<RecipeModel>> response) {
                    try {
                    SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(response.body());
                    Intent intent=new Intent();
                    if(action=="search"){
                        intent = new Intent("SearchDetailRecipe");
                        prefsEditor.putString("SearchDetailRecipeData", json);
                    }

                    prefsEditor.commit();
                    context.sendBroadcast(intent);
                    }
                    catch (Exception ex){

                    }
                }

                @Override
                public void onFailure(Call<List<RecipeModel>> call, Throwable t) {
                    Utility.hideLoader(context);
                    Utility.showMessage(context, "No Recipes Found", "Info");
                }

            });
        }
        catch (Exception ex){
            Utility.hideLoader(context);
            Utility.showMessage(context,"API Call failed: Could not complete your request","Info");
        }
    }

    public static void AddDeleteFavourite(final Context context,final String action, final int post_id) {
        try {
            mMySharedPreference=new MySharedPreference(context);
            final SharedPreferences appSharedPrefs = PreferenceManager
                    .getDefaultSharedPreferences(context);
            mAPIService = ApiUtils.getAPIService();
            Long userId= Long.valueOf(0);
            if(mMySharedPreference.isUserLogin()){
                userId=mMySharedPreference.getUserInfo().getID();
            }
            mAPIService.AddDeleteFavourite(action,userId, post_id).enqueue(new Callback<GenericModel>()  {
                @Override
                public void onResponse(Call<GenericModel> callResponse, Response<GenericModel> response) {
                    try {
                    SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(response.body());
                    Intent intent=new Intent();
                        intent = new Intent("FavouriteMade");
                        prefsEditor.putString("FavouriteMadeData", json);


                    prefsEditor.commit();
                    context.sendBroadcast(intent);
                    }
                    catch (Exception ex){

                    }
                }

                @Override
                public void onFailure(Call<GenericModel> call, Throwable t) {
                    Utility.hideLoader(context);
                    Utility.showMessage(context, "No recipes found!", "Info");
                }

            });
        }
        catch (Exception ex){
            Utility.hideLoader(context);
            Utility.showMessage(context,"API Call failed: Could not complete your request","Info");
        }
    }
    public static void ShowRecipes(final Context context,final String action, final int paged) {
        try {
            mMySharedPreference=new MySharedPreference(context);
            final SharedPreferences appSharedPrefs = PreferenceManager
                    .getDefaultSharedPreferences(context);
            mAPIService = ApiUtils.getAPIService();
            Long userId= Long.valueOf(0);
            if(mMySharedPreference.isUserLogin()){
                userId=mMySharedPreference.getUserInfo().getID();
            }
            mAPIService.ShowRecipes(action, paged,userId).enqueue(new Callback<List<RecipeModel>>()  {
                @Override
                public void onResponse(Call<List<RecipeModel>> callResponse, Response<List<RecipeModel>> response) {
                    try {
                    SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(response.body());
                    Intent intent=new Intent();
                    if(action=="healthy-fusion"){
                        intent = new Intent("HealthyFusionAllRecipe");
                        prefsEditor.putString("HealthyFusionAllRecipeData", json);
                    }

                    if(action=="fusion-kids"){
                        intent = new Intent("KidsFusionAllRecipe");
                        prefsEditor.putString("KidsFusionAllRecipeData", json);
                    }

                    prefsEditor.commit();
                    context.sendBroadcast(intent);
                    }
                    catch (Exception ex){

                    }
                }

                @Override
                public void onFailure(Call<List<RecipeModel>> call, Throwable t) {
                    Utility.hideLoader(context);
                    Utility.showMessage(context, "No Recipes Found", "Info");
                }

            });
        }
        catch (Exception ex){
            Utility.hideLoader(context);
            Utility.showMessage(context,"API Call failed: Could not complete your request","Info");
        }
    }

    public static void GetCategories(final Context context,final String action) {
        try {
            mMySharedPreference=new MySharedPreference(context);
            final SharedPreferences appSharedPrefs = PreferenceManager
                    .getDefaultSharedPreferences(context);
            mAPIService = ApiUtils.getAPIService();
            mAPIService.GetCategories(action).enqueue(new Callback<List<CategoryModel>>()  {
                @Override
                public void onResponse(Call<List<CategoryModel>> callResponse, Response<List<CategoryModel>> response) {
                    try {
                    SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(response.body());
                    Intent intent=new Intent();
                        intent = new Intent("GetAllCategory");
                        prefsEditor.putString("GetAllCategoryData", json);
                    mMySharedPreference.setCategories(json);

                    prefsEditor.commit();
                    context.sendBroadcast(intent);
                    }
                    catch (Exception ex){

                    }
                }

                @Override
                public void onFailure(Call<List<CategoryModel>> call, Throwable t) {
                    Utility.hideLoader(context);
//                    Utility.showMessage(context, "API Call failed: Could not complete your request", "Info");
                }

            });
        }
        catch (Exception ex){
            Utility.hideLoader(context);
            Utility.showMessage(context,"API Call failed: Could not complete your request","Info");
        }
    }
    public static void GetRecipeByCategory(final Context context,final String action, final int paged, final int category_id) {
        try {
            mMySharedPreference=new MySharedPreference(context);
            final SharedPreferences appSharedPrefs = PreferenceManager
                    .getDefaultSharedPreferences(context);
            mAPIService = ApiUtils.getAPIService();
            Long userId= Long.valueOf(0);
            if(mMySharedPreference.isUserLogin()){
                userId=mMySharedPreference.getUserInfo().getID();
            }
            mAPIService.GetRecipeByCategory(action, paged,userId,category_id).enqueue(new Callback<List<RecipeModel>>()  {
                @Override
                public void onResponse(Call<List<RecipeModel>> callResponse, Response<List<RecipeModel>> response) {
                    try {
                    SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(response.body());
                    Intent intent=new Intent();
                    if(action=="get_product_by_category"){
                        intent = new Intent("CategoryAllRecipe");
                        prefsEditor.putString("CategoryAllRecipeData", json);
                    }

                    prefsEditor.commit();
                    context.sendBroadcast(intent);
                    }
                    catch (Exception ex){

                    }
                }

                @Override
                public void onFailure(Call<List<RecipeModel>> call, Throwable t) {
                    Utility.hideLoader(context);
                    Utility.showMessage(context, "No Recipes Found", "Info");
                }

            });
        }
        catch (Exception ex){
            Utility.hideLoader(context);
            Utility.showMessage(context,"API Call failed: Could not complete your request","Info");
        }
    }

    public static List<RecipeModel> GetSearchString(final String search) {
        try {

            Call<List<RecipeModel>> call=mAPIService.GetSearch("search", 1,Long.valueOf(0),search,"");
            List<RecipeModel> lls= call.execute().body();
//            call.enqueue(new Callback<List<RecipeModel>>()  {
//                ArrayList<String> arr=new ArrayList<>();
//                @Override
//                public void onResponse(Call<List<RecipeModel>> callResponse, Response<List<RecipeModel>> response) {
//
//
//                    Gson gson = new Gson();
//                    if(response.body()!=null){
//                        for(int i=0;i<response.body().size();i++){
//                            arr.add(response.body().get(i).getTitle());
//                        }
//
//                    }
//
//                }
//
//                @Override
//                public void onFailure(Call<List<RecipeModel>> call, Throwable t) {
//                   // Utility.hideLoader(context);
//                    // Utility.showMessage(context, "API Call failed: Could not complete your request", "Info");
//                }
//            });

                return lls;

        }
        catch (Exception ex){
           // Utility.hideLoader(context);
            // Utility.showMessage(context,"API Call failed: Could not complete your request","Info");
        }
        return null;
    }

    public static void PasswordChange(final Context context,final String action, final String old_pass, final String new_pass) {
        try {
            mMySharedPreference=new MySharedPreference(context);
            final SharedPreferences appSharedPrefs = PreferenceManager
                    .getDefaultSharedPreferences(context);
            mAPIService = ApiUtils.getAPIService();
            Long userId= Long.valueOf(0);
            if(mMySharedPreference.isUserLogin()){
                userId=mMySharedPreference.getUserInfo().getID();
            }
            mAPIService.PasswordChange(action,userId, old_pass, new_pass).enqueue(new Callback<GenericModel>()  {
                @Override
                public void onResponse(Call<GenericModel> callResponse, Response<GenericModel> response) {
                    try {
                    SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(response.body());
                    Intent intent=new Intent();
                    intent = new Intent("PasswordChange");
                    prefsEditor.putString("PasswordChangeData", json);


                    prefsEditor.commit();
                    context.sendBroadcast(intent);
                    }
                    catch (Exception ex){

                    }
                }

                @Override
                public void onFailure(Call<GenericModel> call, Throwable t) {
                    Utility.hideLoader(context);
                    Utility.showMessage(context, "Password Change failed: Could not complete your request", "Info");
                }

            });
        }
        catch (Exception ex){
            Utility.hideLoader(context);
            Utility.showMessage(context,"Password Change failed: Could not complete your request","Info");
        }
    }

    public static void ForgotPassEmail(final Context context,final String action, final String email) {
        try {
            mMySharedPreference=new MySharedPreference(context);
            final SharedPreferences appSharedPrefs = PreferenceManager
                    .getDefaultSharedPreferences(context);
            mAPIService = ApiUtils.getAPIService();

            mAPIService.ForgotPassEmail(action,email).enqueue(new Callback<ForgotPassEmailModel>()  {
                @Override
                public void onResponse(Call<ForgotPassEmailModel> callResponse, Response<ForgotPassEmailModel> response) {
                    try {
                    SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(response.body());
                    Intent intent;
                    intent = new Intent("ForgotPassEmail");
                    prefsEditor.putString("ForgotPassEmailData", json);


                    prefsEditor.commit();
                    context.sendBroadcast(intent);
                    }
                    catch (Exception ex){

                    }
                }

                @Override
                public void onFailure(Call<ForgotPassEmailModel> call, Throwable t) {
                    Utility.hideLoader(context);
                    Utility.showMessage(context, "Email doesn't exist, please sign up", "Info");
                }

            });
        }
        catch (Exception ex){
            Utility.hideLoader(context);
            Utility.showMessage(context,"Password Change failed: Could not complete your request","Info");
        }
    }

    public static void ForgotPassCode(final Context context,final String action, final Integer code) {
        try {
            mMySharedPreference=new MySharedPreference(context);
            final SharedPreferences appSharedPrefs = PreferenceManager
                    .getDefaultSharedPreferences(context);
            mAPIService = ApiUtils.getAPIService();

            Long userId= Long.valueOf(0);
            if(mMySharedPreference.getTempUserId()!=null){
                userId=Long.valueOf(mMySharedPreference.getTempUserId());
            }
            mAPIService.ForgotPassCode(action,userId,code).enqueue(new Callback<GenericModel>()  {
                @Override
                public void onResponse(Call<GenericModel> callResponse, Response<GenericModel> response) {
                    try {
                    SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(response.body());
                    Intent intent;
                    intent = new Intent("ForgotPassCode");
                    prefsEditor.putString("ForgotPassCodeData", json);


                    prefsEditor.commit();
                    context.sendBroadcast(intent);
                    }
                    catch (Exception ex){

                    }
                }

                @Override
                public void onFailure(Call<GenericModel> call, Throwable t) {
                    Utility.hideLoader(context);
                    Utility.showMessage(context, "Password Reset failed: Could not complete your request", "Info");
                }

            });
        }
        catch (Exception ex){
            Utility.hideLoader(context);
            Utility.showMessage(context,"Password Change failed: Could not complete your request","Info");
        }
    }
    public static void ForgotPassReset(final Context context,final String action, final String newEmail) {
        try {
            mMySharedPreference=new MySharedPreference(context);
            final SharedPreferences appSharedPrefs = PreferenceManager
                    .getDefaultSharedPreferences(context);
            mAPIService = ApiUtils.getAPIService();

            Long userId= Long.valueOf(0);
            if(mMySharedPreference.getTempUserId()!=null){
                userId=Long.valueOf(mMySharedPreference.getTempUserId());
            }
            mAPIService.ForgotPassReset(action,userId,newEmail).enqueue(new Callback<GenericModel>()  {
                @Override
                public void onResponse(Call<GenericModel> callResponse, Response<GenericModel> response) {
                    try {
                    SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(response.body());
                    Intent intent;
                    intent = new Intent("ForgotPassReset");
                    prefsEditor.putString("ForgotPassResetData", json);


                    prefsEditor.commit();
                    context.sendBroadcast(intent);
                    }
                    catch (Exception ex){

                    }
                }

                @Override
                public void onFailure(Call<GenericModel> call, Throwable t) {
                    Utility.hideLoader(context);
                    Utility.showMessage(context, "Password Reset failed: Could not complete your request", "Info");
                }

            });
        }
        catch (Exception ex){
            Utility.hideLoader(context);
            Utility.showMessage(context,"Password Change failed: Could not complete your request","Info");
        }
    }

}
