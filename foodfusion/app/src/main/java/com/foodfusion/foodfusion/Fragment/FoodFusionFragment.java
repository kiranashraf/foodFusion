package com.foodfusion.foodfusion.Fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.foodfusion.foodfusion.Adapter.CategGridAdapter;
import com.foodfusion.foodfusion.Adapter.GridAdapter;
import com.foodfusion.foodfusion.Adapter.HLVAdapter;
import com.foodfusion.foodfusion.CategoryRecipeActivity;
import com.foodfusion.foodfusion.Custom.CircleTransform;
import com.foodfusion.foodfusion.Custom.EqualSpacingItemDecoration;
import com.foodfusion.foodfusion.Custom.MyGridView;
import com.foodfusion.foodfusion.DB.MySharedPreference;
import com.foodfusion.foodfusion.DashboardActivity;
import com.foodfusion.foodfusion.FilterActivity;
import com.foodfusion.foodfusion.Global;
import com.foodfusion.foodfusion.ImageCache.ImageFetcherNew;
import com.foodfusion.foodfusion.ItemDetailActivity;
import com.foodfusion.foodfusion.Model.CategoryModel;
import com.foodfusion.foodfusion.Model.GenericModel;
import com.foodfusion.foodfusion.Model.RecipeModel;
import com.foodfusion.foodfusion.R;
import com.foodfusion.foodfusion.SearchActivity;
import com.foodfusion.foodfusion.SubCategoriesActivity;
import com.foodfusion.foodfusion.TrendingActivity;
import com.foodfusion.foodfusion.Util.Constants;
import com.foodfusion.foodfusion.Util.DataCenter;
import com.foodfusion.foodfusion.Util.Utility;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.labo.kaji.relativepopupwindow.RelativePopupWindow;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
//import android.app.FragmentManager;


public class FoodFusionFragment extends Fragment {

    Activity currentActivity;
    int numberOfloader = 3;
    int total_load=0;
//    RecyclerView mRecyclerView;
//    RecyclerView.LayoutManager mLayoutManager;
//    RecyclerView.Adapter mAdapter;
    MyGridView mgridRecipe;
    GridAdapter mGridAdapterRecipe;

    RecyclerView mRecyclerViewTrending;
    RecyclerView.LayoutManager mLayoutManagerTrending;
    RecyclerView.Adapter mAdapterTrending;

    RecyclerView mRecyclerViewMayLike;
    RecyclerView.LayoutManager mLayoutManagerMayLike;
    RecyclerView.Adapter mAdapterMayLike;

    ImageView mimg_recipe;
    TextView mtv_title;
    TextView mtv_content;

    TextView mtrending_more;
    TextView mmayLike_more;

    MySharedPreference mMySharedPreference;

    MyGridView mgrid;
    CategGridAdapter mGridAdapter;
    List<CategoryModel> categories;

    RadioGroup mrdogrp;
    private RadioButton radioBtn;
    int selectedId;

    ImageView iv_type_logo, iv_type_like;
    String radioSelect="all";


    private ImageFetcherNew mImageFetcher;

    //search
    AutoCompleteTextView searchText;
    ArrayList<String> recipes;
    ArrayAdapter<String> adapter;


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
        return inflater.inflate(R.layout.fragment_food_fusion, container, false);
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        currentActivity = getActivity();
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
        mMySharedPreference=new MySharedPreference(currentActivity);
        iv_type_like=(ImageView)getView().findViewById(R.id.iv_type_like);
        iv_type_logo=(ImageView)getView().findViewById(R.id.iv_type_logo);
        //Radio
        mrdogrp=(RadioGroup)getView().findViewById(R.id.rdogrp);
        selectedId = mrdogrp.getCheckedRadioButtonId();
        mImageFetcher=((Global) getActivity().getApplication()).getImageFetcher(currentActivity);



