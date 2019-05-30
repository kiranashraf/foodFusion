package com.foodfusion.foodfusion.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.foodfusion.foodfusion.Global;
import com.foodfusion.foodfusion.Listeners.ItemClickListener;
import com.foodfusion.foodfusion.Model.DemoItem;
import com.foodfusion.foodfusion.R;
import com.foodfusion.foodfusion.Util.Utility;

import java.util.List;

/**
 * Created by Rameez on 3/7/2018.
 */

public class AsymGridAdapter extends ArrayAdapter<DemoItem> {

    private final LayoutInflater layoutInflater;
    Context context;
    public AsymGridAdapter(Context context, List<DemoItem> items) {
        super(context, 0, items);
        this.context=context;
        layoutInflater = LayoutInflater.from(context);
    }

    public AsymGridAdapter(Context context) {
        super(context, 0);
        this.context=context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

//        View    v = layoutInflater.inflate( R.layout.asym_adapter_item, parent, false);
//        TextView tvSpecies = (TextView) v.findViewById(R.id.tvSpecies);
//        ImageView img_type=(ImageView)v.findViewById(R.id.img_type);
//        ImageView iv_like=(ImageView)v.findViewById(R.id.iv_like);
//        ImageView img_thumbnail=(ImageView)v.findViewById(R.id.img_thumbnail) ;
//
//        //textView.setText(item.getText());
//        return v;
        AsymGridAdapter.ViewHolder viewHolder = null;
        try{
            DemoItem item = getItem(position);
            if(convertView==null){
                viewHolder = new AsymGridAdapter.ViewHolder();
                convertView = layoutInflater.inflate( R.layout.asym_adapter_item, parent, false);
                //convertView = inflater.inflate(R.layout.grid_item,parent,false);

                convertView.setTag(viewHolder);
            }
            else {
                viewHolder=(AsymGridAdapter.ViewHolder)convertView.getTag();
            }
            viewHolder.tvSpecies=(TextView)convertView.findViewById(R.id.tv_species);
            viewHolder.imgThumbnail=(ImageView)convertView.findViewById(R.id.img_thumbnail);
            viewHolder.img_type=(ImageView)convertView.findViewById(R.id.img_type);
            viewHolder.iv_like=(ImageView) convertView.findViewById(R.id.iv_like);
            viewHolder.iv_like.setTag(R.id.recipeModelId,item.getRecipe());

            viewHolder.tvSpecies.setText(item.getRecipe().getTitle());
            viewHolder.tvSpecies.setTypeface(Utility.SetTextFontRegular(context));
            Global.picassoWithCache.with(context)
                    .load(item.getRecipe().getImage()).into(viewHolder.imgThumbnail);
            viewHolder.id = item.getRecipe().getID();
            if(item.getRecipe().getIsFavourite()==1){
                viewHolder.iv_like.setImageResource(R.drawable.liked_detail_icon);
                viewHolder.iv_like.setTag(R.id.likeImageId,R.drawable.liked_detail_icon);
            } else {
                viewHolder.iv_like.setImageResource(R.drawable.like_detail_icon);
                viewHolder.iv_like.setTag(R.id.likeImageId,R.drawable.like_detail_icon);
            }
            if(Integer.parseInt(item.getRecipe().getTermId())==454 ||item.getRecipe().getTermSlug()=="food-fusion-kids" ){
                viewHolder.img_type.setImageResource(R.drawable.ff_kids_symbol);
            }else if (Integer.parseInt(item.getRecipe().getTermId())==453 ||item.getRecipe().getTermSlug()=="healthy-fusion"){
                viewHolder.img_type.setImageResource(R.drawable.ff_healthy_symbol);
            } else {
                viewHolder.img_type.setImageResource(R.drawable.ff_logo_small_icon);
            }
            //viewHolder.imgThumbnail.setImageResource(alRecipe.get(i).getImage());


        }
        catch(Exception ex) {


        }
        return convertView;
        }

    @Override public int getViewTypeCount() {
        return 2;
    }

    @Override public int getItemViewType(int position) {
        return position % 2 == 0 ? 1 : 0;
    }

    public void appendItems(List<DemoItem> newItems) {
        addAll(newItems);
        notifyDataSetChanged();
    }

    public void setItems(List<DemoItem> moreItems) {
        clear();
        appendItems(moreItems);
    }
    public static class ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public ImageView imgThumbnail;
        public TextView tvSpecies;
        public int id;
        public ImageView iv_like;
        public  ImageView img_type;


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