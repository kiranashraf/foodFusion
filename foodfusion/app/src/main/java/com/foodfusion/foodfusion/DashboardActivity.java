package com.foodfusion.foodfusion;

import android.app.Activity;
import android.app.Fragment;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.foodfusion.foodfusion.Adapter.NavDrawerListAdapter;
import com.foodfusion.foodfusion.Adapter.PagerCustomAdapter;
import com.foodfusion.foodfusion.Custom.NetworkChangeReceiver;
import com.foodfusion.foodfusion.DB.MySharedPreference;
import com.foodfusion.foodfusion.ImageCache.ImageFetcherNew;
import com.foodfusion.foodfusion.Model.CategoryModel;
import com.foodfusion.foodfusion.Model.GenericModel;
import com.foodfusion.foodfusion.Model.NavDrawerItem;
import com.foodfusion.foodfusion.Model.RecipeModel;
import com.foodfusion.foodfusion.Util.Constants;
import com.foodfusion.foodfusion.Util.DataCenter;
import com.foodfusion.foodfusion.Util.Utility;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.foodfusion.foodfusion.Util.Constants.SEARCH_QUERY_THRESHOLD;
import static com.foodfusion.foodfusion.Util.Constants.drawer.ALLCATEGORIES;

public class DashboardActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerArrowDrawable drawerArrow;
    DrawerLayout rootLayout;
    ArrayList<NavDrawerItem> navDrawerItems;
    Activity currentActivity;
    String[] menuText;
    LinearLayout mainLayout;
    EditText searchText;
    MySharedPreference mMySharedPreference;
    String userName="Guest";

    SearchView searchView;
    NetworkChangeReceiver mReceiver;

    private ImageFetcherNew mImageFetcher;

    private static final int LOGIN_REQUEST_CODE = 2121;
    private static final int SIGNUP_REQUEST_CODE = 2122;
    LinearLayout network;

    TabLayout tabLayout;
    public PagerCustomAdapter adapter;
    // ViewPagerAdapter
    private HashMap<Integer, Fragment> fragmentHashMap = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_dashboard);

            if (getIntent().getBooleanExtra("EXIT", false)) {
                finish();
            }

            currentActivity = this;
            mImageFetcher = ((Global) this.getApplication()).getImageFetcher(currentActivity);
            network = (LinearLayout) findViewById(R.id.network);
//        ((Global) this.getApplication()).setImageFetcher(currentActivity);
            mMySharedPreference = new MySharedPreference(this);

