package com.foodfusion.foodfusion.Network;

import com.foodfusion.foodfusion.Model.CategoryModel;
import com.foodfusion.foodfusion.Model.ForgotPassEmailModel;
import com.foodfusion.foodfusion.Model.GenericModel;
import com.foodfusion.foodfusion.Model.RecipeDetailModel;
import com.foodfusion.foodfusion.Model.RecipeModel;
import com.foodfusion.foodfusion.Model.SignUpModel;
import com.foodfusion.foodfusion.Model.TwitterLoginModel;
import com.foodfusion.foodfusion.Model.UserModel;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Rameez on 2/27/2018.
 */

public class RetrofitClient {
    private static Retrofit retrofit = null;

    public static Retrofit getClient(String baseUrl) {
//        OkHttpClient client = new OkHttpClient();
//        client.setConnectTimeout(10, TimeUnit.SECONDS);
//        client.setReadTimeout(30, TimeUnit.SECONDS);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
        }

        return retrofit;
    }
    public interface APIService {
        @GET("json.php")
        Call<UserModel> Login(@Query("action") String action,
                              @Query("email") String email,
                              @Query("pwd") String pwd);

        @GET("json.php")
        Call<TwitterLoginModel> LoginFB(@Query("action") String action,
                                @Query("fb_id") String fb_id,
                                @Query("fb_name") String fb_name,
                                @Query("email ") String email  );

        @GET("json.php")
        Call<TwitterLoginModel> LoginTwitter(@Query("action") String action,
                                             @Query("tweet_id") String tweet_id,
                                             @Query("tweet_name") String tweet_name,
                                             @Query("email ") String email  );

        @POST("json.php")
        Call<List<RecipeModel>> HomeTypeRecipe(@Query("action") String action,
                                               @Query("user_id") Long user_id);

        @POST("json.php")
        Call<RecipeDetailModel> DetailRecipe(@Query("action") String action,
                                             @Query("post_id") int post_id,
                                             @Query("user_id") Long user_id);

        @POST("json.php")
        Call<List<RecipeModel>> HomeTrendingRecipe(@Query("action") String action);

        @POST("json.php")
        Call<List<RecipeModel>> GetAllRecipe(@Query("action") String action,
                                             @Query("paged") int paged,
                                             @Query("user_id") Long user_id);

        @POST("json.php")
        Call<GenericModel> AddDeleteFavourite(@Query("action") String action,
                                         @Query("user_id") Long user_id,
                                         @Query("post_id") int post_id);
        @POST("json.php")
        Call<List<RecipeModel>> ShowRecipes(@Query("action") String action,
                                             @Query("paged") int paged,
                                             @Query("user_id") Long user_id);

        @POST("json.php")
        Call<List<CategoryModel>> GetCategories(@Query("action") String action);

        @POST("json.php")
        Call<SignUpModel> SignUp(@Query("action") String action,
                                 @Query("fname") String fname,
                                 @Query("lname") String lname,
                                 @Query("uname") String uname,
                                 @Query("email") String email,
                                 @Query("pwd") String pwd);

        @POST("json.php")
        Call<List<RecipeModel>> GetRecipeByCategory(@Query("action") String action,
                                                    @Query("paged") int paged,
                                                    @Query("user_id") Long user_id,
                                                    @Query("category_id") int category_id);

        @POST("json.php")
        Call<List<RecipeModel>> GetSearch(@Query("action") String action,
                                                    @Query("paged") int paged,
                                                    @Query("user_id") Long user_id,
                                                    @Query("search") String search,
                                                    @Query("category_name") String category_name);

        @POST("json.php")
        Call<GenericModel> PasswordChange(@Query("action") String action,
                                          @Query("user_id") Long user_id,
                                          @Query("old_pass") String old_pass,
                                          @Query("new_pass") String new_pass);

        @POST("json.php")
        Call<ForgotPassEmailModel> ForgotPassEmail(@Query("action") String action,
                                                   @Query("email") String email);

        @POST("json.php")
        Call<GenericModel> ForgotPassCode(@Query("action") String action,
                                                   @Query("user_id") Long user_id,
                                          @Query("code") Integer code
                                          );
        @POST("json.php")
        Call<GenericModel> ForgotPassReset(@Query("action") String action,
                                          @Query("user_id") Long user_id,
                                          @Query("new_pass") String new_pass
        );

        // @FormUrlEncoded
    }
}
