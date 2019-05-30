package com.foodfusion.foodfusion.Adapter;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.foodfusion.foodfusion.Listeners.ItemClickListener;
import com.foodfusion.foodfusion.Model.CategoryModel;
import com.foodfusion.foodfusion.R;
import com.foodfusion.foodfusion.Util.Utility;

import java.util.ArrayList;

/**
 * Created by Rameez on 3/8/2018.
 */

public class CategGridAdapter extends BaseAdapter {

    ArrayList<CategoryModel> alCategory;
    Context context;
    LayoutInflater inflater;
    public CategGridAdapter(Context context, ArrayList<CategoryModel> alCategory) {
        super();
        this.context = context;
        this.alCategory = alCategory;
//        View v = LayoutInflater.from(context)
//                .inflate(R.layout.grid_item, viewGroup, false);
        inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void add(CategoryModel category)
    {
        Log.w("Category GridAdapter","add");
        alCategory.add(category);
    }
    @Override
    public int getCount() {
        if(alCategory==null){
            return 0;
        }
        return alCategory.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return alCategory.get(i).getParentId();
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        CategGridAdapter.ViewHolder viewHolder = null;
        try{
            if(convertView==null){
                viewHolder = new CategGridAdapter.ViewHolder();
                convertView = inflater.inflate(R.layout.item_category,parent,false);

                convertView.setTag(viewHolder);
            }
            else {
                viewHolder=(CategGridAdapter.ViewHolder)convertView.getTag();
            }
            viewHolder.tv_Ctitle=(TextView)convertView.findViewById(R.id.tv_Ctitle);
            viewHolder.tv_Ctotal=(TextView)convertView.findViewById(R.id.tv_Ctotal);
            viewHolder.llParent=(LinearLayout) convertView.findViewById(R.id.llParent);
            viewHolder.llParent.setTag(R.id.categoryData,alCategory.get(i));
            viewHolder.iv_Cicon=(ImageView) convertView.findViewById(R.id.iv_Cicon);
if(alCategory.get(i).getCategory().length()<=15){

    //viewHolder.tv_Ctitle.setText(alCategory.get(i).getCategory().replace("&","and").replace("#308;",""));
    viewHolder.tv_Ctitle.setText(Html.fromHtml(alCategory.get(i).getCategory()));
    viewHolder.tv_Ctitle.setTypeface(Utility.SetTextFontRegular(context));
    viewHolder.tv_Ctotal.setText(alCategory.get(i).getTotalRecipes().toString()+ " recipe(s)");
    viewHolder.tv_Ctotal.setTypeface(Utility.SetTextFontRegular(context));
} else {
    String[] arrName=alCategory.get(i).getCategory().split(" ");
    String catTitle="";
    if(arrName.length>2){
        catTitle=arrName[0]+" "+arrName[1];
    } else {
        catTitle=arrName[0];
    }
//    viewHolder.tv_Ctitle.setTextSize((float) ((viewHolder.tv_Ctitle.getTextSize())/4) -2);
//    viewHolder.tv_Ctotal.setTextSize((float) ((viewHolder.tv_Ctotal.getTextSize())/4) -3);
    viewHolder.tv_Ctitle.setText(catTitle);
    viewHolder.tv_Ctitle.setTypeface(Utility.SetTextFontRegular(context));
    viewHolder.tv_Ctotal.setText(alCategory.get(i).getTotalRecipes().toString()+ " recipe(s)");
    viewHolder.tv_Ctotal.setTypeface(Utility.SetTextFontRegular(context));
}


            viewHolder.id = alCategory.get(i).getParentId();

            if(alCategory.get(i).getCategory().toLowerCase().contains("vegeta")){
                viewHolder.iv_Cicon.setImageResource(R.drawable.vegetarian);

            }else if(alCategory.get(i).getCategory().toLowerCase().contains("summer")){
                viewHolder.iv_Cicon.setImageResource(R.drawable.summer);

            }else if(alCategory.get(i).getCategory().toLowerCase().contains("sehri")|| alCategory.get(i).getCategory().toLowerCase().contains("aftari")||
                    alCategory.get(i).getCategory().toLowerCase().contains("iftari")){
                viewHolder.iv_Cicon.setImageResource(R.drawable.sehri);

            }else if(alCategory.get(i).getCategory().toLowerCase().contains("salad")){
                viewHolder.iv_Cicon.setImageResource(R.drawable.salads);

            }else if(alCategory.get(i).getCategory().toLowerCase().contains("rice")){
                viewHolder.iv_Cicon.setImageResource(R.drawable.rice);

            }else if(alCategory.get(i).getCategory().toLowerCase().contains("rama" +
                    "zan")){
                viewHolder.iv_Cicon.setImageResource(R.drawable.ramzan);

            }else if(alCategory.get(i).getCategory().toLowerCase().contains("pizza")){
                viewHolder.iv_Cicon.setImageResource(R.drawable.pizza);

            }else if(alCategory.get(i).getCategory().toLowerCase().contains("pasta")){
                viewHolder.iv_Cicon.setImageResource(R.drawable.pasta);

            }else if(alCategory.get(i).getCategory().toLowerCase().contains("paratha")){
                viewHolder.iv_Cicon.setImageResource(R.drawable.parathas);

            }else if(alCategory.get(i).getCategory().toLowerCase().contains("mutton")){
                viewHolder.iv_Cicon.setImageResource(R.drawable.mutton);

            }else if(alCategory.get(i).getCategory().toLowerCase().contains("lunch") || alCategory.get(i).getCategory().toLowerCase().contains("dinner")){
                viewHolder.iv_Cicon.setImageResource(R.drawable.lunch_dinner);

            }else if(alCategory.get(i).getCategory().toLowerCase().contains("kabab")){
                viewHolder.iv_Cicon.setImageResource(R.drawable.kababs);

            }else if(alCategory.get(i).getCategory().toLowerCase().contains("everyday")){
                viewHolder.iv_Cicon.setImageResource(R.drawable.everyday_cooking);

            }else if(alCategory.get(i).getCategory().toLowerCase().contains("desert") || alCategory.get(i).getCategory().toLowerCase().contains("dessert")){
                viewHolder.iv_Cicon.setImageResource(R.drawable.deserts);

            }else if(alCategory.get(i).getCategory().toLowerCase().contains("daal")){
                viewHolder.iv_Cicon.setImageResource(R.drawable.daal);

            }else if(alCategory.get(i).getCategory().toLowerCase().contains("curries") || alCategory.get(i).getCategory().toLowerCase().contains("curry")){
                viewHolder.iv_Cicon.setImageResource(R.drawable.curries);

            }else if(alCategory.get(i).getCategory().toLowerCase().contains("chutney") || alCategory.get(i).getCategory().toLowerCase().contains("dip")){
                viewHolder.iv_Cicon.setImageResource(R.drawable.chutneys_dips);

            }else if(alCategory.get(i).getCategory().toLowerCase().contains("chinese")){
                viewHolder.iv_Cicon.setImageResource(R.drawable.chinese);

            }else if(alCategory.get(i).getCategory().toLowerCase().contains("chicken")){
                viewHolder.iv_Cicon.setImageResource(R.drawable.chicken);

            }else if(alCategory.get(i).getCategory().toLowerCase().contains("cake")){
                viewHolder.iv_Cicon.setImageResource(R.drawable.cakes);

            }else if(alCategory.get(i).getCategory().toLowerCase().contains("burger") || alCategory.get(i).getCategory().toLowerCase().contains("sandwich")){
                viewHolder.iv_Cicon.setImageResource(R.drawable.burger_sandwich);

            }else if(alCategory.get(i).getCategory().toLowerCase().contains("breads")){
                viewHolder.iv_Cicon.setImageResource(R.drawable.breads);

            }else if(alCategory.get(i).getCategory().toLowerCase().contains("biryani")){
                viewHolder.iv_Cicon.setImageResource(R.drawable.biryani);

            }else if(alCategory.get(i).getCategory().toLowerCase().contains("baking") || alCategory.get(i).getCategory().toLowerCase().contains("bake")){
                viewHolder.iv_Cicon.setImageResource(R.drawable.baking);

            }else if(alCategory.get(i).getCategory().toLowerCase().contains("beef")){
                viewHolder.iv_Cicon.setImageResource(R.drawable.beef);

            }else if(alCategory.get(i).getCategory().toLowerCase().contains("winter")){
                viewHolder.iv_Cicon.setImageResource(R.drawable.winter_category_icon);

            }else if(alCategory.get(i).getCategory().toLowerCase().contains("bbq")){
                viewHolder.iv_Cicon.setImageResource(R.drawable.bbq_category_icon);

            } else if(alCategory.get(i).getCategory().toLowerCase().contains("breakfast")){
                viewHolder.iv_Cicon.setImageResource(R.drawable.breakfast_category_icon);

            } else if(alCategory.get(i).getCategory().toLowerCase().contains("dishtype")){
                viewHolder.iv_Cicon.setImageResource(R.drawable.dishtype_category_icon);

            } else if(alCategory.get(i).getCategory().toLowerCase().contains("drink")){
                viewHolder.iv_Cicon.setImageResource(R.drawable.drinks_category_icon);

            } else if(alCategory.get(i).getCategory().toLowerCase().contains("ingred")){
                viewHolder.iv_Cicon.setImageResource(R.drawable.ingregients_category_icon);

            } else if(alCategory.get(i).getCategory().toLowerCase().contains("lunch")){
                viewHolder.iv_Cicon.setImageResource(R.drawable.lunch_category_icon);

            } else if(alCategory.get(i).getCategory().toLowerCase().contains("snack")){
                viewHolder.iv_Cicon.setImageResource(R.drawable.snack_category_icon);

            } else {
                viewHolder.iv_Cicon.setImageResource(R.drawable.other_category_icon);
            }


            //viewHolder.imgThumbnail.setImageResource(alRecipe.get(i).getImage());


        }
        catch(Exception ex)
        {

String abc=ex.toString();
        }
        return convertView;
    }

    public static class ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public TextView tv_Ctitle;
        public TextView tv_Ctotal;
        public LinearLayout llParent;
        public int id;
        public ImageView iv_Cicon;


        @Override
        public boolean onLongClick(View view) {
            return false;
        }

        @Override
        public void onClick(View view) {

        }

        public void onClick(ItemClickListener itemClickListener) {
        }
    }

}