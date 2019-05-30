package com.foodfusion.foodfusion;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.foodfusion.foodfusion.Adapter.FilterAdapter;
import com.foodfusion.foodfusion.DB.MySharedPreference;
import com.foodfusion.foodfusion.Listeners.FilterClickListener;
import com.foodfusion.foodfusion.Model.CategoryModel;
import com.foodfusion.foodfusion.Model.FilterModel;
import com.foodfusion.foodfusion.Util.Utility;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.labo.kaji.relativepopupwindow.RelativePopupWindow;

import java.util.ArrayList;
import java.util.List;

public class FilterActivity extends RelativePopupWindow {

    //Activity currentActivity;
    Button mapply_filter;
    LinearLayout ll1;
    Context context;
    List<CategoryModel> categLst;
    String term="";
    RecyclerView recyclerView;
    ArrayList<String> filterSend=new ArrayList<String>();
    int count=0;
    TextView filterCount,untoggle;
    ArrayList<FilterModel> lst;
    FilterAdapter adap;
    Button apply_filter;

    String[] FILTERS = new String[] {
            "Biryani", "Sabzi (Vegetable)", "Burgers", "Karahi", "Rice recipes", "Sandwich/burger", "Chicken",
            "Snacks and Appetizers", "Ramzan", "Bakra Eid", "Parathas","Chinese", "Everyday cooking", "Daal", "Kababs",
             "Biscuits", "Cakes and bakes", "Desi desserts", "Desserts", "Baked", "Healthy", "For kids",
            "Pizzas", "Fusion", "Homemade"
    };
public FilterActivity(){

}
    public FilterActivity(final Context context, String searchTerm){
        this.context=context;
        term=searchTerm;
        setContentView(LayoutInflater.from(context).inflate(R.layout.activity_filter, null));
        ll1=(LinearLayout)getContentView().findViewById(R.id.ll1) ;
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        // Disable default animation for circular reveal
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setAnimationStyle(0);
        }
        Utility.overrideFonts(context, ll1);
        filterCount=(TextView)getContentView().findViewById(R.id.filterCount) ;
        untoggle=(TextView) getContentView().findViewById(R.id.untoggle);
        try {
            fillFilter();
        } catch(Exception ex){
            //FirebaseCrash.log(ex.getMessage());
        }
        apply_filter=(Button)getContentView().findViewById(R.id.apply_filter);
        apply_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String filter = "";
                    for (int i = 0; i < filterSend.size(); i++) {
                        if (filter == "") {
                            filter = filterSend.get(i);
                        } else {
                            filter = filter + "," + filterSend.get(i);
                        }
                    }
                    String search = term;
                    Intent intent = new Intent(context, SearchActivity.class);
                    intent.putExtra("name", term);
                    intent.putExtra("filter", filter);
                    context.startActivity(intent);
                } catch(Exception ex){

                }

            }
        });
    }

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_filter);
        currentActivity = this;
        fillFilter();
        LinearLayout rootLayout = (LinearLayout) findViewById(R.id.ll1);
       // Utility.overrideFonts(currentActivity, rootLayout);
    }*/

    @SuppressLint("ResourceAsColor")
    public void fillFilter(){

        LinearLayout ll_filterItems = (LinearLayout)getContentView().findViewById(R.id.ll_filterItems);
        /*mapply_filter = (Button)findViewById(R.id.apply_filter);
        mapply_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(currentActivity, SearchActivity.class);
                // intent.putExtra("id",alRecipe.get(position).getID());
                startActivity(intent);
            }
        });*/

        TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
        TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        rowParams.setMargins(3,3,3,3);

        TableLayout tableLayout = new TableLayout(getContentView().getContext());
        tableLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));// assuming the parent view is a LinearLayout

        TableRow tableRow = new TableRow(getContentView().getContext());
        tableRow.setLayoutParams(tableParams);// TableLayout is the parent view

        tableLayout.addView(tableRow);


//        ll_filterItems.addView(tableLayout);

        recyclerView= (RecyclerView) getContentView().findViewById(R.id.recyclerview);
        FlexboxLayoutManager mLayoutManager = new FlexboxLayoutManager(context);
        mLayoutManager.setFlexWrap(FlexWrap.WRAP);
        mLayoutManager.setFlexDirection(FlexDirection.ROW);

//        mLayoutManager.setAlignItems(AlignItems.STRETCH);
//        mLayoutManager.setJustifyContent(JustifyContent.SPACE_AROUND);
//        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context
//        );

//        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
//        recyclerView.setLayoutManager(mLayoutManager);
        MySharedPreference mMySharedPreference=new MySharedPreference(context);
        categLst = mMySharedPreference.getCategories();
        mLayoutManager.onAttachedToWindow(recyclerView);
        recyclerView.setLayoutManager(mLayoutManager);
        lst=new ArrayList<>();
        if(categLst!=null) {
            for (int i = 0; i < categLst.size(); i++) {
                FilterModel obj = new FilterModel();
                obj.setId(i);
                obj.setCategory(FILTERS[i]);
                obj.setChecked(false);
                lst.add(obj);
            }
        }
        FilterClickListener listener = new FilterClickListener() {
            @Override
            public void onClick(View view, int position) {
                try {
                    boolean isChecked = ((ToggleButton) view).isChecked();
                    if (isChecked) {
                        filterSend.add(((ToggleButton) view).getText().toString());
                        filterCount.setText("Add Filter(" + filterSend.size() + ")");
                    } else {
                        filterSend.remove(((ToggleButton) view).getText().toString());
                    }
                    filterCount.setText("Add Filter(" + filterSend.size() + ")");
                    lst.get(position).setChecked(isChecked);
                    // Toast.makeText(context, "Position " + position, Toast.LENGTH_SHORT).show();
                }catch (Exception ex){

                }
            }
        };
        adap=new FilterAdapter(context, lst, listener);

        recyclerView.setAdapter(adap);


        WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);

        Display display = windowManager.getDefaultDisplay();
        int width = display.getWidth();
        int height=display.getHeight();
        recyclerView.getLayoutParams().height=height/2;
       // recyclerView.getLayoutParams().width=width/2 + width/3;
        untoggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    filterSend = null;
                    filterSend = new ArrayList<>();
                    filterCount.setText("Add Filter(" + filterSend.size() + ")");
                    adap.unToggleAll();
                }catch(Exception ex){

                }
            }
        });
    }


}
