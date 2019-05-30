package com.foodfusion.foodfusion;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.foodfusion.foodfusion.Adapter.HLVAdapter;
import com.foodfusion.foodfusion.Custom.EqualSpacingItemDecoration;
import com.foodfusion.foodfusion.Custom.NetworkChangeReceiver;
import com.foodfusion.foodfusion.DB.MySharedPreference;
import com.foodfusion.foodfusion.Model.RecipeDetailModel;
import com.foodfusion.foodfusion.Model.RecipeModel;
import com.foodfusion.foodfusion.Util.Constants;
import com.foodfusion.foodfusion.Util.DataCenter;
import com.foodfusion.foodfusion.Util.Utility;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.foodfusion.foodfusion.Util.Constants.SEARCH_QUERY_THRESHOLD;

public class ItemDetailActivity extends AppCompatActivity  {

    Activity currentActivity;
    private static final int RECOVERY_DIALOG_REQUEST = 1;

    public static final String KEY_VIDEO_ID = "KEY_VIDEO_ID";
    private final YouTubeBaseActivity mYouTubeBaseActivity=new YouTubeBaseActivity();

    private String mVideoId;
    private static TextView mtv_title;
    private static TextView mtv_content;
    private static TextView mtv_detail_eng,mtv_detail_eng2;
    private static TextView mtv_detail_urdu,mtv_detail_urdu2;

    private static ImageView mimg_back;
    private static ImageView mlike_icon_menu;
    private static View mlike_icon_menu1;

    TextView mmayLike_more;

    RecyclerView mRecyclerViewMayLike;
    RecyclerView.LayoutManager mLayoutManagerMayLike;
    RecyclerView.Adapter mAdapterMayLike;

    private MySharedPreference mMySharedPreference;

    private int id;

    RadioGroup mrdogrp;
    int selectedId;

    boolean fullScreen;
    LinearLayout mEnglish,mUrdu, mEnglish2,mUrdu2;


    //search

    SearchView searchView;

    NetworkChangeReceiver mReceiver;
    LinearLayout network;
    LinearLayout data,noData;
    //
//    private static YouTube youtube;
//    private static int counter = 0;
//
//    GoogleAccountCredential mCredential;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_item_detail);

            currentActivity = this;
            network = (LinearLayout) findViewById(R.id.network);
            data = (LinearLayout) findViewById(R.id.data);
            noData = (LinearLayout) findViewById(R.id.noData);
            mtv_title = (TextView) findViewById(R.id.tv_title);
            mMySharedPreference = new MySharedPreference(this);
            id = getIntent().getExtras().getInt("id");
            if (!(id < 0)) {
                callApi();
            } else {
                Toast.makeText(this, R.string.no_detail_recipe, Toast.LENGTH_SHORT).show();
            }

            mimg_back = (ImageView) findViewById(R.id.img_back);
            mimg_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
try{
                    finish();
}
catch(Exception ex){

}
                }
            });
