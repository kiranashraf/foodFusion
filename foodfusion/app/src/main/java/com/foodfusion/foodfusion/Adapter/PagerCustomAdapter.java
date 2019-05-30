package com.foodfusion.foodfusion.Adapter;

/**
 * Created by saifuddin on 11/2/2016.
 */

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.foodfusion.foodfusion.Fragment.AllCategoryFragment;
import com.foodfusion.foodfusion.Fragment.FoodFusionFragment;
import com.foodfusion.foodfusion.Fragment.HealthyFusionFragment;
import com.foodfusion.foodfusion.Fragment.KidsFragment;

public class PagerCustomAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerCustomAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public android.support.v4.app.Fragment getItem(int position) {

        switch (position) {
            case 0:
                FoodFusionFragment tab1 = new FoodFusionFragment();
                return tab1;
            case 1:
                HealthyFusionFragment tab2 = new HealthyFusionFragment();
                return tab2;
            case 2:
                KidsFragment tab3 = new KidsFragment();
                return tab3;
            case 3:
                AllCategoryFragment tab4 = new AllCategoryFragment();
                return tab4;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}