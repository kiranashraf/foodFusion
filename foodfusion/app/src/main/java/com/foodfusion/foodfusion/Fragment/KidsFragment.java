package com.foodfusion.foodfusion.Fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.felipecsl.asymmetricgridview.library.widget.AsymmetricGridView;
import com.foodfusion.foodfusion.Adapter.GridAdapter;
import com.foodfusion.foodfusion.Custom.CircleTransform;
import com.foodfusion.foodfusion.Custom.GridViewWithHeaderAndFooter;
import com.foodfusion.foodfusion.DB.MySharedPreference;
import com.foodfusion.foodfusion.DashboardActivity;
import com.foodfusion.foodfusion.Global;
import com.foodfusion.foodfusion.ImageCache.ImageFetcherNew;
import com.foodfusion.foodfusion.ItemDetailActivity;
import com.foodfusion.foodfusion.Model.DemoItem;
import com.foodfusion.foodfusion.Model.RecipeModel;
import com.foodfusion.foodfusion.R;
import com.foodfusion.foodfusion.Util.DataCenter;
import com.foodfusion.foodfusion.Util.Utility;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class KidsFragment extends Fragment implements AdapterView.OnItemClickListener ,AbsListView.OnScrollListener {

    AsymmetricGridView listView;
    Activity currentActivity;
    final List<DemoItem> items = new ArrayList<>();
    MySharedPreference mMySharedPreference;
    static int Pager=1;
    ImageView iv_type_like,iv_type_logo,mimg_recipe;
    TextView mtv_title,mtv_content;
    GridViewWithHeaderAndFooter mgrid;
    GridAdapter mGridAdapter;
    LinearLayout loadMore1;
    View llHeader;
    View mHeaderView,mFooterView;
    private ImageFetcherNew mImageFetcher;
    LinearLayout data, noData;
    TextView reload;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_healthy_fusion, container, false);


        // initialize your items array

    }
    @Override
    public void onItemClick(@NonNull AdapterView<?> parent, @NonNull View view,
                            int position, long itemId) {
        Intent intent = new Intent(this.getContext(), ItemDetailActivity.class);
        intent.putExtra("id",items.get(position).getRecipe().getID());
        this.getContext().startActivity(intent);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        currentActivity = getActivity();
        mImageFetcher=((Global) getActivity().getApplication()).getImageFetcher(currentActivity);
        mMySharedPreference= new MySharedPreference(currentActivity);
        data=(LinearLayout)getView().findViewById(R.id.data);
        noData=(LinearLayout)getView().findViewById(R.id.noData);
        Utility.overrideFonts(currentActivity, noData);
        reload=(TextView)getView().findViewById(R.id.reload);
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.showLoader(currentActivity);
                if(((DashboardActivity)getActivity()).checkIfNet()){


                    Intent intent=new Intent(currentActivity,DashboardActivity.class);
                    currentActivity.startActivity(intent);
                    currentActivity.finish();


                }else{
                    data.setVisibility(GONE);
                    noData.setVisibility(VISIBLE);
                    Toast.makeText(currentActivity,R.string.stillNoConnection,Toast.LENGTH_SHORT).show();
                    Utility.hideLoader(currentActivity);
                }
            }
        });
        Pager=1;

        mgrid = (GridViewWithHeaderAndFooter) getView().findViewById(R.id.grid);
        LayoutInflater LayoutInflaterinflater = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        mHeaderView = LayoutInflater.from(getActivity())
                .inflate(R.layout.grid_header, mgrid, false);

        mgrid.addHeaderView(mHeaderView, null, false);

        mFooterView = LayoutInflater.from(getActivity())
                .inflate(R.layout.grid_footer, mgrid, false);
        mgrid.addFooterView(mFooterView, null, false);
        initializeData(currentActivity);




    }
    public void initializeData(Context context){
        try {
            if(!((DashboardActivity)getActivity()).checkIfNet()){
                SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
                if (appSharedPrefs.contains("KidsFusionAllRecipeData")) {
                    //populateRecipe();
                    populateCacheRecipe();
                    data.setVisibility(VISIBLE);
                    noData.setVisibility(GONE);
                }else{
                    data.setVisibility(GONE);
                    noData.setVisibility(VISIBLE);
                }
            } else {
                data.setVisibility(VISIBLE);
                noData.setVisibility(GONE);
                SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
                if (appSharedPrefs.contains("KidsFusionAllRecipeData")) {
                    //populateRecipe();
                    populateCacheRecipe();
                }


                Utility.showLoader(context);
                DataCenter.ShowRecipes(context, "fusion-kids", Pager);
            }
        }
        catch(Exception ex){
            Utility.hideLoader(currentActivity);
            Utility.showMessage(context,  getString(R.string.defaultError), getString(R.string.alert));
        }
    }
    public void initializeData1(){
        try {


            Utility.showLoader(this.getActivity());
            DataCenter.ShowRecipes(this.getActivity(), "fusion-kids",Pager);



        }
        catch(Exception ex){
            Utility.hideLoader(currentActivity);
            Utility.showMessage(this.getActivity(),  getString(R.string.defaultError), getString(R.string.alert));
        }
    }
    void populateRecipe(){

        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        Gson gson = new Gson();
        String json = "",json1="";
        if (appSharedPrefs.contains("KidsFusionAllRecipeData")){
            json = appSharedPrefs.getString("KidsFusionAllRecipeData", "");
            TypeToken<List<RecipeModel>> token = new TypeToken<List<RecipeModel>>() {};
            final List<RecipeModel> responce = gson.fromJson(json, token.getType());
            //List<RecipeModel> responce = gson.fromJson(json, RecipeModel.class);
            if(responce!=null){
                if(Pager>1){
                    mgrid.smoothScrollToPosition(mGridAdapter.getCount()+1);
                    for(int i=0;i<responce.size();i++)
                        mGridAdapter.add(responce.get(i));
                    if(responce.size()>0) {
                        // Update the GridView
                        mGridAdapter.notifyDataSetChanged();

                        Pager++;
                    }else {
                        try {
                            loadMore1.setVisibility(GONE);
                        } catch(Exception ex){
                            Utility.hideLoader(currentActivity);
                            //FirebaseCrash.log(ex.getMessage());
                        }
                    }

                }
                else {

                    if(responce.size()==1){

                    }else {
                        if (appSharedPrefs.contains("KidsFusionAllRecipeCacheData")){
                            json1 = appSharedPrefs.getString("KidsFusionAllRecipeCacheData", "");
                        }
                        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
                        prefsEditor.putString("KidsFusionAllRecipeCacheData", json);
                        prefsEditor.commit();



                        if(!json.equals(json1)) {
                            ArrayList<RecipeModel> responce1 = new ArrayList<>();
                            if (responce != null) {
                                for (int i = 1; i < responce.size(); i++) {
                                    responce1.add(responce.get(i));
                                }
                                responce1.add(responce.get(0));
                            }
                            mGridAdapter = new GridAdapter(getContext(), (ArrayList<RecipeModel>) responce1, ((Global) getActivity().getApplication()).getImageFetcher(currentActivity));


                            loadMore1 = (LinearLayout) mFooterView.findViewById(R.id.loadmore1);
                            loadMore1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    try {
                                        initializeData1();
                                    } catch (Exception ex) {
                                        Utility.hideLoader(currentActivity);
                                    }
                                    // loadMore1.setVisibility(View.GONE);
                                }
                            });


                            mimg_recipe = (ImageView) mHeaderView.findViewById(R.id.img_recipe);
//                        Global.picassoWithCache.with(this.getContext())
//                                .load(responce.get(0).getImage()).transform(new CircleTransform()).into(mimg_recipe);
                            Global.picassoWithCache.with(getActivity())
                                    .load(responce.get(0).getImage())
                                    .networkPolicy(NetworkPolicy.OFFLINE)
                                    .transform(new CircleTransform())
                                    .into(mimg_recipe, new Callback() {
                                        @Override
                                        public void onSuccess() {

                                        }

                                        @Override
                                        public void onError() {
                                            //Try again online if cache failed
                                            Global.picassoWithCache.with(getActivity())
                                                    .load(responce.get(0).getImage())
                                                    .error(R.mipmap.imagefetcher)
                                                    .transform(new CircleTransform())
                                                    .into(mimg_recipe, new Callback() {
                                                        @Override
                                                        public void onSuccess() {

                                                        }

                                                        @Override
                                                        public void onError() {
                                                            Log.v("Picasso","Could not fetch image");
                                                        }
                                                    });
                                        }
                                    });

//                            mImageFetcher.loadDashboardImage(responce.get(0).getImage(), mimg_recipe);

                            mtv_title = (TextView) mHeaderView.findViewById(R.id.tv_title);

                            mimg_recipe.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(currentActivity, ItemDetailActivity.class);
                                    intent.putExtra("id", responce.get(0).getID());
                                    currentActivity.startActivity(intent);
                                }
                            });