//        final Bundle arguments = getIntent().getExtras();
//        if (arguments != null && arguments.containsKey(KEY_VIDEO_ID)) {
//            mVideoId = arguments.getString(KEY_VIDEO_ID);
//        }
            mlike_icon_menu = (ImageView) findViewById(R.id.like_icon_menu);
            mlike_icon_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                    if (checkIfNet()) {
                        if (mMySharedPreference.isUserLogin()) {


                            RecipeDetailModel item = (RecipeDetailModel) v.getTag(R.id.recipeModelId);
                            int drawable = (int) v.getTag(R.id.likeImageId);
                            if (drawable == R.drawable.like_icon_menu) {
                                //ImageView imgLike=v.findViewById(R.id.iv_like);
                                mlike_icon_menu.setImageResource(R.drawable.liked_icon_menu_red);
                                mlike_icon_menu.setTag(R.id.likeImageId, R.drawable.liked_icon_menu_red);
                                DataCenter.AddDeleteFavourite(currentActivity, "fav_recipe", item.getID());
                            } else {
                                //ImageView imgLike=v.findViewById(R.id.iv_like);
                                mlike_icon_menu.setImageResource(R.drawable.like_icon_menu);
                                mlike_icon_menu.setTag(R.id.likeImageId, R.drawable.like_icon_menu);
                                DataCenter.AddDeleteFavourite(currentActivity, "delete_fav_recipe", item.getID());
                            }

                            //
                            DataCenter.HomeTypeRecipe(currentActivity, "home-trending-recipes");
                            DataCenter.HomeTypeRecipe(currentActivity, "home-more-recipes");
                            if (mMySharedPreference.getCurrentRadio() == Constants.radio.ALL.getValue())
                                DataCenter.HomeTypeRecipe(currentActivity, "home-all-recipes");
                            if (mMySharedPreference.getCurrentRadio() == Constants.radio.RECENT.getValue())
                                DataCenter.HomeTypeRecipe(currentActivity, "home-recent-recipes");
                            if (mMySharedPreference.getCurrentRadio() == Constants.radio.FAV.getValue())
                                DataCenter.HomeTypeRecipe(currentActivity, "home-fav-recipes");
                            if (mMySharedPreference.getCurrentRadio() == Constants.radio.VIEW.getValue())
                                DataCenter.HomeTypeRecipe(currentActivity, "home-view-recipes");
                        } else {
                            Utility.showLoginLikeMessage(currentActivity);
                        }
                    } else {
                        Toast.makeText(currentActivity, R.string.stillNoConnection, Toast.LENGTH_SHORT).show();
                    }
                    }
                    catch(Exception ex){

                    }
                }
            });
            ScrollView rootLayout = (ScrollView) findViewById(R.id.sv1);
            Utility.overrideFonts(currentActivity, rootLayout);

            mEnglish = (LinearLayout) findViewById(R.id.englishTab);
            mUrdu = (LinearLayout) findViewById(R.id.urduTab);
            mEnglish2 = (LinearLayout) findViewById(R.id.englishTab2);
            mUrdu2 = (LinearLayout) findViewById(R.id.urduTab2);
            mrdogrp = (RadioGroup) findViewById(R.id.rdogrp);
            selectedId = mrdogrp.getCheckedRadioButtonId();
            if (R.id.btn1 == selectedId) { //English selected
                mEnglish.setVisibility(VISIBLE);
                mUrdu.setVisibility(GONE);
                mEnglish2.setVisibility(VISIBLE);
                mUrdu2.setVisibility(GONE);
            } else {
                mEnglish.setVisibility(GONE);
                mUrdu.setVisibility(VISIBLE);
                mEnglish2.setVisibility(GONE);
                mUrdu2.setVisibility(VISIBLE);
            }
            mrdogrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    // checkedId is the RadioButton selected
                    if (R.id.btn1 == checkedId) { //English selected
                        mEnglish.setVisibility(VISIBLE);
                        mUrdu.setVisibility(GONE);
                        mEnglish2.setVisibility(VISIBLE);
                        mUrdu2.setVisibility(GONE);
                    } else {
                        mEnglish.setVisibility(GONE);
                        mUrdu.setVisibility(VISIBLE);
                        mEnglish2.setVisibility(GONE);
                        mUrdu2.setVisibility(VISIBLE);
                    }
                }
            });

            mmayLike_more = (TextView) findViewById(R.id.mayLike_more);
            mmayLike_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent intent = new Intent(ItemDetailActivity.this, TrendingActivity.class);
                        intent.putExtra("name", Utility.ShowMore.MAYALSOLIKE);
                        startActivity(intent);
                    }
                    catch(Exception ex){

                    }
                }
            });
            //Search
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            checkIfNet();
        }
        catch(Exception ex){

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        try {
            getMenuInflater().inflate(R.menu.menu_item2, menu);

            searchView =
                    (SearchView) menu.findItem(R.id.action_search).getActionView();

            searchView.setQueryHint("Search...");
            searchView.setIconifiedByDefault(false);
            searchView.setMaxWidth(Integer.MAX_VALUE);
            //Here is where the magic happens:
            int options = searchView.getImeOptions();
            searchView.setImeOptions(options | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
            int magId = getResources().getIdentifier("android:id/search_mag_icon", null, null);
            ImageView magImage = (ImageView) searchView.findViewById(magId);
            magImage.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
            magImage.setVisibility(GONE);

            int searchPlateId = searchView.getContext().getResources().getIdentifier("android:id/search_plate", null, null);
            View searchPlate = searchView.findViewById(searchPlateId);
            if (searchPlate != null) {
                searchPlate.setBackgroundColor(getResources().getColor(R.color.appColor));
                int searchTextId = searchPlate.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
                TextView searchText = (TextView) searchPlate.findViewById(searchTextId);
                if (searchText != null) {
                    searchText.setTextColor(Color.WHITE);
                    searchText.setHintTextColor(Color.WHITE);
                }
            }
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
// if user presses enter, do default search, ex:
                    if (query.length() >= SEARCH_QUERY_THRESHOLD) {//SEARCH_QUERY_THRESHOLD

                        Intent intent = new Intent(currentActivity, SearchActivity.class);
                        intent.putExtra("name", query);
                        startActivity(intent);

                        searchView.getSuggestionsAdapter().changeCursor(null);
                        return true;
                    }
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String query) {

                    if (query.length() >= SEARCH_QUERY_THRESHOLD) { //SEARCH_QUERY_THRESHOLD
                        new ItemDetailActivity.SearchRuntimeGet().execute(query);
                    } else {
                        searchView.getSuggestionsAdapter().changeCursor(null);
                    }

                    return true;
                }
            });


            searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {

                @Override
                public boolean onSuggestionSelect(int position) {

                    Cursor cursor = (Cursor) searchView.getSuggestionsAdapter().getItem(position);
                    String term = cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1));
                    int id = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID));
                    searchView.setQuery(term, true);
                    cursor.close();
                    Intent intent = new Intent(currentActivity, ItemDetailActivity.class);
                    intent.putExtra("id", id);
                    currentActivity.startActivity(intent);