//        Picasso.setSingletonInstance(getCustomPicasso());

            mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

            Toolbar toolbar2 = (Toolbar) findViewById(R.id.toolbar2);
            setSupportActionBar(toolbar2);

            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);
            SetMenuButton();

            SetTabs();
        /*searchText = (EditText)findViewById(R.id.searchText);
        searchText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (searchText.getRight() - searchText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        Toast.makeText(currentActivity,"Test Click",Toast.LENGTH_LONG);
                        return true;
                    }
                }
                return false;
            }
        });*/
            try {
                if (mMySharedPreference.isUserLogin()) {
                    userName = mMySharedPreference.getUserInfo().getDisplayName();
                } else {
                    userName = "Guest";
                }
            } catch (Exception ex) {
                userName = "Guest";
                mMySharedPreference.setUserLoggedIn(false);
            }
            try {
                setSideBar();
            } catch (Exception ex) {
                DataCenter.GetCategories(this, "get-categories");
            }

            SetTitleFragments("Hi, " + userName);
            SetUserPicFragments();
            rootLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            Utility.overrideFonts(this, rootLayout);
        }
        catch (Exception ex){

        }
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

                    searchView.getSuggestionsAdapter().changeCursor(null);
                    return true;
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                if (query.length() >= SEARCH_QUERY_THRESHOLD) { //SEARCH_QUERY_THRESHOLD
                    new DashboardActivity.SearchRuntimeGet().execute(query);
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
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        too.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
//        super.onConfigurationChanged(newConfig);
//        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
//    }

    public void setSideBar(){
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.navdrawer);

        drawerArrow = new DrawerArrowDrawable(this) {

        };

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.hamburger_menu_icon, R.string.drawer_open,
                R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                mainLayout.setTranslationX(slideOffset * drawerView.getWidth());
                mDrawerLayout.bringChildToFront(drawerView);
                mDrawerLayout.requestLayout();
            }

        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        menuText = getResources().getStringArray(R.array.app_side_menu);

        navDrawerItems = new ArrayList<NavDrawerItem>();
        if(mMySharedPreference.isUserLogin()){
            navDrawerItems.add(new NavDrawerItem(userName ,Constants.drawer.LOGGEDIN.getValue()));
            navDrawerItems.add(new NavDrawerItem("My Favourites" ,Constants.drawer.FAVOURITES.getValue()));
        } else {
            navDrawerItems.add(new NavDrawerItem(userName ,Constants.drawer.LOGGEDIN.getValue()));
            navDrawerItems.add(new NavDrawerItem("Login", Constants.drawer.LOGIN.getValue()));
            navDrawerItems.add(new NavDrawerItem("Signup", Constants.drawer.SIGNUP.getValue()));
        }
        List<CategoryModel> categ = mMySharedPreference.getCategories();
        if(categ!=null) {
            if (categ.size() > 5) {
                navDrawerItems.add(new NavDrawerItem("All Categories", ALLCATEGORIES.getValue()));
                for (int i = 0; i < 6; i++) {
                    navDrawerItems.add(new NavDrawerItem(categ.get(i).getCategory(), categ.get(i).getParentId()));
                }
            } else {
                navDrawerItems.add(new NavDrawerItem("All Categories", ALLCATEGORIES.getValue()));
                for (int i = 0; i < categ.size(); i++) {
                    navDrawerItems.add(new NavDrawerItem(categ.get(i).getCategory(), categ.get(i).getParentId()));
                }
            }
        }
        if(mMySharedPreference.isUserLogin()){
            navDrawerItems.add(new NavDrawerItem("Logout", Constants.drawer.LOGOUT.getValue()));
        }
        NavDrawerListAdapter adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                displayView(navDrawerItems.get(position).getId());
            }
        });

        //displayView(1);
    }

    private void SetTabs() {
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.food_fusion_icons).setText("Food Fusion"));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.healthy_fusion_icons).setText("Health Fusion"));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.kids_icons).setText("Kids"));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.all_category_icons).setText("All Categories"));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        adapter = new PagerCustomAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if(tab.getPosition() == 0) {
                    SetUserPicFragments();
                    SetTitleFragments("Hi, "+userName);
                }
                if(tab.getPosition() == 1) {
                    SetUserPicFragments();
                    SetTitleFragments("Healthy Fusion");
                }
                if(tab.getPosition() == 2) {
                    SetUserPicFragments();
                    SetTitleFragments("Kids Fusion");
                }
                if(tab.getPosition() == 3) {
                    SetUserPicFragments();
                    SetTitleFragments("Categories");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        tabLayout.getTabAt(0).select();
    }

    private void SetTitleFragments(String title) {
        TextView titleView = (TextView) findViewById(R.id.title);
        titleView.setText(title.replace("&","and"));
    }

    private void SetUserPicFragments() {
        final CircleImageView userPic=(CircleImageView) findViewById(R.id.icon_profile);
        if(mMySharedPreference.isSocialUserLogin()){
            if(mMySharedPreference.getUserPic()!=null){
                Global.picassoWithCache.with(this)
                        .load(mMySharedPreference.getUserPic()).error(R.drawable.profile_female_icon).resize(120,120).centerCrop().into(userPic);
                Global.picassoWithCache.with(this)
                        .load(mMySharedPreference.getUserPic())
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .resize(120,120).centerCrop()
                        .into(userPic, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                //Try again online if cache failed
                                Global.picassoWithCache.with(currentActivity)
                                        .load(mMySharedPreference.getUserPic())
                                        .error(R.drawable.profile_female_icon)
                                        .resize(120,120).centerCrop()
                                        .into(userPic, new Callback() {
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

            } else {
                Global.picassoWithCache.with(this)
                        .load(R.drawable.profile_female_icon).error(R.drawable.profile_female_icon).resize(120,120).centerCrop().into(userPic);

            }
        } else {
            Global.picassoWithCache.with(this)
                    .load(R.drawable.profile_female_icon).error(R.drawable.profile_female_icon).resize(120,120).centerCrop().into(userPic);

        }
//        TextView titleView = (TextView) findViewById(R.id.title);
//        titleView.setText(title);
    }

    private void displayView(int position) {
        // update the main content by replacing fragments
        Intent i = null;
        Bundle bundle = new Bundle();
        int parameter = 0;
        //position = 7;
        String itemName = Constants.drawer.name(position);
        Intent intent;
        if(itemName==null){


            CategoryModel obj=new CategoryModel();
            List<CategoryModel> categ = mMySharedPreference.getCategories();
            for(int j=0;j<categ.size();j++){
                if(categ.get(j).getParentId()==position){
                    obj=categ.get(j);
                    break;
                }
            }
            if(obj.getSubCategory()!=null){
                intent = new Intent(this, SubCategoriesActivity.class);
                intent.putExtra("id",(int)obj.getParentId());
                intent.putExtra("name",obj.getCategory());
                startActivity(intent);
            }
            else {
                intent = new Intent(this, CategoryRecipeActivity.class);
                intent.putExtra("id",(int)obj.getParentId());
                intent.putExtra("name",obj.getCategory());
                startActivity(intent);
            }

//            intent = new Intent(DashboardActivity.this, CategoryRecipeActivity.class);
//
//            intent.putExtra("id",position);
//            intent.putExtra("name",name);
//            startActivity(intent);
        }
        else {

            Constants.drawer item = Constants.drawer.valueOf(itemName);
            switch (item) {
                case LOGIN:
                        intent = new Intent(DashboardActivity.this, SigninActivity.class);
                        startActivityForResult(intent, LOGIN_REQUEST_CODE);
                        overridePendingTransition(0, 0);
                        //mDrawerLayout.closeDrawers();
                        break;
                case LOGOUT:
                    mMySharedPreference.setUserLoggedIn(false);
                    mMySharedPreference.setSocialUserLoggedIn(false);
//                    facebook logout?\
                    LoginManager.getInstance().logOut();
                    finish();
                    startActivity(getIntent());
                    break;
                case LOGGEDIN:
                    intent = new Intent(DashboardActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    break;
                case SIGNUP:
                    intent = new Intent(DashboardActivity.this, LandingActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    //mDrawerLayout.closeDrawers();
                    break;
                case FAVOURITES:
                    //mDrawerLayout.closeDrawers();
                    intent = new Intent(DashboardActivity.this, TrendingActivity.class);
                    intent.putExtra("name",Utility.ShowMore.MYFAVOURITES);
                    startActivity(intent);
                    break;
                case ALLCATEGORIES:
                    tabLayout.getTabAt(3).select();
                    mDrawerLayout.closeDrawers();
                    break;
//                    intent = new Intent(DashboardActivity.this, SignUpActivity.class);
//                    startActivity(intent);
//                    overridePendingTransition(0, 0);
                default:
                    break;
            }
        }
    }

    private void SetMenuButton(){
        ImageView menu = (ImageView) findViewById(R.id.icon_profile_drawer);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // toggleMenu(v);
                    if (mDrawerLayout.isDrawerVisible(Gravity.LEFT)) {
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                        return;
                    } else {
                        mDrawerLayout.openDrawer(Gravity.LEFT);
                    }
                } catch (Exception ex){

                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (mDrawerToggle.onOptionsItemSelected(item)) {
                return true;
            }
        }
        mDrawerLayout.closeDrawer(mDrawerList);
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public void setSideBarImage(){
        //TextView tv_header_heading = (TextView) findViewById(R.id.tv_header_heading);
    }

    @Override
    public void onResume() {
        checkIfNet();
        this.mReceiver = new NetworkChangeReceiver();
        registerReceiver(
                this.mReceiver,
                new IntentFilter(
                        ConnectivityManager.CONNECTIVITY_ACTION));
        super.onResume();

        this.registerReceiver(mMessageReceiver, new IntentFilter("FavouriteMade"));
        this.registerReceiver(mMessageReceiver, new IntentFilter("NetworkConnection"));
        this.registerReceiver(mMessageReceiver, new IntentFilter("GetAllCategory"));
    }
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

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
                    Toast.makeText(currentActivity, R.string.FavouriteMakeError, Toast.LENGTH_LONG).show();
                }
            }
            if(intent.getAction().equals("GetAllCategory")){
                try {
                    setSideBar();
                }
                catch(Exception ex){
//                    Toast.makeText(currentActivity, R.string.FavouriteMakeError, Toast.LENGTH_LONG).show();
                }
            }
            if(intent.getAction().equals("NetworkConnection")){
                checkIfNet();
            }
            // refreshScreen();
        }
    };

    @Override
    public void onPause() {

        unregisterReceiver(mReceiver);
        super.onPause();
        this.unregisterReceiver(mMessageReceiver);
    }

    public void likeClickHandlerFragment(View v) {
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
            } else {
                Utility.showLoginLikeMessage(currentActivity);
            }
        } else {
            Toast.makeText(this,R.string.stillNoConnection,Toast.LENGTH_SHORT).show();
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

    public void likeClickHandlerFF(View v) {
        if(checkIfNet()) {
            if (mMySharedPreference.isUserLogin()) {


                RecipeModel item = (RecipeModel) v.getTag(R.id.recipeModelId);
                int drawable = (int) v.getTag(R.id.likeImageId);
                if (drawable == R.drawable.like_detail_icon) {
                    ImageView imgLike = v.findViewById(R.id.iv_type_like);
                    imgLike.setImageResource(R.drawable.liked_detail_icon);
                    imgLike.setTag(R.id.likeImageId, R.drawable.liked_detail_icon);
                    DataCenter.AddDeleteFavourite(currentActivity, "fav_recipe", item.getID());
                } else {
                    ImageView imgLike = v.findViewById(R.id.iv_type_like);
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

    public void loadProfile(View v){
        if(mMySharedPreference.isUserLogin()){
        Intent intent = new Intent(this, ProfileActivity.class);
//        intent.putExtra("name",Utility.ShowMore.TRENDING);
        startActivity(intent);
    }
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



    public Boolean checkIfNet(){
        try {
            SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(currentActivity);
            Gson gson = new Gson();
            Boolean json = false;
            if (appSharedPrefs.contains("isNetworkConnection")) {
                json = appSharedPrefs.getBoolean("isNetworkConnection", false);

                if (json) {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) tabLayout.getLayoutParams();
                    params.bottomMargin=0;
                    tabLayout.setLayoutParams(params);
                    tabLayout.setLayoutParams(params);
                    network.setVisibility(View.GONE);
                } else{
//                    Toast.makeText(currentActivity, "Connection Lost", Toast.LENGTH_LONG).show();

                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) tabLayout.getLayoutParams();
                    params.bottomMargin=200;
                    tabLayout.setLayoutParams(params);
                    network.setVisibility(View.VISIBLE);

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

    }

}