//                        mtv_title.setText(responce.get(0).getTitle().replace("&","and").replace("#308;",""));
                            mtv_title.setText(Html.fromHtml(responce.get(0).getTitle()));
//                        mtv_title.setTypeface(mtv_title.getTypeface(),BOLD);
                            mtv_content = (TextView) mHeaderView.findViewById(R.id.tv_content);
                            iv_type_like = (ImageView) mHeaderView.findViewById(R.id.iv_type_like);
                            iv_type_logo = (ImageView) mHeaderView.findViewById(R.id.iv_type_logo);
//                        mtv_content.setText(responce.get(0).getContent().replace("&","and").replace("#308;","").replace("amp;",""));
                            mtv_content.setText(Html.fromHtml(responce.get(0).getContent()));
                            iv_type_like.setTag(R.id.recipeModelId, responce.get(0));
                            if (responce.get(0).getIsFavourite() == 1) {
                                iv_type_like.setImageResource(R.drawable.liked_detail_icon);
                                iv_type_like.setTag(R.id.likeImageId, R.drawable.liked_detail_icon);
                            } else {
                                iv_type_like.setImageResource(R.drawable.like_detail_icon);
                                iv_type_like.setTag(R.id.likeImageId, R.drawable.like_detail_icon);
                            }

                            if (responce.get(0).getTermId().equals("464") || responce.get(0).getTermSlug().equals("food-fusion-kids")) {
                                iv_type_logo.setImageResource(R.drawable.ff_kids_symbol);
                            } else if (responce.get(0).getTermId().equals("463") || responce.get(0).getTermSlug().equals("healthy-fusion")) {
                                iv_type_logo.setImageResource(R.drawable.ff_healthy_symbol);
                            } else {
                                iv_type_logo.setImageResource(R.drawable.ff_logo_small_icon);
                            }