//            Intent intent = new Intent(MainActivity.this, SearchableActivity.class);
//            intent.setAction(Intent.ACTION_SEARCH);
//            intent.putExtra(SearchManager.QUERY, term);
//            startActivity(intent);

                    return true;
                }

                @Override
                public boolean onSuggestionClick(int position) {

                    return onSuggestionSelect(position);
                }
            });
            //searchView.setMenuItem(item);

            searchView.setSuggestionsAdapter(new SimpleCursorAdapter(
                    this, android.R.layout.simple_list_item_1, null,
                    new String[]{SearchManager.SUGGEST_COLUMN_TEXT_1},
                    new int[]{android.R.id.text1}));
        }
        catch (Exception ex){


        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id=item.getItemId();
//        if(id==R.id.action_favourite){
//           // Toast.makeText(this,"hello",Toast.LENGTH_LONG);
//            return true;
//        }
//        if(id==R.id.action_back){
//            finish();
//            // Toast.makeText(this,"hello",Toast.LENGTH_LONG);
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }
    public void openSearchView(View v){
        searchView.setVisibility(VISIBLE);

    }

    public void shareRecipe(View v){
        if(checkIfNet()) {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Aj Khanay me " + mtv_title.getText() + " Pakayen: https://www.youtube.com/watch?v=" + mVideoId);
            startActivity(Intent.createChooser(sharingIntent, "Share using"));
        }else{
            Toast.makeText(this,R.string.stillNoConnection,Toast.LENGTH_SHORT).show();
        }
    }
    protected  void callApi(){
        try{
            if(checkIfNet()) {
//            Utility.showLoader(this);
                DataCenter.DetailRecipe(this, "get_recipe_by_id", id);
            }

//            DataCenter.HomeTypeRecipe(this, "home-more-recipes");
        }
        catch(Exception ex){
            Utility.hideLoader(currentActivity);
            Utility.showMessage(this,  getString(R.string.defaultError), getString(R.string.alert));
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
        registerReceiver(mMessageReceiver, new IntentFilter("RecipeDetail"));
        this.registerReceiver(mMessageReceiver, new IntentFilter("NetworkConnection"));
        checkIfNet();
    }

    @Override
    public void onPause() {
        unregisterReceiver(mReceiver);
        super.onPause();
        unregisterReceiver(mMessageReceiver);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("HomeMayLikeRecipe")){
                populateMayLikeRecipe();
            }
            if(intent.getAction().equals("RecipeDetail")){
                try {
                    Utility.hideLoader(currentActivity);
                    populateRecipeDetail();
                    populateMayLikeRecipe();
                }
                catch(Exception ex){
                    Utility.hideLoader(currentActivity);
                    //FirebaseCrash.log(ex.getMessage());
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
    public void populateRecipeDetail() {
        try {
            SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
            Gson gson = new Gson();
            String json = "";
            if (appSharedPrefs.contains("RecipeDetailData")) {
                json = appSharedPrefs.getString("RecipeDetailData", "");
//            TypeToken<List<RecipeModel>> token = new TypeToken<List<RecipeModel>>() {};
//            List<RecipeModel> responce = gson.fromJson(json, token.getType());
                RecipeDetailModel responce = gson.fromJson(json, RecipeDetailModel.class);
                if (responce != null) {
                    SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();

                    prefsEditor.putInt("RecipeDetailDataID", id);
                    prefsEditor.commit();
//                TextView totalViews=(TextView)findViewById(R.id.totalViews);
//                totalViews.setText(responce.getTotalViews());

                    TextView totalLikes = (TextView) findViewById(R.id.totalLikes);
                    final LinearLayout llSponsor = (LinearLayout) findViewById(R.id.llSponsor);
//                final ImageView imgSponsor=(ImageView)findViewById(R.id.imgSponsor);
                    final View imgSp = (View) findViewById(R.id.imgSp);
                    if (responce.getSponsorImage() != "") {
                        llSponsor.setVisibility(VISIBLE);
//                    Global.picassoWithCache.with(this)
//                            .load(responce.getSponsorImage()).into(imgSponsor);
//                    imgSponsor.setBackgroundResource();
                        Picasso.with(this).load(responce.getSponsorImage()).into(new Target() {

                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                imgSp.setBackground(new BitmapDrawable(currentActivity.getResources(), bitmap));
                            }

                            @Override
                            public void onBitmapFailed(final Drawable errorDrawable) {
                                Log.d("TAG", "FAILED");
                            }

                            @Override
                            public void onPrepareLoad(final Drawable placeHolderDrawable) {
                                Log.d("TAG", "Prepare Load");
                            }
                        });

                    } else {
                        llSponsor.setVisibility(GONE);
                    }

                    TextView recipeBy = (TextView) findViewById(R.id.recipeBy);
                    recipeBy.setText(Html.fromHtml(responce.getRecipeBy() != null ? responce.getRecipeBy() : ""));

                    TextView recipeDate = (TextView) findViewById(R.id.recipeDate);
                    recipeDate.setText(Html.fromHtml(responce.getDate() != null ? responce.getDate() : ""));
                    totalLikes.setText(responce.getTotalLikes());

//                TextView tvDate=(TextView)findViewById(R.id.tvDate);
//                tvDate.setText(responce.getDate().substring(0, Math.min(responce.getDate().length(), 10)));

//                TextView rating=(TextView)findViewById(R.id.rating);
//                rating.setText(responce.getTotalLikes());

                    mVideoId = responce.getVideoId();
                    YouTubePlayerFragment youtubePlayerFragment = new YouTubePlayerFragment();
                    youtubePlayerFragment.initialize(getString(R.string.DEVELOPER_KEY), new YouTubePlayer.OnInitializedListener() {

                        @Override
                        public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
//Here we can set some flags on the player

                            //This flag tells the player to switch to landscape when in fullscreen, it will also return to portrait
                            //when leaving fullscreen
                            youTubePlayer.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION);

                            //This flag tells the player to automatically enter fullscreen when in landscape. Since we don't have
                            //landscape layout for this activity, this is a good way to allow the user rotate the video player.
                            //youTubePlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE);

                            //This flag controls the system UI such as the status and navigation bar, hiding and showing them
                            //alongside the player UI
                            //youTubePlayer.setShowFullscreenButton(false);

                            if (mVideoId != null) {
                                if (b) {
                                    youTubePlayer.play();
                                } else {
                                    youTubePlayer.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {

                                        @Override
                                        public void onFullscreen(boolean _isFullScreen) {
                                            fullScreen = _isFullScreen;
                                            if (fullScreen) {
                                                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                                            } else {
                                                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                                            }
                                        }

                                    });

                                    youTubePlayer.loadVideo(mVideoId);
                                    youTubePlayer.cueVideo(mVideoId);
                                }
                            }
                        }

                        @Override
                        public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                            if (youTubeInitializationResult.isUserRecoverableError()) {
                                youTubeInitializationResult.getErrorDialog(currentActivity, RECOVERY_DIALOG_REQUEST).show();
                            } else {
                                //Handle the failure
                                Toast.makeText(currentActivity, R.string.error_init_failure, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.video_container, youtubePlayerFragment);
                    fragmentTransaction.commit(); //Amber



//                mtv_title.setText(responce.getTitle().replace("&","and").replace("#038;",""));
                    mtv_title.setText(Html.fromHtml(responce.getTitle()));
                    mtv_content = (TextView) findViewById(R.id.tv_content);
//                mtv_content.setText(responce.getContent().replace("&","and").replace("#038;","").replace("amp;",""));
                    mtv_content.setText(Html.fromHtml(responce.getContent()));
                    mlike_icon_menu.setTag(R.id.recipeModelId, responce);
                    if (responce.getIsFavourite() == 1) {
                        mlike_icon_menu.setImageResource(R.drawable.liked_icon_menu_red);
                        mlike_icon_menu.setTag(R.id.likeImageId, R.drawable.liked_icon_menu_red);
                    } else {
                        mlike_icon_menu.setImageResource(R.drawable.like_icon_menu);
                        mlike_icon_menu.setTag(R.id.likeImageId, R.drawable.like_icon_menu);
                    }

                    mtv_detail_eng = (TextView) findViewById(R.id.tv_detail_eng);
                    mtv_detail_eng2 = (TextView) findViewById(R.id.tv_detail_eng2);


                    mtv_detail_urdu = (TextView) findViewById(R.id.tv_detail_urdu);
                    mtv_detail_urdu2 = (TextView) findViewById(R.id.tv_detail_urdu2);

                    if (!responce.getFullRecipeDetailEnglish().contains("[heading title=\"Ingredients:\"]")
                            || !responce.getFullRecipeDetailEnglish().contains("[heading title=\"Directions:\"]")) {
                        mtv_detail_eng.setText(Html.fromHtml(responce.getFullRecipeDetailEnglish().replace("\n", "<br>")));
                        mtv_detail_eng2.setText(Html.fromHtml(responce.getFullRecipeDetailEnglish().replace("\n", "<br>")));
                    } else {


                        Spanned spanned = Html.fromHtml(
                                "<img src='" + getResources().getDrawable(R.drawable.bullet_icon) + "'/>", imageGetter, null);
                        String replacedStringEng = responce.getFullRecipeDetailEnglish().replace("&amp", "and");
                        replacedStringEng = replacedStringEng.replace("\n\n", "\n");
                        replacedStringEng = replacedStringEng.replace("[heading title=\"Ingredients:\"]", "<h2>Ingredients Required</h2>");
                        replacedStringEng = replacedStringEng.replace("[heading title=\"Directions:\"]", "Directions");
                        replacedStringEng = replacedStringEng.replace("\n", "\n\n \u25BA ");
                        replacedStringEng = replacedStringEng.replace(" \u25BA Directions", "\n<h2>Directions</h2>");

                        int ind = replacedStringEng.lastIndexOf("\u25BA");
                        if (ind >= 0)
                            replacedStringEng = new StringBuilder(replacedStringEng).replace(ind, ind + 1, " ").toString();
                        replacedStringEng = replacedStringEng.replace("\n", "<br>");
                        replacedStringEng = replacedStringEng.replace("</h2><br><br>", "</h2>");
                        String[] arrEng = replacedStringEng.split("<h2>Directions</h2>");
                        arrEng[1] = "<h2>Directions</h2>" + arrEng[1];
                        arrEng[0] = arrEng[0].replace(" \u25BA", "<img src='" + getResources().getDrawable(R.drawable.bullet_icon) + "'/>&nbsp; &nbsp;");
                        arrEng[1] = arrEng[1].replace(" \u25BA", "");
                        Spanned spanned1 = Html.fromHtml(
                                arrEng[0], imageGetter, null);
                        Spanned spanned12 = Html.fromHtml(
                                arrEng[1], imageGetter, null);

//                mtv_detail_eng.setText(spanned+ " " +replacedStringEng);
                        mtv_detail_eng.setText(spanned1);
                        mtv_detail_eng2.setText(spanned12);

                    }

                    if (!responce.getFullRecipeDetailRoman().contains("[heading title=\"Ajza:\"]")
                            || !responce.getFullRecipeDetailRoman().contains("[heading title=\"Directions:\"]")) {
                        mtv_detail_urdu.setText(Html.fromHtml(responce.getFullRecipeDetailRoman().replace("\n", "<br>")));
                        mtv_detail_urdu2.setText(Html.fromHtml(responce.getFullRecipeDetailRoman().replace("\n", "<br>")));
                    } else {
                        Spanned spanned2 = Html.fromHtml(
                                "<img src='" + getResources().getDrawable(R.drawable.bullet_icon) + "'/>", imageGetter, null);
                        String replacedStringUrdu = responce.getFullRecipeDetailRoman().replace("&amp", "aur");
                        replacedStringUrdu = replacedStringUrdu.replace("\n\n", "\n");
                        replacedStringUrdu = replacedStringUrdu.replace("[heading title=\"Ajza:\"]", "<h2>Zarori Ajza</h2>");
                        replacedStringUrdu = replacedStringUrdu.replace("[heading title=\"Directions:\"]", "Directions");
                        replacedStringUrdu = replacedStringUrdu.replace("\n", "\n\n \u25BA ");
                        replacedStringUrdu = replacedStringUrdu.replace(" \u25BA Directions", "\n<h2>Directions</h2>");

                        int ind1 = replacedStringUrdu.lastIndexOf("\u25BA");
                        if (ind1 >= 0)
                            replacedStringUrdu = new StringBuilder(replacedStringUrdu).replace(ind1, ind1 + 1, " ").toString();
                        replacedStringUrdu = replacedStringUrdu.replace("\n", "<br>");
                        replacedStringUrdu = replacedStringUrdu.replace("</h2><br><br>", "</h2>");
                        String[] arrUrdu = replacedStringUrdu.split("<h2>Directions</h2>");
                        arrUrdu[1] = "<h2>Directions</h2>" + arrUrdu[1];
                        arrUrdu[0] = arrUrdu[0].replace(" \u25BA", "<img src='" + getResources().getDrawable(R.drawable.bullet_icon) + "'/>&nbsp; &nbsp;");
                        arrUrdu[1] = arrUrdu[1].replace(" \u25BA", "");
                        Spanned spanned3 = Html.fromHtml(
                                arrUrdu[0], imageGetter, null);
                        Spanned spanned32 = Html.fromHtml(
                                arrUrdu[1], imageGetter, null);
                        mtv_detail_urdu.setText(spanned3);
                        mtv_detail_urdu2.setText(spanned32);
                    }

//                BannerSlider bannerSlider = (BannerSlider) findViewById(R.id.banner_slider1);
//                List<Banner> banners=new ArrayList<>();
//                //add banner using image url
//                banners.add(new RemoteBanner(responce.getImages().getThumbnail0()));
//                banners.add(new RemoteBanner(responce.getImages().getThumbnail1()));
//                banners.add(new RemoteBanner(responce.getImages().getThumbnail2()));
//                banners.add(new RemoteBanner(responce.getImages().getThumbnail3()));
//
//                //add banner using resource drawable
//                //banners.add(new DrawableBanner(R.drawable.yourDrawable));
//
//                    bannerSlider.setBanners(banners);
                    // Initialize credentials and service object.
//                String[] SCOPES = { YouTubeScopes.YOUTUBE_FORCE_SSL };
//                mCredential = GoogleAccountCredential.usingOAuth2(
//                        getApplicationContext(), Arrays.asList(SCOPES))
//                        .setBackOff(new ExponentialBackOff());
//
//                try {
//                    Auth auth=new Auth(this);
//                    List<String> scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube.force-ssl");
////                    Credential credential = auth.authorize(scopes, "commentthreads");
//                    youtube = new YouTube.Builder(auth.HTTP_TRANSPORT, auth.JSON_FACTORY, mCredential).build();
//
//                    String videoId = "3Ft1touyPew";
//                    // Get video comments threads
//                    CommentThreadListResponse commentsPage = prepareListRequest(videoId).execute();
//
//                    while (true) {
//                        handleCommentsThreads(commentsPage.getItems());
//
//                        String nextPageToken = commentsPage.getNextPageToken();
//                        if (nextPageToken == null)
//                            break;
//
//                        // Get next page of video comments threads
//                        commentsPage = prepareListRequest(videoId).setPageToken(nextPageToken).execute();
//                    }
//                }
//                catch (IOException ex){
//String rc=ex.toString();
//                }
//                YouTube youtube = getYouTubeService();
//                try {
//                    HashMap<String, String> parameters = new HashMap<>();
//                    parameters.put("part", "snippet,replies");
//                    parameters.put("videoId", "m4Jtj2lCMAA");
//
//                    YouTube.CommentThreads.List commentThreadsListByVideoIdRequest = youtube.commentThreads().list(parameters.get("part").toString());
//                    if (parameters.containsKey("videoId") && parameters.get("videoId") != "") {
//                        commentThreadsListByVideoIdRequest.setVideoId(parameters.get("videoId").toString());
//                    }
//
//                    CommentThreadListResponse response = commentThreadsListByVideoIdRequest.execute();
//                    System.out.println(response);
//                } catch(Exception ex) {
//
//                }

                } else {
                    Utility.hideLoader(this);
                    Utility.showMessage(this, getString(R.string.defaultError), "Info");
                }
            }
        }
        catch (Exception ex){
            Utility.hideLoader(currentActivity);
            //FirebaseCrash.log(ex.getMessage());
        }
    }
    public void populateMayLikeRecipe() {
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
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
                mRecyclerViewMayLike = (RecyclerView) findViewById(R.id.recycler_view_may_like);
                mRecyclerViewMayLike.addItemDecoration(new EqualSpacingItemDecoration(20));
                mRecyclerViewMayLike.setHasFixedSize(true);

                // The number of Columns
                mLayoutManagerMayLike = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                mRecyclerViewMayLike.setLayoutManager(mLayoutManagerMayLike);

                mAdapterMayLike = new HLVAdapter(this, (ArrayList<RecipeModel>) responce,0,((Global) currentActivity.getApplication()).getImageFetcher(currentActivity));
                mRecyclerViewMayLike.setAdapter(mAdapterMayLike);




            } else {
                Utility.hideLoader(this);
                Utility.showMessage(this, getString(R.string.defaultError), "Info");
            }
        }
    }


