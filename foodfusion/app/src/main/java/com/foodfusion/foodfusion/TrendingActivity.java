package com.foodfusion.foodfusion;

import android.app.Activity;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
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
import static com.foodfusion.foodfusion.Util.Constants.SEARCH_QUERY_THRESHOLD;

public class TrendingActivity extends AppCompatActivity  implements AbsListView.OnScrollListener {

    Activity currentActivity;
    GridViewWithHeaderAndFooter mgrid;
    GridAdapter mGridAdapter;
    static int Pager=1;
    LinearLayout loadMore1;
    TextView mtrending_more;

    LinearLayout rootLayout;

    ImageView mImg_back;

    Object id;

    String apiCall;
    String dataSp;
    String dataCacheSp;

    TextView mtv_title;

    MySharedPreference mMySharedPreference;
    NetworkChangeReceiver mReceiver;

//search

    SearchView searchView;
    View mFooterView;

    LinearLayout network;
    LinearLayout data,noData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Pager=1;
        setContentView(R.layout.activity_trending);

        currentActivity = this;
        network=(LinearLayout)findViewById(R.id.network);
        data=(LinearLayout)findViewById(R.id.data);
        noData=(LinearLayout)findViewById(R.id.noData);
        mtv_title=(TextView)findViewById(R.id.tv_title);
        checkIfNet();
        mMySharedPreference= new MySharedPreference(this);

        id= getIntent().getExtras().get("name");
        if(id==Utility.ShowMore.TRENDING){
            apiCall="get-all-trending-recipes";
            dataSp="TrendingAllRecipeData";
            dataCacheSp="TrendingAllRecipeCacheData";
            mtv_title.setText(R.string.TrendingMore);
        } else if(id==Utility.ShowMore.MAYALSOLIKE){
            apiCall="get-all-more-recipes";
            dataSp="GetAllMoreRecipeData";
            dataCacheSp="GetAllMoreRecipeCacheData";
            mtv_title.setText(R.string.MayLikeMore);
        }
        else if(id==Utility.ShowMore.MYFAVOURITES){
            apiCall="show_fav_recipes";
            dataSp="FavAllRecipeData";
            dataCacheSp="FavAllRecipeCacheData";
            mtv_title.setText(R.string.MYFOURITES);
        }
        rootLayout = (LinearLayout) findViewById(R.id.cl1);




        mImg_back=(ImageView)findViewById(R.id.img_back);
        mImg_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        Utility.overrideFonts(currentActivity, rootLayout);
        checkIfNet();

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
        initializeData();


