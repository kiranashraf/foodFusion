package com.foodfusion.foodfusion;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

/**
 * Created by Rameez on 3/14/2018.
 */

public class ActivityTest extends AppCompatActivity {
    //amber search
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("Material Search");
//        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
//        searchView=(MaterialSearchView)findViewById(R.id.search_view);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_item,menu);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setQueryHint("Search...");
searchView.setIconifiedByDefault(false);
        int magId = getResources().getIdentifier("android:id/search_mag_icon", null, null);
        ImageView magImage = (ImageView) searchView.findViewById(magId);
        magImage.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        magImage.setVisibility(View.GONE);
        //searchIcon.setImageDrawable(null);
//        MenuItem item=menu.findItem(R.id.action_search);
//        searchView.setMenuItem(item);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
        if(id == R.id.action_search){
            Toast.makeText(this, "search",Toast.LENGTH_LONG);
        }

        return super.onOptionsItemSelected(item);
    }
}