        if(mMySharedPreference.getCurrentRadio().equals("all")){
//            initializeDataType(R.id.btn1);
            selectedId=R.id.btn1;
            radioBtn=(RadioButton)getView().findViewById(R.id.btn1);
            radioBtn.setChecked(true);
        } else if(mMySharedPreference.getCurrentRadio().equals("recent")){
//            initializeDataType(R.id.btn2);
            selectedId=R.id.btn2;
            radioBtn=(RadioButton)getView().findViewById(R.id.btn2);
            radioBtn.setChecked(true);
        }else if(mMySharedPreference.getCurrentRadio().equals("fav")){
//            initializeDataType(R.id.btn3);
            selectedId=R.id.btn3;
            radioBtn=(RadioButton)getView().findViewById(R.id.btn3);
            radioBtn.setChecked(true);
        }else if(mMySharedPreference.getCurrentRadio().equals("view")){
//            initializeDataType(R.id.btn4);
            selectedId=R.id.btn4;
            radioBtn=(RadioButton)getView().findViewById(R.id.btn4);
            radioBtn.setChecked(true);
        }

        initializeDataType(selectedId);
        //Toast.makeText(this.getActivity(),radioBtn.getText()+"",Toast.LENGTH_SHORT);
        mrdogrp.setOnCheckedChangeListener(null);
//        holder.someCheckBox.setChecked(false);
        mrdogrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                initializeDataType(checkedId);

//                Toast.makeText(currentActivity,checkedId+"",Toast.LENGTH_SHORT);
            }

        });


        //end radio



        final ImageView imgSearch = (ImageView)getView().findViewById(R.id.imgSearch);
        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog1(view);
            }
        });

        RatingBar ratingBar = (RatingBar)getView().findViewById(R.id.ratingBar);
        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(getResources().getColor(R.color.appColor), PorterDuff.Mode.SRC_ATOP);


        initializeData();
        FrameLayout rootLayout = (FrameLayout)getView().findViewById(R.id.frameMain);


        mtrending_more= (TextView) getView().findViewById(R.id.trending_more);
        mtrending_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TrendingActivity.class);
                intent.putExtra("name",Utility.ShowMore.TRENDING);
                startActivity(intent);
            }
        });
        mmayLike_more= (TextView) getView().findViewById(R.id.mayLike_more);
        mmayLike_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TrendingActivity.class);
                intent.putExtra("name",Utility.ShowMore.MAYALSOLIKE);
                startActivity(intent);
            }
        });
        initlializeDataCateg();



        initializeSearch();
        Utility.overrideFonts(currentActivity, rootLayout);
    }
