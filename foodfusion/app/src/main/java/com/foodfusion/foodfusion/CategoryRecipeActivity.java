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
import android.text.Html;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.foodfusion.foodfusion.Adapter.GridAdapter;
import com.foodfusion.foodfusion.Custom.GridViewWithHeaderAndFooter;
import com.foodfusion.foodfusion.Custom.NetworkChangeReceiver;
import com.foodfusion.foodfusion.DB.MySharedPreference;
import com.foodfusion.foodfusion.Model.GenericModel;
import com.foodfusion.foodfusion.Model.RecipeModel;
import com.foodfusion.foodfusion.Util.Constants;
import com.foodfusion.foodfusion.Util.DataCenter;
import com.foodfusion.foodfusion.Util.Utility;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

/**
 * Created by Rameez on 3/8/2018.
 */

public class CategoryRecipeActivity extends AppCompatActivity implements AbsListView.OnScrollListener {

    Activity currentActivity;
    GridViewWithHeaderAndFooter mgrid;
    GridAdapter mGridAdapter;
    static int Pager=1;
    LinearLayout loadMore1;
    TextView mtrending_more;

    LinearLayout rootLayout;

    ImageView mImg_back;

    int id;
    String parentName;

    String apiCall;

    String dataSp;
    String dataCacheSp;
    TextView mtv_title;

    MySharedPreference mMySharedPreference;
    View mFooterView;
    LinearLayout network;
    LinearLayout data,noData;
    NetworkChangeReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Pager=1;
        setContentView(R.layout.activity_category_recipes);

        currentActivity = this;
        network=(LinearLayout)findViewById(R.id.network);
        data=(LinearLayout)findViewById(R.id.data);
        noData=(LinearLayout)findViewById(R.id.noData);
        mtv_title=(TextView)findViewById(R.id.tv_title);

        mMySharedPreference= new MySharedPreference(this);

        parentName= getIntent().getExtras().getString("name");
        if(parentName!=null){
            mtv_title.setText(Html.fromHtml(parentName));
        }
        id= getIntent().getExtras().getInt("id",-1);
        if(id>0){
            apiCall="get_product_by_category";
            dataSp="CategoryAllRecipeData";
            dataCacheSp="CategoryAllRecipeCacheData";
        }
        checkIfNet();
        rootLayout = (LinearLayout) findViewById(R.id.cl1);

        mgrid = (GridViewWithHeaderAndFooter) findViewById(R.id.grid);
        LayoutInflater LayoutInflaterinflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

