package com.foodfusion.foodfusion.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.foodfusion.foodfusion.Adapter.CategGridAdapter;
import com.foodfusion.foodfusion.CategoryRecipeActivity;
import com.foodfusion.foodfusion.Custom.MyGridView;
import com.foodfusion.foodfusion.DB.MySharedPreference;
import com.foodfusion.foodfusion.DashboardActivity;
import com.foodfusion.foodfusion.Model.CategoryModel;
import com.foodfusion.foodfusion.R;
import com.foodfusion.foodfusion.SubCategoriesActivity;
import com.foodfusion.foodfusion.Util.Utility;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class AllCategoryFragment extends Fragment {

    Activity currentActivity;
    List<CategoryModel> categories;

    MyGridView mgrid;
    CategGridAdapter mGridAdapter;
    MySharedPreference mMySharedPreference;
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
        return inflater.inflate(R.layout.fragment_all_category, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        currentActivity = getActivity();
        data=(LinearLayout)getView().findViewById(R.id.data);
        noData=(LinearLayout)getView().findViewById(R.id.noData);
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
        FrameLayout rootLayout = (FrameLayout)getView().findViewById(R.id.frameMain);
        Utility.overrideFonts(currentActivity, rootLayout);

        initlializeData(currentActivity);
    }

    public void initlializeData(Context context){
        try {
            mMySharedPreference=new MySharedPreference(context);
            categories = mMySharedPreference.getCategories();
            if(!((DashboardActivity)getActivity()).checkIfNet()){
                if (categories!=null) {
                    //populateRecipe();
                    gridPopulate();
                    data.setVisibility(VISIBLE);
                    noData.setVisibility(GONE);
                }else{
                    data.setVisibility(GONE);
                    noData.setVisibility(VISIBLE);
                }
            } else{
                data.setVisibility(VISIBLE);
                noData.setVisibility(GONE);
                gridPopulate();
            }

        }
        catch(Exception ex){

        }
    }
public  void gridPopulate(){
    //  Collections.sort(categories);
    mgrid= (MyGridView) getView().findViewById(R.id.grid);
    mGridAdapter = new CategGridAdapter(currentActivity,(ArrayList<CategoryModel>) categories);
    mgrid.setAdapter(mGridAdapter);
    // mgrid.setOnScrollListener(this);
    // Pager++;

    mgrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long itemId) {
            CategoryModel categ= categories.get(position);
            if(categ.getSubCategory()!=null){
                Intent intent = new Intent(adapterView.getContext(), SubCategoriesActivity.class);
                intent.putExtra("id",(int)itemId);
                intent.putExtra("name",categ.getCategory());
                adapterView.getContext().startActivity(intent);
            }
            else {
                Intent intent = new Intent(adapterView.getContext(), CategoryRecipeActivity.class);
                intent.putExtra("id",(int)itemId);
                intent.putExtra("name",categ.getCategory());
                startActivity(intent);
            }
        }
    });
}
        @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