//                        View myView = getContext().inflater.inflate(R.layout.grid_header, null);
//                        linearLayout_row1_recent_released.addView(myview,
//                                new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT) );
//                        llHeader=(View)getView().findViewById(R.id.llHeader);
//
//                        mgrid.addHeaderView(llHeader);
                            mgrid.setAdapter(mGridAdapter);
                            mgrid.setOnScrollListener(this);
                        }
                        Pager++;
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



            }
            else {
                try {
                    loadMore1.setVisibility(GONE);
                } catch(Exception ex){
                    Utility.hideLoader(currentActivity);
                    //FirebaseCrash.log(ex.getMessage());
                }
                Utility.hideLoader(this.getContext());
                Utility.showMessage(this.getContext(),  getString(R.string.defaultError),"Info");
            }
        }


    }
    void populateCacheRecipe(){

        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        Gson gson = new Gson();
        String json = "";
        if (appSharedPrefs.contains("KidsFusionAllRecipeCacheData")){
            json = appSharedPrefs.getString("KidsFusionAllRecipeCacheData", "");
            TypeToken<List<RecipeModel>> token = new TypeToken<List<RecipeModel>>() {};
            final List<RecipeModel> responce = gson.fromJson(json, token.getType());
            //List<RecipeModel> responce = gson.fromJson(json, RecipeModel.class);
            if(responce!=null){


                if(responce.size()==1){

                }else {

                    ArrayList<RecipeModel> responce1=new ArrayList<>();
                    if(responce!=null){
                        for(int i=1;i<responce.size();i++){
                            responce1.add(responce.get(i));
                        }
                        responce1.add(responce.get(0));}
                    mGridAdapter = new GridAdapter(getContext(), (ArrayList<RecipeModel>) responce1,((Global) getActivity().getApplication()).getImageFetcher(currentActivity));



                    loadMore1=(LinearLayout)mFooterView.findViewById(R.id.loadmore1);
                    loadMore1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                mgrid.setScrollY(mGridAdapter.getCount() - 1);
                                initializeData1();
                            }
                            catch (Exception ex){
                                Utility.hideLoader(currentActivity);
                                Utility.hideLoader(currentActivity);
                            }
                        }
                    });


                    mimg_recipe= (ImageView) mHeaderView.findViewById(R.id.img_recipe);
