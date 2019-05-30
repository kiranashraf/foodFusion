package com.foodfusion.foodfusion.Adapter;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.foodfusion.foodfusion.Global;
import com.foodfusion.foodfusion.Listeners.ItemClickListener;
import com.foodfusion.foodfusion.Model.RecipeModel;
import com.foodfusion.foodfusion.R;
import com.foodfusion.foodfusion.Util.Utility;

import java.util.ArrayList;

/**
 * Created by Rameez on 2/28/2018.
 */

public class GridResizeAdapter extends BaseAdapter {

    ArrayList<RecipeModel> alRecipe;
    Context context;
    LayoutInflater inflater;
    public GridResizeAdapter(Context context, ArrayList<RecipeModel> alRecipe) {
        super();
        this.context = context;
        this.alRecipe = alRecipe;
//        View v = LayoutInflater.from(context)
//                .inflate(R.layout.grid_item, viewGroup, false);
        inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

//        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
//			.build();
//        ImageLoader.getInstance().init(config);
//        ImageLoader imageLoader=new ImageLoader();
    }

    public void add(RecipeModel recipe)
    {
        Log.w("RecipeModel GridAdapter","add");
        alRecipe.add(recipe);
    }
    @Override
    public int getCount() {
        return alRecipe.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return alRecipe.get(i).getID();
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
try{
    if(convertView==null){
        viewHolder = new ViewHolder();
        convertView = inflater.inflate(R.layout.grid_item,parent,false);

        convertView.setTag(viewHolder);
    }
    else {
        viewHolder=(ViewHolder)convertView.getTag();
    }
    viewHolder.tvSpecies=(TextView)convertView.findViewById(R.id.tv_species);
    viewHolder.imgThumbnail=(ImageView)convertView.findViewById(R.id.img_thumbnail);
    viewHolder.iv_icon=(ImageView) convertView.findViewById(R.id.iv_icon);
    viewHolder.iv_like=(ImageView) convertView.findViewById(R.id.iv_like);
    viewHolder.iv_like.setTag(R.id.recipeModelId,alRecipe.get(i));

        viewHolder.tvSpecies.setText(Html.fromHtml(alRecipe.get(i).getTitle()));
        viewHolder.tvSpecies.setTypeface(Utility.SetTextFontRegular(context));


    Global.picassoWithCache.with(context)
                .load(alRecipe.get(i).getImage()).into(viewHolder.imgThumbnail);
        viewHolder.id = alRecipe.get(i).getID();
        if(alRecipe.get(i).getIsFavourite()==1){
            viewHolder.iv_like.setImageResource(R.drawable.liked_detail_icon);
            viewHolder.iv_like.setTag(R.id.likeImageId,R.drawable.liked_detail_icon);
        } else {
            viewHolder.iv_like.setImageResource(R.drawable.like_detail_icon);
            viewHolder.iv_like.setTag(R.id.likeImageId,R.drawable.like_detail_icon);
        }
    if(Integer.parseInt(alRecipe.get(i).getTermId())==464 ||alRecipe.get(i).getTermSlug().equals("food-fusion-kids") ){
        viewHolder.iv_icon.setImageResource(R.drawable.ff_kids_symbol);
    }else if (Integer.parseInt(alRecipe.get(i).getTermId())==463 ||alRecipe.get(i).getTermSlug().equals("healthy-fusion")){
        viewHolder.iv_icon.setImageResource(R.drawable.ff_healthy_symbol);
    } else {
        viewHolder.iv_icon.setImageResource(R.drawable.ff_logo_small_icon);
    }
        //viewHolder.imgThumbnail.setImageResource(alRecipe.get(i).getImage());


}
catch(Exception ex)
{


}
        return convertView;
    }

    public static class ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public ImageView imgThumbnail;
        public TextView tvSpecies;
        public int id;
        public ImageView iv_like;
        public ImageView iv_icon;
       
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