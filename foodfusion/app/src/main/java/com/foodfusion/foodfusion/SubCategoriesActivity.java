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
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.foodfusion.foodfusion.Adapter.CategGridAdapter;
import com.foodfusion.foodfusion.Custom.MyGridView;
import com.foodfusion.foodfusion.Custom.NetworkChangeReceiver;
import com.foodfusion.foodfusion.DB.MySharedPreference;
import com.foodfusion.foodfusion.Model.CategoryModel;
import com.foodfusion.foodfusion.Model.SubCategory;
import com.foodfusion.foodfusion.Util.Utility;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rameez on 3/8/2018.
 */

public class SubCategoriesActivity extends AppCompatActivity {
    Activity currentActivity;
    MySharedPreference mMySharedPreference;
    LinearLayout rootLayout;
    int id;
    String parentName;
    TextView mtv_title;
    List<SubCategory> lstCategory;
    List<CategoryModel> lstParentCategory;
    MyGridView mgrid;
    CategGridAdapter mGridAdapter;
    ArrayList<CategoryModel> obj;
    NetworkChangeReceiver mReceiver;
    LinearLayout network;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subcategories);

        currentActivity = this;
        network=(LinearLayout)findViewById(R.id.network);
        mtv_title=(TextView) findViewById(R.id.tv_title);
        checkIfNet();
        mMySharedPreference= new MySharedPreference(this);
        parentName= getIntent().getExtras().getString("name");
        if(parentName!=null){
            mtv_title.setText(parentName.replace("&","and").replace("#038;",""));
        }
        id= getIntent().getExtras().getInt("id",-1);
        if(id>0){
            try {
                lstParentCategory = mMySharedPreference.getCategories();
                obj= new ArrayList<CategoryModel>();

                for(int i=0;i<lstParentCategory.size();i++){
                    if(lstParentCategory.get(i).getParentId()==id){
                        lstCategory=lstParentCategory.get(i).getSubCategory();
                        break;
                    }
                }
                if(lstCategory!=null){
                    for(int j=0;j<lstCategory.size();j++){
                        CategoryModel temp=new CategoryModel();
                        temp.setCategory(lstCategory.get(j).getSubCategory());
                        temp.setParentId(lstCategory.get(j).getID());
                        temp.setTotalRecipes(lstCategory.get(j).getTotalRecipes());
                        obj.add(temp);
                    }
                } else {
                    Toast.makeText(this,R.string.error_subCategory_notFound,Toast.LENGTH_SHORT);
                }
                if(obj!=null){
                    mgrid= (MyGridView) findViewById(R.id.grid);
                    mGridAdapter = new CategGridAdapter(this,(ArrayList<CategoryModel>) obj);
                    mgrid.setAdapter(mGridAdapter);

                    mgrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long itemId) {
                            CategoryModel categ= obj.get(position);
                                Intent intent = new Intent(adapterView.getContext(), CategoryRecipeActivity.class);
                                intent.putExtra("id",(int)itemId);
                                intent.putExtra("name",categ.getCategory());
                                startActivity(intent);


                            //Toast.makeText(currentActivity, "You clicked on position : " + position + " and id : " + itemId, Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    Toast.makeText(this,R.string.error_subCategory_notFound,Toast.LENGTH_SHORT);
                }
                // apiCall="get_product_by_category";
            }
            catch(Exception ex){
                Toast.makeText(this,R.string.error_subCategory_notFound,Toast.LENGTH_SHORT);
            }
        }
        rootLayout = (LinearLayout) findViewById(R.id.cl1);
//        loadMore1=(LinearLayout)  findViewById(R.id.loadmore1);
//        mtrending_more=(TextView) findViewById(R.id.trending_more);
//        mtrending_more.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                initializeData();
//            }
//        });
//
//        mImg_back=(ImageView)findViewById(R.id.img_back);
//        mImg_back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });

        Utility.overrideFonts(currentActivity, rootLayout);

      //  initializeData();

    }
public void BackButton(View v){
        finish();
}
    @Override
    public void onResume() {
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
            // refreshScreen();
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