        mFooterView = LayoutInflater.from(currentActivity).inflate(R.layout.grid_footer, mgrid, false);
        mgrid.addFooterView(mFooterView, null, false);
        loadMore1=(LinearLayout)  mFooterView.findViewById(R.id.loadmore1);
        loadMore1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    initializeData1();
                }
                catch (Exception ex){
                    Utility.hideLoader(currentActivity);
                }
            }
        });

        mImg_back=(ImageView)findViewById(R.id.img_back);
        mImg_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                finish();
            }
        });

        Utility.overrideFonts(currentActivity, rootLayout);

        initializeData();

    }

    public void initializeData(){
        try {

                SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
                if (appSharedPrefs.contains(dataCacheSp)) {
                    if (appSharedPrefs.getInt("CategoryAllRecipeCacheDataID", -1) == id) {
                        populateCacheRecipes();
                    }
                }
            if(checkIfNet()) {
                Utility.showLoader(this);
                DataCenter.GetRecipeByCategory(this, apiCall, Pager, id);
            }
        }
        catch(Exception ex){
            Utility.hideLoader(this);
            Utility.showMessage(this,  getString(R.string.defaultError), getString(R.string.alert));
        }

    }

    public void initializeData1(){
        try {
            Utility.showLoader(this);
            DataCenter.GetRecipeByCategory(this, apiCall,Pager,id);
        }
        catch(Exception ex){
            Utility.hideLoader(this);
            Utility.showMessage(this,  getString(R.string.defaultError), getString(R.string.alert));
        }

    }
    public void populateRecipes(){
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = "",json1="";
        if (appSharedPrefs.contains(dataSp)){
            json = appSharedPrefs.getString(dataSp, "");
            TypeToken<List<RecipeModel>> token = new TypeToken<List<RecipeModel>>() {};
            final List<RecipeModel> responce = gson.fromJson(json, token.getType());
            //List<RecipeModel> responce = gson.fromJson(json, RecipeModel.class);
            if(responce!=null){
                if(Pager>1){
                    for(int i=0;i<responce.size();i++)
                        mGridAdapter.add(responce.get(i));
                    // Update the GridView
                    mGridAdapter.notifyDataSetChanged();
                    Pager++;
                }
                else {
                    if(appSharedPrefs.contains(dataCacheSp)){
                        json1 = appSharedPrefs.getString(dataCacheSp, "");;
                    }
                    SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();

                    prefsEditor.putString("CategoryAllRecipeCacheData", json);
                    prefsEditor.putInt("CategoryAllRecipeCacheDataID", id);
                    prefsEditor.commit();
                    if(!json1.equals(json)) {
                        mGridAdapter = new GridAdapter(this, (ArrayList<RecipeModel>) responce, ((Global) this.getApplication()).getImageFetcher(currentActivity));
                        mgrid.setAdapter(mGridAdapter);
                        mgrid.setOnScrollListener(this);
                    }
                    Pager++;
                }
                mgrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long itemId) {
                        Intent intent = new Intent(adapterView.getContext(), ItemDetailActivity.class);
                        intent.putExtra("id",(int)itemId);
                        adapterView.getContext().startActivity(intent);

//                    Toast.makeText(getApplicationContext(), "You clicked on position : " + i + " and id : " + l, Toast.LENGTH_LONG).show();
                    }
                });
            }
            else {
                Utility.hideLoader(this);
                Utility.showMessage(this,  getString(R.string.defaultError),"Info");
            }
        }
    }

    public void populateCacheRecipes(){
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = "";
        if (appSharedPrefs.contains(dataCacheSp)){
            json = appSharedPrefs.getString(dataCacheSp, "");
            TypeToken<List<RecipeModel>> token = new TypeToken<List<RecipeModel>>() {};
            final List<RecipeModel> responce = gson.fromJson(json, token.getType());
            //List<RecipeModel> responce = gson.fromJson(json, RecipeModel.class);
            if(responce!=null){


                WindowManager windowManager = (WindowManager)this.getSystemService(Context.WINDOW_SERVICE);

                Display display = windowManager.getDefaultDisplay();
                int height=display.getHeight();


                // mgrid.getLayoutParams().height=height+height/3;
                mGridAdapter = new GridAdapter(this,(ArrayList<RecipeModel>) responce,((Global) this.getApplication()).getImageFetcher(currentActivity));
                mgrid.setAdapter(mGridAdapter);
                mgrid.setOnScrollListener(this);
            }
            mgrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long itemId) {
                    Intent intent = new Intent(adapterView.getContext(), ItemDetailActivity.class);
                    intent.putExtra("id",(int)itemId);
                    adapterView.getContext().startActivity(intent);

//                    Toast.makeText(getApplicationContext(), "You clicked on position : " + i + " and id : " + l, Toast.LENGTH_LONG).show();
                }
            });

        }

    }

    @Override
    public void onResume() {
        this.mReceiver = new NetworkChangeReceiver();
        registerReceiver(
                this.mReceiver,
                new IntentFilter(
                        ConnectivityManager.CONNECTIVITY_ACTION));
        super.onResume();
        this.registerReceiver(mMessageReceiver, new IntentFilter("CategoryAllRecipe"));
        this.registerReceiver(mMessageReceiver, new IntentFilter("FavouriteMade"));

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

            if(intent.getAction().equals("CategoryAllRecipe")){
                populateRecipes();

                Utility.hideLoader(currentActivity);
            }
            if(intent.getAction().equals("FavouriteMade")){
                try {
                    SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
                    Gson gson = new Gson();
                    String json = "";
                    if (appSharedPrefs.contains("FavouriteMadeData")) {
                        json = appSharedPrefs.getString("FavouriteMadeData", "");
                        GenericModel responce = gson.fromJson(json, GenericModel.class);
                        if (responce.getStatus() == 2) {
                            Toast.makeText(getApplicationContext(), R.string.FavouriteMakeError, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.FavouriteMakeError, Toast.LENGTH_LONG).show();
                    }
                }
                catch(Exception ex){
                    Toast.makeText(getApplicationContext(), R.string.FavouriteMakeError, Toast.LENGTH_LONG).show();
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
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//        if (mgrid.getLastVisiblePosition() + 1 == 10* (Pager-1)) {
//            loadMore1.setVisibility(View.VISIBLE);// Load More Button
//        } else {
//            loadMore1.setVisibility(View.GONE);
//        }
        if(mGridAdapter!=null) {
            if (mGridAdapter.getCount() % 10 != 0) {
                loadMore1.setVisibility(GONE);
            } else {
                loadMore1.setVisibility(View.VISIBLE);
            }
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {

            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
    public void likeClickHandler(View v) {
        if(checkIfNet()){
        if(mMySharedPreference.isUserLogin()) {


            RecipeModel item= (RecipeModel)v.getTag(R.id.recipeModelId);
            int drawable=(int)v.getTag(R.id.likeImageId);
            if(drawable==R.drawable.like_detail_icon){
                ImageView imgLike=v.findViewById(R.id.iv_like);
                imgLike.setImageResource(R.drawable.liked_detail_icon);
                imgLike.setTag(R.id.likeImageId,R.drawable.liked_detail_icon);
                DataCenter.AddDeleteFavourite(this, "fav_recipe", item.getID());
            } else {
                ImageView imgLike=v.findViewById(R.id.iv_like);
                imgLike.setImageResource(R.drawable.like_detail_icon);
                imgLike.setTag(R.id.likeImageId,R.drawable.like_detail_icon);
                DataCenter.AddDeleteFavourite(this, "delete_fav_recipe", item.getID());
            }
            DataCenter.HomeTypeRecipe(currentActivity, "home-trending-recipes");
            DataCenter.HomeTypeRecipe(currentActivity, "home-more-recipes");
            if(mMySharedPreference.getCurrentRadio()== Constants.radio.ALL.getValue())
                DataCenter.HomeTypeRecipe(currentActivity, "home-all-recipes");
            if(mMySharedPreference.getCurrentRadio()== Constants.radio.RECENT.getValue())
                DataCenter.HomeTypeRecipe(currentActivity, "home-recent-recipes");
            if(mMySharedPreference.getCurrentRadio()== Constants.radio.FAV.getValue())
                DataCenter.HomeTypeRecipe(currentActivity, "home-fav-recipes");
            if(mMySharedPreference.getCurrentRadio()== Constants.radio.VIEW.getValue())
                DataCenter.HomeTypeRecipe(currentActivity, "home-view-recipes");
        } else {
            Utility.showLoginLikeMessage(currentActivity);
        }}
        else {
            Toast.makeText(this,R.string.stillNoConnection,Toast.LENGTH_SHORT).show();
        }
        //Toast.makeText(getApplicationContext(), "You clicked on position : " + itemToRemove.getTitle() + " and id : " + itemToRemove.getID(), Toast.LENGTH_LONG).show();
        //adapter.remove(itemToRemove);

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
                    if(!appSharedPrefs.contains(dataCacheSp)){
                        data.setVisibility(View.GONE);
                        noData.setVisibility(View.VISIBLE);
                    }else {
                        if(appSharedPrefs.getInt("CategoryAllRecipeCacheDataID",-1)==id) {
                            data.setVisibility(View.VISIBLE);
                            noData.setVisibility(View.GONE);
                        } else {
                            data.setVisibility(View.GONE);
                            noData.setVisibility(View.VISIBLE);
                        }
                    }

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

    public void Reload(View v){
        Utility.showLoader(this);
        if(checkIfNet()){
            initializeData();
            data.setVisibility(View.VISIBLE);
            noData.setVisibility(View.GONE);
        }else{

            data.setVisibility(View.GONE);
            noData.setVisibility(View.VISIBLE);
            Toast.makeText(this,R.string.stillNoConnection,Toast.LENGTH_SHORT).show();
            Utility.hideLoader(this);
        }
    }

}