public void initializeSearch(){
    searchText = (AutoCompleteTextView) getView().findViewById(R.id.searchText);
    // Get the string array
    recipes=new ArrayList<>();

    // Create the adapter and set it to the AutoCompleteTextView
    adapter = new ArrayAdapter<String>(getContext(), R.layout.search_list_popup, recipes);
    searchText.setAdapter(adapter);

    searchText.addTextChangedListener(new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            DataCenter.GetSearch(getContext(),"search",1,s.toString(),"");

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    });
//    searchText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//        @Override
//        public void onFocusChange(View view, boolean b) {
//            Toast.makeText(getContext(),"Enter",Toast.LENGTH_LONG);
//        }
//    });
    searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    event != null &&
                            event.getAction() == KeyEvent.ACTION_DOWN &&
                            event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    Intent intent = new Intent(getActivity(), SearchActivity.class);
                    intent.putExtra("name",searchText.getText());
                    startActivity(intent);
                    return true; // consume.
                }
            }
            return false; // pass on to other listeners.
        }
    });
    searchText.setOnKeyListener(new View.OnKeyListener()
    {
        public boolean onKey(View v, int keyCode, KeyEvent event)
        {
            if (event.getAction() == KeyEvent.ACTION_DOWN)
            {
                switch (keyCode)
                {
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                    case KeyEvent.KEYCODE_ENTER:
                        //addCourseFromTextBox();                        String name=searchText.getText();
                        Intent intent = new Intent(getActivity(), SearchActivity.class);
                        intent.putExtra("name",searchText.getText());
                        startActivity(intent);

                        return true;
                    default:
                        break;
                }
            }
            return false;
        }
    });
}
    public void initializeData(){
        try {
            SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
            Utility.showLoader(this.getActivity());
            if (appSharedPrefs.contains("HomeTrendingRecipeData"))
                populateTrendingRecipe();
            if (appSharedPrefs.contains("HomeMayLikeRecipeData"))
                populateMayLikeRecipe();
            DataCenter.HomeTypeRecipe(this.getActivity(), "home-trending-recipes");
            DataCenter.HomeTypeRecipe(this.getActivity(), "home-more-recipes");
        }
        catch(Exception ex){
            Utility.hideLoader(currentActivity);
            Utility.showMessage(this.getActivity(),  getString(R.string.defaultError), getString(R.string.alert));
        }

    }
    public void initializeDataType(long checkedId){

        mMySharedPreference=new MySharedPreference(currentActivity);
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        if(R.id.btn1==checkedId){
            if(!((DashboardActivity)getActivity()).checkIfNet()){
                if(appSharedPrefs.contains("HomeAllRecipeData")){

                    populateAllRecipe("HomeAllRecipeData");
                    data.setVisibility(VISIBLE);
                    noData.setVisibility(GONE);
                } else{
                    data.setVisibility(GONE);
                    noData.setVisibility(VISIBLE);
                }
            } else {
                data.setVisibility(VISIBLE);
                noData.setVisibility(GONE);
                if (appSharedPrefs.contains("HomeAllRecipeData")) {

                    populateAllRecipe("HomeAllRecipeData");
                    Utility.showLoader(this.getActivity());
                } else {
                    Utility.showLoader(this.getActivity());
                }
                DataCenter.HomeTypeRecipe(this.getActivity(), "home-all-recipes");
            }
            String rad=Constants.radio.ALL.getValue();
            mMySharedPreference.setCurrentRadio(rad);
        }else if(R.id.btn2==checkedId){
            if(!((DashboardActivity)getActivity()).checkIfNet()) {
                if(appSharedPrefs.contains("HomeRecentRecipeData")){
                    populateAllRecipe("HomeRecentRecipeData");
                    data.setVisibility(VISIBLE);
                    noData.setVisibility(GONE);
                }else{
                    data.setVisibility(GONE);
                    noData.setVisibility(VISIBLE);
                }
            } else {
                data.setVisibility(VISIBLE);
                noData.setVisibility(GONE);
                if (appSharedPrefs.contains("HomeRecentRecipeData")) {
                    populateAllRecipe("HomeRecentRecipeData");
                    Utility.showLoader(this.getActivity());
                } else {
                    Utility.showLoader(this.getActivity());
                }
                DataCenter.HomeTypeRecipe(this.getActivity(), "home-recent-recipes");
            }
            String rad=Constants.radio.RECENT.getValue();
            mMySharedPreference.setCurrentRadio(rad);
        } else if(R.id.btn3==checkedId){
            if(!((DashboardActivity)getActivity()).checkIfNet()) {
                if (appSharedPrefs.contains("HomeFavRecipeData")) {
                    populateAllRecipe("HomeFavRecipeData");
                    data.setVisibility(VISIBLE);
                    noData.setVisibility(GONE);
                } else {
                    data.setVisibility(GONE);
                    noData.setVisibility(VISIBLE);
                }
            } else {
                data.setVisibility(VISIBLE);
                noData.setVisibility(GONE);
                if (appSharedPrefs.contains("HomeFavRecipeData")) {
                    populateAllRecipe("HomeFavRecipeData");
                    Utility.showLoader(this.getActivity());
                } else {
                    Utility.showLoader(this.getActivity());
                }
                DataCenter.HomeTypeRecipe(this.getActivity(), "home-fav-recipes");
            }
            String rad=Constants.radio.FAV.getValue();
            mMySharedPreference.setCurrentRadio(rad);
        } else if(R.id.btn4==checkedId){
            if(!((DashboardActivity)getActivity()).checkIfNet()) {
                if (appSharedPrefs.contains("HomeViewRecipeData")) {
                    populateAllRecipe("HomeViewRecipeData");
                    data.setVisibility(VISIBLE);
                    noData.setVisibility(GONE);
                } else {
                    data.setVisibility(GONE);
                    noData.setVisibility(VISIBLE);
                }
            }else {
                data.setVisibility(VISIBLE);
                noData.setVisibility(GONE);
                if (appSharedPrefs.contains("HomeViewRecipeData")) {
                    populateAllRecipe("HomeViewRecipeData");
                    Utility.showLoader(this.getActivity());
                } else {
                    Utility.showLoader(this.getActivity());
                }
                DataCenter.HomeTypeRecipe(this.getActivity(), "home-view-recipes");
            }
            String rad=Constants.radio.VIEW.getValue();
            mMySharedPreference.setCurrentRadio(rad);
        } else {
            if(!((DashboardActivity)getActivity()).checkIfNet()) {
                if (appSharedPrefs.contains("HomeAllRecipeData")) {
                    populateAllRecipe("HomeAllRecipeData");
                    data.setVisibility(VISIBLE);
                    noData.setVisibility(GONE);
                } else {
                    data.setVisibility(GONE);
                    noData.setVisibility(VISIBLE);
                }
            } else {
                data.setVisibility(VISIBLE);
                noData.setVisibility(GONE);
                if (appSharedPrefs.contains("HomeAllRecipeData")) {
                    populateAllRecipe("HomeAllRecipeData");
                    Utility.showLoader(this.getActivity());
                } else {
                    Utility.showLoader(this.getActivity());
                }
                DataCenter.HomeTypeRecipe(this.getActivity(), "home-all-recipes");
            }
            String rad=Constants.radio.ALL.getValue();
            mMySharedPreference.setCurrentRadio(rad);
        }
    }

    public void initlializeDataCateg(){
        try {

            categories = mMySharedPreference.getCategories();
            ArrayList<CategoryModel> obj=new ArrayList<>();
            int count=0;
            for(int k=0;k<categories.size() && count <9;k++){
                CategoryModel mod=new CategoryModel();
                mod.setTotalRecipes(categories.get(k).getTotalRecipes());
                mod.setParentId(categories.get(k).getParentId());
                mod.setCategory(categories.get(k).getCategory());
                mod.setSubCategory(categories.get(k).getSubCategory());
                obj.add(mod);
                count++;
//                if(categories.get(k).getCategory().length()<12){
//                    mod.setTotalRecipes(categories.get(k).getTotalRecipes());
//                    mod.setParentId(categories.get(k).getParentId());
//                    mod.setCategory(categories.get(k).getCategory());
//                    mod.setSubCategory(categories.get(k).getSubCategory());
//                    obj.add(mod);
//                    count++;
//                }
            }
            mgrid= (MyGridView) getView().findViewById(R.id.grid); //amber
            mGridAdapter = new CategGridAdapter(this.getContext(),(ArrayList<CategoryModel>) obj);
            mgrid.setAdapter(mGridAdapter);
            // mgrid.setOnScrollListener(this);
            // Pager++;

            mgrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long itemId) {
                    try {
                        CategoryModel categ = categories.get(position);
                        if (categ.getSubCategory() != null) {
                            Intent intent = new Intent(adapterView.getContext(), SubCategoriesActivity.class);
                            intent.putExtra("id", (int) itemId);
                            intent.putExtra("name", categ.getCategory());
                            adapterView.getContext().startActivity(intent);
                        } else {
                            Intent intent = new Intent(adapterView.getContext(), CategoryRecipeActivity.class);
                            intent.putExtra("id", (int) itemId);
                            intent.putExtra("name", categ.getCategory());
                            startActivity(intent);
                        }
                    } catch (Exception ex){

                    }
                }
            });
        }
        catch(Exception ex){
            Utility.hideLoader(currentActivity);
DataCenter.GetCategories(this.getActivity(),"get-categories");
            //FirebaseCrash.log(ex.getMessage());
        }
    }

    public void openDialog1(View view) {
        /*Intent i = new Intent(currentActivity, FilterActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        currentActivity.startActivity(i);*/
        LinearLayout llSearch=(LinearLayout)getView().findViewById(R.id.llSearch) ;
        FilterActivity popup = new FilterActivity(currentActivity,searchText.getText().toString());

        popup.setWidth(llSearch.getWidth());
        popup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        final int vertPos;
        vertPos = RelativePopupWindow.VerticalPosition.BELOW;
        final int horizPos;
        horizPos = RelativePopupWindow.HorizontalPosition.ALIGN_RIGHT;
        popup.showOnAnchor(llSearch, vertPos, horizPos, true);
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
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mMessageReceiver, new IntentFilter("HomeAllRecipe"));
        getActivity().registerReceiver(mMessageReceiver, new IntentFilter("HomeRecentRecipe"));
        getActivity().registerReceiver(mMessageReceiver, new IntentFilter("HomeFavRecipe"));
        getActivity().registerReceiver(mMessageReceiver, new IntentFilter("HomeViewRecipe"));
        getActivity().registerReceiver(mMessageReceiver, new IntentFilter("HomeTrendingRecipe"));
        getActivity().registerReceiver(mMessageReceiver, new IntentFilter("HomeMayLikeRecipe"));
        getActivity().registerReceiver(mMessageReceiver, new IntentFilter("GetAllCategory"));
        getActivity().registerReceiver(mMessageReceiver, new IntentFilter("SearchRecipe"));


    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mMessageReceiver);
    }
    public void populateAllRecipe(String term){
try{
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        Gson gson = new Gson();
        String json = "";
        if (appSharedPrefs.contains(term)){
            json = appSharedPrefs.getString(term, "");
            TypeToken<List<RecipeModel>> token = new TypeToken<List<RecipeModel>>() {};
            final List<RecipeModel> responce = gson.fromJson(json, token.getType());
            //List<RecipeModel> responce = gson.fromJson(json, RecipeModel.class);
            if(responce!=null){


                ArrayList<RecipeModel> responce1=new ArrayList<>();
                if(responce!=null){
                for(int i=1;i<5;i++){
                    responce1.add(responce.get(i));
                }}

                mgridRecipe=(MyGridView)getView().findViewById(R.id.gridRecipe);
//                mAdapter = new HLVAdapter(this.getActivity(),(ArrayList<RecipeModel>) responce1);
//                mRecyclerView.setAdapter(mAdapter);
//                mRecyclerView.setHasFixedSize(false);

                mGridAdapterRecipe=new GridAdapter(this.getActivity(),(ArrayList<RecipeModel>) responce1,mImageFetcher);
                mgridRecipe.setAdapter(mGridAdapterRecipe);

                mgridRecipe.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long itemId) {
                        try {
                            Intent intent = new Intent(adapterView.getContext(), ItemDetailActivity.class);
                            intent.putExtra("id", (int) itemId);
                            adapterView.getContext().startActivity(intent);
                        } catch(Exception ex){

                        }
//                    Toast.makeText(getApplicationContext(), "You clicked on position : " + i + " and id : " + l, Toast.LENGTH_LONG).show();
                    }
                });

                mimg_recipe= (ImageView) getView().findViewById(R.id.img_recipe);

                //image caching

//                OkHttpClient okHttpClient = new OkHttpClient();
//                okHttpClient.networkInterceptors().add(new Interceptor() {
//                    @Override
//                    public Response intercept(Chain chain) throws IOException {
//                        Response originalResponse = chain.proceed(chain.request());
//                        return originalResponse.newBuilder().header("Cache-Control", "max-age=" + (60 * 60 * 24 * 365)).build();
//                    }
//                });
//
//                okHttpClient.setCache(new Cache(currentActivity.getCacheDir(), Integer.MAX_VALUE));
//                OkHttpDownloader okHttpDownloader = new OkHttpDownloader(okHttpClient);
//                Picasso picasso = new Picasso.Builder(this.getContext()).downloader(okHttpDownloader).build();
//                picasso.load(imageURL).into(mimg_recipe);

                //end

//                Global.picassoWithCache.with(this.getContext())
//                        .load(responce.get(0).getImage()).transform(new CircleTransform()).into(mimg_recipe);

//                mImageFetcher.loadImage(responce.get(0).getImage(),mimg_recipe);
//                Bitmap bmp= new CircleTransform().transform(((BitmapDrawable)mimg_recipe.getDrawable()).getBitmap());
//                mimg_recipe.setImageBitmap(bmp);
//                ImageView temp=new ImageView(getContext());
//                mImageFetcher.loadDashboardImage(responce.get(0).getImage(),temp);
//
//                Bitmap bitmap = ((BitmapDrawable)temp.getDrawable()).getBitmap();
//                mimg_recipe.setImageBitmap(new CircleTransform().transform(bitmap));
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




//                try {
//                    ImageDownloader imgLoader2 = new ImageDownloader(currentActivity);
//
//                    imgLoader2.DownloadImage(responce.get(0).getImage());
//                    ImageLoader imgLoader1 = new ImageLoader(currentActivity);
//                    imgLoader1.DisplayImage(responce.get(0).getImage(), mimg_recipe);
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

//                imgLoader.displayImage(responce.get(0).getImage(), mimg_recipe);

                mtv_title=(TextView) getView().findViewById(R.id.tv_title);

                mimg_recipe.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(currentActivity, ItemDetailActivity.class);
                        intent.putExtra("id",responce.get(0).getID());
                        currentActivity.startActivity(intent);
                    }
                });
               // mtv_title.setText(responce.get(0).getTitle().replace("&","and").replace("#308;",""));
                mtv_title.setText(Html.fromHtml(responce.get(0).getTitle()));
                mtv_content=(TextView) getView().findViewById(R.id.tv_content);