        //Search
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_item,menu);
        searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setQueryHint("Search...");
        searchView.setIconifiedByDefault(false);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        //Here is where the magic happens:
        int options = searchView.getImeOptions();
        searchView.setImeOptions(options| EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        int magId = getResources().getIdentifier("android:id/search_mag_icon", null, null);
        ImageView magImage = (ImageView) searchView.findViewById(magId);
        magImage.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        magImage.setVisibility(View.GONE);

        int searchPlateId = searchView.getContext().getResources().getIdentifier("android:id/search_plate", null, null);
        View searchPlate = searchView.findViewById(searchPlateId);
        if (searchPlate!=null) {
            searchPlate.setBackgroundColor(getResources().getColor(R.color.appColor));
            int searchTextId = searchPlate.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
            TextView searchText = (TextView) searchPlate.findViewById(searchTextId);
            if (searchText!=null) {
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
                    intent.putExtra("name",query);
                    startActivity(intent);
//                Intent intent = new Intent(MainActivity.this, SearchableActivity.class);
//                intent.setAction(Intent.ACTION_SEARCH);
//                intent.putExtra(SearchManager.QUERY, query);
//                startActivity(intent);

                    searchView.getSuggestionsAdapter().changeCursor(null);
                    return true;
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                if (query.length() >= SEARCH_QUERY_THRESHOLD) { //SEARCH_QUERY_THRESHOLD
                    new TrendingActivity.SearchRuntimeGet().execute(query);
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
                int id=cursor.getInt(cursor.getColumnIndex(BaseColumns._ID));
                searchView.setQuery(term,true);
                cursor.close();
                Intent intent = new Intent(currentActivity, ItemDetailActivity.class);
                intent.putExtra("id",id);
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
                new String[] { SearchManager.SUGGEST_COLUMN_TEXT_1 },
                new int[] { android.R.id.text1 }));
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1234 && resultCode == RESULT_OK) {
            ArrayList<String> matches = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
           // search.populateEditText(matches.get(0));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    public void initializeData(){
        try {
            SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
            if (appSharedPrefs.contains(dataCacheSp)) {
                populateCacheRecipes();
            }
           // Utility.showLoader(this);
            DataCenter.GetAllRecipe(this, apiCall,Pager);
        }
        catch(Exception ex){
            Utility.hideLoader(this);
            Utility.showMessage(this,  getString(R.string.defaultError), getString(R.string.alert));
        }

    }
    public void initializeData1(){
        try {
            Utility.showLoader(this);
            DataCenter.GetAllRecipe(this, apiCall,Pager);
        }
        catch(Exception ex){
            Utility.hideLoader(this);
            Utility.showMessage(this,  getString(R.string.defaultError), getString(R.string.alert));
        }

    }
public void populateRecipes(){
    SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
    Gson gson = new Gson();
    String json = "";
    String json1= "";
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

//                WindowManager windowManager = (WindowManager)this.getSystemService(Context.WINDOW_SERVICE);
//
//                Display display = windowManager.getDefaultDisplay();
//                int height=display.getHeight();
//                mgrid.getLayoutParams().height=height+height/3;

if(appSharedPrefs.contains(dataCacheSp)){
    json1 = appSharedPrefs.getString(dataSp, "");;
}
               // mgrid.getLayoutParams().height=height+height/3;
                SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();

                if(dataSp=="TrendingAllRecipeData"){
                    prefsEditor.putString("TrendingAllRecipeCacheData", json);
                    prefsEditor.commit();
                } else if(dataSp=="GetAllMoreRecipeData"){
                    prefsEditor.putString("GetAllMoreRecipeCacheData", json);
                    prefsEditor.commit();
                } else if (dataSp=="FavAllRecipeData"){
                    prefsEditor.putString("FavAllRecipeCacheData", json);
                    prefsEditor.commit();
                }
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
            Utility.showMessage(this,  "No recipes found!","Info");
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
        this.registerReceiver(mMessageReceiver, new IntentFilter("TrendingAllRecipe"));
        this.registerReceiver(mMessageReceiver, new IntentFilter("GetAllMoreRecipe"));
        this.registerReceiver(mMessageReceiver, new IntentFilter("FavAllRecipe"));
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

            if(intent.getAction().equals("TrendingAllRecipe") || intent.getAction().equals("GetAllMoreRecipe")
                    || intent.getAction().equals("FavAllRecipe")){
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
                        if(apiCall.equals("show_fav_recipes")) {
                            finish();
                            startActivity(getIntent().putExtra("name", Utility.ShowMore.MYFAVOURITES));
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

    public void likeClickHandler(View v) {
        if(checkIfNet()) {
            if (mMySharedPreference.isUserLogin()) {


                RecipeModel item = (RecipeModel) v.getTag(R.id.recipeModelId);
                int drawable = (int) v.getTag(R.id.likeImageId);
                if (drawable == R.drawable.like_detail_icon) {
                    ImageView imgLike = v.findViewById(R.id.iv_like);
                    imgLike.setImageResource(R.drawable.liked_detail_icon);
                    imgLike.setTag(R.id.likeImageId, R.drawable.liked_detail_icon);
                    DataCenter.AddDeleteFavourite(this, "fav_recipe", item.getID());
                } else {
                    ImageView imgLike = v.findViewById(R.id.iv_like);
                    imgLike.setImageResource(R.drawable.like_detail_icon);
                    imgLike.setTag(R.id.likeImageId, R.drawable.like_detail_icon);
                    DataCenter.AddDeleteFavourite(this, "delete_fav_recipe", item.getID());
                }
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
            Toast.makeText(this,R.string.stillNoConnection,Toast.LENGTH_SHORT).show();
        }
        //Toast.makeText(getApplicationContext(), "You clicked on position : " + itemToRemove.getTitle() + " and id : " + itemToRemove.getID(), Toast.LENGTH_LONG).show();
        //adapter.remove(itemToRemove);

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {

            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

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
                    String term = Html.fromHtml(terms.get(index).getTitle()).toString() ;

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
                    if(!appSharedPrefs.contains(dataSp)){
                        data.setVisibility(View.GONE);
                        noData.setVisibility(View.VISIBLE);
                    }else {
                        data.setVisibility(View.VISIBLE);
                        noData.setVisibility(View.GONE);
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