//    private static YouTube.CommentThreads.List prepareListRequest(String videoId) {
//try {
//    return youtube.commentThreads()
//            .list("snippet,replies")
//            .setVideoId(videoId)
//            .setMaxResults(100L)
//            .setModerationStatus("published")
//            .setTextFormat("plainText");
//}
//catch(Exception ex){
//    return null;
//}
//    }
//    private static void handleCommentsThreads(List<CommentThread> commentThreads) {
//
//        for (CommentThread commentThread : commentThreads) {
//            List<Comment> comments = Lists.newArrayList();
//            comments.add(commentThread.getSnippet().getTopLevelComment());
//
//            CommentThreadReplies replies = commentThread.getReplies();
//            if (replies != null)
//                comments.addAll(replies.getComments());
//
//            System.out.println("Found " + comments.size() + " comments.");
//
//            // Do your comments logic here
//            counter += comments.size();
//        }
//    }
//    @Override
//    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
////Here we can set some flags on the player
//
//        //This flag tells the player to switch to landscape when in fullscreen, it will also return to portrait
//        //when leaving fullscreen
//        youTubePlayer.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION);
//
//        //This flag tells the player to automatically enter fullscreen when in landscape. Since we don't have
//        //landscape layout for this activity, this is a good way to allow the user rotate the video player.
//        youTubePlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE);
//
//        //This flag controls the system UI such as the status and navigation bar, hiding and showing them
//        //alongside the player UI
//        youTubePlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_SYSTEM_UI);
//
//        if (mVideoId != null) {
//            if (b) {
//                youTubePlayer.play();
//            } else {
//                youTubePlayer.loadVideo(mVideoId);
//            }
//        }
//    }
//
//    @Override
//    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
//        if (youTubeInitializationResult.isUserRecoverableError()) {
//            youTubeInitializationResult.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
//        } else {
//            //Handle the failure
//            Toast.makeText(this, R.string.error_init_failure, Toast.LENGTH_LONG).show();
//        }
//    }






    /**
     * An asynchronous task that handles the YouTube Data API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.youtube.YouTube mService = null;
        private Exception mLastError = null;

        MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.youtube.YouTube.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Food Fusion")
                    .build();
        }

        /**
         * Background task to call YouTube Data API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {

                mLastError = e;
                cancel(true);
                return null;
            }
        }

        /**
         * Fetch information about the "GoogleDevelopers" YouTube channel.
         * @return List of Strings containing information about the channel.
         * @throws IOException
         */
        private List<String> getDataFromApi() throws IOException {
            // Get a list of up to 10 files.
            List<String> channelInfo = new ArrayList<String>();
            ChannelListResponse result = mService.channels().list("snippet,contentDetails,statistics")
                    .setForUsername("GoogleDevelopers")
                    .execute();
            List<Channel> channels = result.getItems();
            if (channels != null) {
                Channel channel = channels.get(0);
                channelInfo.add("This channel's ID is " + channel.getId() + ". " +
                        "Its title is '" + channel.getSnippet().getTitle() + ", " +
                        "and it has " + channel.getStatistics().getViewCount() + " views.");
            }
            return channelInfo;
        }


        @Override
        protected void onPreExecute() {
//            mOutputText.setText("");
//            mProgress.show();
        }

        @Override
        protected void onPostExecute(List<String> output) {
//            mProgress.hide();
            if (output == null || output.size() == 0) {
//                mOutputText.setText("No results returned.");
            } else {
                output.add(0, "Data retrieved using the YouTube Data API:");
//                mOutputText.setText(TextUtils.join("\n", output));
            }
        }

        @Override
        protected void onCancelled() {
//            mProgress.hide();
            if (mLastError != null) {
//                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
//                    showGooglePlayServicesAvailabilityErrorDialog(
//                            ((GooglePlayServicesAvailabilityIOException) mLastError)
//                                    .getConnectionStatusCode());
//                } else if (mLastError instanceof UserRecoverableAuthIOException) {
//                    startActivityForResult(
//                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
//                            MainActivity.REQUEST_AUTHORIZATION);
//                } else {
//                    mOutputText.setText("The following error occurred:\n"
//                            + mLastError.getMessage());
//                }
            } else {
//                mOutputText.setText("Request cancelled.");
            }
        }
    }
    Html.ImageGetter imageGetter = new Html.ImageGetter() {

        @Override
        public Drawable getDrawable(String source) {
            Drawable d = getResources().getDrawable(R.drawable.bullet_icon);
            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            return d;
        }
    };
    public class SearchRuntimeGet extends AsyncTask<String, Void, Cursor> {

        private  final String[] sAutocompleteColNames = new String[] {
                BaseColumns._ID,                         // necessary for adapter
                SearchManager.SUGGEST_COLUMN_TEXT_1      // the full search term
        };

        @Override
        protected Cursor doInBackground(String... params) {

            MatrixCursor cursor = new MatrixCursor(sAutocompleteColNames);

            // get your search terms from the server here, ex:
            List<RecipeModel> terms = DataCenter.GetSearchString(params[0]);

            // parse your search terms into the MatrixCursor
            if(terms!=null){
                for (int index = 0; index < terms.size(); index++) {
                    String term = Html.fromHtml(terms.get(index).getTitle()).toString();

                    Object[] row = new Object[] { terms.get(index).getID(), term };
                    cursor.addRow(row);
                }}

            return cursor;
        }

        @Override
        protected void onPostExecute(Cursor result) {
            searchView.getSuggestionsAdapter().changeCursor(result);
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

    @Override //reconfigure display properties on screen rotation
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        //Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            // handle change here
        }
        else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            // or here
        }
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
                    if(!appSharedPrefs.contains("RecipeDetailData")){
                        data.setVisibility(View.GONE);
                        noData.setVisibility(View.VISIBLE);
                    }else {
                        if(appSharedPrefs.getInt("RecipeDetailDataID",-1)==id) {
                            data.setVisibility(View.VISIBLE);
                            noData.setVisibility(View.GONE);
                            populateRecipeDetail();
                            populateMayLikeRecipe();
                        } else{
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
            Utility.hideLoader(currentActivity);
            // Toast.makeText(currentActivity, R.string.FavouriteMakeError, Toast.LENGTH_LONG).show();
            return false;
        }
    }
    public void Reload(View v){
        Utility.showLoader(this);
        if(checkIfNet()){
            callApi();
            data.setVisibility(View.VISIBLE);
            noData.setVisibility(View.GONE);
        }else{

            data.setVisibility(View.GONE);
            noData.setVisibility(View.VISIBLE);
            Toast.makeText(this,R.string.stillNoConnection,Toast.LENGTH_SHORT).show();
            Utility.hideLoader(this);
        }
    }
    public void backButton(View v){
        finish();
    }
    public void likeClickHandler(View v) {
        if(checkIfNet()) {
            if (mMySharedPreference.isUserLogin()) {


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
            } else {
                Utility.showLoginLikeMessage(currentActivity);
            }
        } else {
            Toast.makeText(this,R.string.stillNoConnection,Toast.LENGTH_SHORT).show();
        }
        //Toast.makeText(getApplicationContext(), "You clicked on position : " + itemToRemove.getTitle() + " and id : " + itemToRemove.getID(), Toast.LENGTH_LONG).show();
        //adapter.remove(itemToRemove);

    }
}
