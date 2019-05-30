package com.foodfusion.foodfusion.Adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.foodfusion.foodfusion.DB.MySharedPreference;
import com.foodfusion.foodfusion.Global;
import com.foodfusion.foodfusion.Model.NavDrawerItem;
import com.foodfusion.foodfusion.R;
import com.foodfusion.foodfusion.Util.Utility;

import java.util.ArrayList;

public class NavDrawerListAdapter extends BaseAdapter {
	
	private Context context;
	ArrayList<NavDrawerItem> navDrawerItems;
    static private ArrayList<NavDrawerItem> navDrawerItems_static;
    MySharedPreference mMySharedPreference;
	
	public NavDrawerListAdapter(Context context, ArrayList<NavDrawerItem> navDrawerItems){
		this.context = context;
		mMySharedPreference=new MySharedPreference(context);
		this.navDrawerItems = navDrawerItems;
        this.navDrawerItems_static = navDrawerItems;
	}

	@Override
	public int getCount() {
		return navDrawerItems.size();
	}

	@Override
	public Object getItem(int position) {
		return navDrawerItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		try {
			if (convertView == null) {
				LayoutInflater mInflater = (LayoutInflater)
						context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
				convertView = mInflater.inflate(R.layout.drawer_list_item, null);
			}

			LinearLayout rl_drawer_list_item = (LinearLayout) convertView.findViewById(R.id.rl_drawer_list_item);
			ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon);
			ImageView imgIcon1 = (ImageView) convertView.findViewById(R.id.icon1);


			if (mMySharedPreference.isSocialUserLogin()) {
				if (mMySharedPreference.getUserPic() != null) {

					Global.picassoWithCache.with(context)
							.load(mMySharedPreference.getUserPic()).error(R.drawable.profile_female_icon).resize(60, 60).centerCrop().into(imgIcon1);
				} else {
					Global.picassoWithCache.with(context)
							.load(R.drawable.profile_female_icon).error(R.drawable.profile_female_icon).resize(60, 60).centerCrop().into(imgIcon1);

				}
			} else {
				Global.picassoWithCache.with(context)
						.load(R.drawable.profile_female_icon).error(R.drawable.profile_female_icon).resize(60, 60).centerCrop().into(imgIcon1);

			}
			TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
			txtTitle.setTypeface(Utility.SetTextFontRegular(context));
			imgIcon1.setVisibility(View.GONE);
			rl_drawer_list_item.setVisibility(View.GONE);

			if (position == 0) {
				LinearLayout lldrawer_list = (LinearLayout) convertView.findViewById(R.id.lldrawer_list);
				lldrawer_list.setGravity(Gravity.CENTER);
				rl_drawer_list_item.setGravity(Gravity.CENTER);
				rl_drawer_list_item.setVisibility(View.VISIBLE);
				imgIcon1.setVisibility(View.VISIBLE);
				txtTitle.setText(Html.fromHtml(navDrawerItems.get(position).getTitle()));
//			txtTitle.setText(navDrawerItems.get(position).getTitle().replace("&","and").replace("#038;",""));
			} else {
				txtTitle.setText(Html.fromHtml(navDrawerItems.get(position).getTitle()));
				//txtTitle.setText(navDrawerItems.get(position).getTitle().replace("&","and").replace("#038;",""));
			}

			return convertView;
		}
		catch(Exception ex){
			Toast.makeText(context,ex.getMessage(),Toast.LENGTH_LONG).show();
		}
		return convertView;
	}
}