//                    Global.picassoWithCache.with(this.getContext())
//                            .load(responce.get(0).getImage()).transform(new CircleTransform()).into(mimg_recipe);
                    Global.picassoWithCache.with(getActivity())
                            .load(responce.get(0).getImage())
                            .networkPolicy(NetworkPolicy.OFFLINE)
                            .transform(new CircleTransform())
                            .into(mimg_recipe, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {
                                    //Try again online if cache failed
                                    Global.picassoWithCache.with(getActivity())
                                            .load(responce.get(0).getImage())
                                            .error(R.mipmap.imagefetcher)
                                            .transform(new CircleTransform())
                                            .into(mimg_recipe, new Callback() {
                                                @Override
                                                public void onSuccess() {

                                                }

                                                @Override
                                                public void onError() {
                                                    Log.v("Picasso","Could not fetch image");
                                                }
                                            });
                                }
                            });

//                    mImageFetcher.loadDashboardImage(responce.get(0).getImage(),mimg_recipe);
                    mtv_title=(TextView) mHeaderView.findViewById(R.id.tv_title);

                    mimg_recipe.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(currentActivity, ItemDetailActivity.class);
                            intent.putExtra("id",responce.get(0).getID());
                            currentActivity.startActivity(intent);
                        }
                    });
//                        mtv_title.setText(responce.get(0).getTitle().replace("&","and").replace("#308;",""));
                    mtv_title.setText(Html.fromHtml(responce.get(0).getTitle()));
//                        mtv_title.setTypeface(mtv_title.getTypeface(),BOLD);
                    mtv_content=(TextView) mHeaderView.findViewById(R.id.tv_content);
                    iv_type_like=(ImageView)mHeaderView.findViewById(R.id.iv_type_like);
                    iv_type_logo=(ImageView)mHeaderView.findViewById(R.id.iv_type_logo);
//                        mtv_content.setText(responce.get(0).getContent().replace("&","and").replace("#308;","").replace("amp;",""));
                    mtv_content.setText(Html.fromHtml(responce.get(0).getContent()));
                    iv_type_like.setTag(R.id.recipeModelId,responce.get(0));
                    if(responce.get(0).getIsFavourite()==1){
                        iv_type_like.setImageResource(R.drawable.liked_detail_icon);
                        iv_type_like.setTag(R.id.likeImageId,R.drawable.liked_detail_icon);
                    } else {
                        iv_type_like.setImageResource(R.drawable.like_detail_icon);
                        iv_type_like.setTag(R.id.likeImageId,R.drawable.like_detail_icon);
                    }

                    if(responce.get(0).getTermId().equals("464") ||responce.get(0).getTermSlug().equals("food-fusion-kids" )){
                        iv_type_logo.setImageResource(R.drawable.ff_kids_symbol);
                    }else if (responce.get(0).getTermId().equals("463") ||responce.get(0).getTermSlug().equals("healthy-fusion")){
                        iv_type_logo.setImageResource(R.drawable.ff_healthy_symbol);
                    } else {
                        iv_type_logo.setImageResource(R.drawable.ff_logo_small_icon);
                    }
//                        View myView = getContext().inflater.inflate(R.layout.grid_header, null);
//                        linearLayout_row1_recent_released.addView(myview,
//                                new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT) );
//                        llHeader=(View)getView().findViewById(R.id.llHeader);
//
//                        mgrid.addHeaderView(llHeader);
                    mgrid.setAdapter(mGridAdapter);
                    mgrid.setOnScrollListener(this);
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




        }


    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mMessageReceiver, new IntentFilter("KidsFusionAllRecipe"));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mMessageReceiver);
    }
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("KidsFusionAllRecipe")){
                populateRecipe();
                Utility.hideLoader(currentActivity);
            }

        }
    };

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if(mGridAdapter!=null) {
            if (mGridAdapter.getCount() % 10 != 0) {
                try {
                    loadMore1.setVisibility(GONE);
                } catch(Exception ex){
                    Utility.hideLoader(currentActivity);
                    //FirebaseCrash.log(ex.getMessage());
                }
            } else {
                try {
                    loadMore1.setVisibility(VISIBLE);
                } catch(Exception ex){
                    Utility.hideLoader(currentActivity);
                    //FirebaseCrash.log(ex.getMessage());
                }
            }
//        if (mgrid.getLastVisiblePosition() + 1 == (10* (Pager-1)) + 2) {
//            loadMore1.setVisibility(View.VISIBLE);// Load More Button
//        } else {
//            loadMore1.setVisibility(View.GONE);
//        }
        }
    }



}