//                mtv_title.setTypeface(mtv_title.getTypeface(),BOLD);
//                mtv_content.setTypeface(mtv_content.getTypeface(),BOLD);

               // mtv_content.setText(responce.get(0).getContent().replace("&","and").replace("#308;","").replace("amp;",""));
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

                TextView tvViews=getView().findViewById(R.id.tvViews);
                tvViews.setText(responce.get(0).getTotalViews()+" Views");
//                tvViews.setTypeface(tvViews.getTypeface(),BOLD);
//                Intent i = new Intent(this.getActivity(), DashboardActivity.class);
//                Utility.StartActivity(this.getActivity(), i);
                Utility.hideLoader(this.getActivity());
            }
            else {
                Utility.hideLoader(this.getActivity());
                Utility.showMessage(this.getActivity(),  getString(R.string.defaultError),"Info");
            }
        }}
        catch(Exception ex){
            Utility.hideLoader(currentActivity);
    Log.e("FF PopulateAllRecipe:", ex.getMessage());
            //FirebaseCrash.log(ex.getMessage());
        }
    }

    public void populateTrendingRecipe(){
        try {
            SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
            Gson gson = new Gson();
            String json = "";
            if (appSharedPrefs.contains("HomeTrendingRecipeData")) {
                json = appSharedPrefs.getString("HomeTrendingRecipeData", "");
                TypeToken<List<RecipeModel>> token = new TypeToken<List<RecipeModel>>() {
                };
                final List<RecipeModel> responce = gson.fromJson(json, token.getType());
                //List<RecipeModel> responce = gson.fromJson(json, RecipeModel.class);
                if (responce != null) {

                    // Calling the RecyclerView
                    mRecyclerViewTrending = (RecyclerView) getView().findViewById(R.id.recycler_view_trending);
                    mRecyclerViewTrending.setHasFixedSize(true);
                    mRecyclerViewTrending.addItemDecoration(new EqualSpacingItemDecoration(20));

                    // The number of Columns
                    mLayoutManagerTrending = new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false);
                    mRecyclerViewTrending.setLayoutManager(mLayoutManagerTrending);

                    mAdapterTrending = new HLVAdapter(this.getActivity(), (ArrayList<RecipeModel>) responce, 0, ((Global) currentActivity.getApplication()).getImageFetcher(currentActivity));
                    mRecyclerViewTrending.setAdapter(mAdapterTrending);

                } else {
                    Utility.hideLoader(this.getActivity());
                    Utility.showMessage(this.getActivity(), getString(R.string.defaultError), "Info");
                }
            }
        }
        catch(Exception ex){
            Utility.hideLoader(currentActivity);
            Log.e("FFPopulateTrending:",ex.getMessage());
            //FirebaseCrash.log(ex.getMessage());
        }
    }
    public void populateMayLikeRecipe() {
        try{
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        Gson gson = new Gson();
        String json = "";
        if (appSharedPrefs.contains("HomeMayLikeRecipeData")) {
            json = appSharedPrefs.getString("HomeMayLikeRecipeData", "");
            TypeToken<List<RecipeModel>> token = new TypeToken<List<RecipeModel>>() {
            };
            final List<RecipeModel> responce = gson.fromJson(json, token.getType());
            //List<RecipeModel> responce = gson.fromJson(json, RecipeModel.class);
            if (responce != null) {

                // Calling the RecyclerView
                mRecyclerViewMayLike = (RecyclerView) getView().findViewById(R.id.recycler_view_may_like);
                mRecyclerViewMayLike.addItemDecoration(new EqualSpacingItemDecoration(20));
                mRecyclerViewMayLike.setHasFixedSize(true);

                // The number of Columns
                mLayoutManagerMayLike = new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false);
                mRecyclerViewMayLike.setLayoutManager(mLayoutManagerMayLike);

                mAdapterMayLike = new HLVAdapter(this.getActivity(), (ArrayList<RecipeModel>) responce, 0, ((Global) currentActivity.getApplication()).getImageFetcher(currentActivity));
                mRecyclerViewMayLike.setAdapter(mAdapterMayLike);


            } else {
                Utility.hideLoader(this.getActivity());
                Utility.showMessage(this.getActivity(), getString(R.string.defaultError), "Info");
            }
        }}
        catch(Exception ex){
            Utility.hideLoader(currentActivity);
            Log.e("FFMayLike: ",ex.getMessage());
            //FirebaseCrash.log(ex.getMessage());
        }
    }
    public void populateSearch(){
        try {
            SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
            Gson gson = new Gson();
            String json = "";
            if (appSharedPrefs.contains("SearchRecipeData")) {
                json = appSharedPrefs.getString("SearchRecipeData", "");
                TypeToken<List<RecipeModel>> token = new TypeToken<List<RecipeModel>>() {
                };
                final List<RecipeModel> responce = gson.fromJson(json, token.getType());
                //List<RecipeModel> responce = gson.fromJson(json, RecipeModel.class);
                if (responce != null) {
                    ArrayList<String> recipes1 = new ArrayList<String>();
                    adapter.clear();
                    for (int i = 0; i < responce.size(); i++) {
                        adapter.add(Html.fromHtml(responce.get(i).getTitle()).toString());
                    }

                    adapter.notifyDataSetChanged();
                    //adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, deviceList);


                } else {
                    Utility.showMessage(this.getActivity(), getString(R.string.defaultError), "Info");
                }
            }
        }
        catch(Exception ex){
            Utility.hideLoader(currentActivity);
            Log.e("FFSearch:",ex.getMessage());
            //FirebaseCrash.log(ex.getMessage());
        }
    }
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            total_load++;
            if(intent.getAction().equals("HomeAllRecipe")){
                populateAllRecipe("HomeAllRecipeData");
            }
            if(intent.getAction().equals("HomeRecentRecipe")){
                populateAllRecipe("HomeRecentRecipeData");
            }
            if(intent.getAction().equals("HomeFavRecipe")){
                populateAllRecipe("HomeFavRecipeData");
            }
            if(intent.getAction().equals("HomeViewRecipe")){
                populateAllRecipe("HomeViewRecipeData");
            }
            if(intent.getAction().equals("HomeTrendingRecipe")){
                populateTrendingRecipe();
            }
            if(intent.getAction().equals("HomeMayLikeRecipe")){
                populateMayLikeRecipe();
            }
            if(intent.getAction().equals("GetAllCategory")){
                initlializeDataCateg();
            }
            if(intent.getAction().equals("SearchRecipe")){
                populateSearch();
            }

            if(total_load >= numberOfloader){
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
                            Toast.makeText(currentActivity, R.string.FavouriteMakeError, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(currentActivity, R.string.FavouriteMakeError, Toast.LENGTH_LONG).show();
                    }
                }
                catch(Exception ex){
                    Utility.hideLoader(currentActivity);
                    Toast.makeText(currentActivity, R.string.FavouriteMakeError, Toast.LENGTH_LONG).show();
                    //FirebaseCrash.log(ex.getMessage());
                }
            }
           // refreshScreen();
        }
    };

//
public void likeClickHandler(View v){
    if(mMySharedPreference.isUserLogin()) {


        RecipeModel item = (RecipeModel) v.getTag(R.id.recipeModelId);
        int drawable = (int) v.getTag(R.id.likeImageId);
        if (drawable == R.drawable.like_detail_icon) {
            ImageView imgLike = v.findViewById(R.id.iv_like);
            imgLike.setImageResource(R.drawable.liked_detail_icon);
            imgLike.setTag(R.id.likeImageId, R.drawable.liked_detail_icon);
            DataCenter.AddDeleteFavourite(currentActivity, "fav_recipe", item.getID());
        } else {
            ImageView imgLike = v.findViewById(R.id.iv_like);
            imgLike.setImageResource(R.drawable.like_detail_icon);
            imgLike.setTag(R.id.likeImageId, R.drawable.like_detail_icon);
            DataCenter.AddDeleteFavourite(currentActivity, "delete_fav_recipe", item.getID());
        }
    }
}
}
