package com.foodfusion.foodfusion.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.foodfusion.foodfusion.DB.MySharedPreference;
import com.foodfusion.foodfusion.Global;
import com.foodfusion.foodfusion.ImageCache.ImageFetcherNew;
import com.foodfusion.foodfusion.ItemDetailActivity;
import com.foodfusion.foodfusion.Listeners.ItemClickListener;
import com.foodfusion.foodfusion.Model.RecipeModel;
import com.foodfusion.foodfusion.R;
import com.foodfusion.foodfusion.Util.DataCenter;
import com.foodfusion.foodfusion.Util.Utility;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by Rameez on 2/28/2018.
 */

public class HLVAdapter extends RecyclerView.Adapter<HLVAdapter.ViewHolder> {

    ArrayList<RecipeModel> alRecipe;
    Context context;
    int isShadow;
    ImageFetcherNew imageFetcherNew;
    MySharedPreference mMySharedPreference;

    public HLVAdapter(Context context, ArrayList<RecipeModel> alRecipe, int isShadow, ImageFetcherNew imageFetcherNew) {
        super();
        this.context = context;
        this.alRecipe = alRecipe;
        this.isShadow=isShadow;
        this.imageFetcherNew=imageFetcherNew;
        mMySharedPreference=new MySharedPreference(context);
    }
    public HLVAdapter(Context context, ArrayList<RecipeModel> alRecipe) {
        super();
        this.context = context;
        this.alRecipe = alRecipe;
        this.isShadow=1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v ;
        if(isShadow==0){
            v =LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.grid_item_shadow, viewGroup, false);
        } else {
            v =LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.grid_item, viewGroup, false);
        }
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        try {
//            viewHolder.tvSpecies.setText(alRecipe.get(i).getTitle().replace("&","and").replace("#038;",""));
            viewHolder.tvSpecies.setText(Html.fromHtml(alRecipe.get(i).getTitle()));
            viewHolder.tvSpecies.setTag(R.id.RecipeId,alRecipe.get(i).getID());
            viewHolder.tvSpecies.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int id = (int) v.getTag(R.id.RecipeId);
                        Intent intent = new Intent(context, ItemDetailActivity.class);
                        intent.putExtra("id",id);
                        context.startActivity(intent);
                    }
                    catch(Exception ex){

                    }
                }
            });
            viewHolder.tvSpecies.setTypeface(Utility.SetTextFontRegular(context));
            Global.picassoWithCache.with(context)
                    .load(alRecipe.get(i).getImage()).into(viewHolder.imgThumbnail);
            imageFetcherNew.loadImage(alRecipe.get(i).getImage(),viewHolder.imgThumbnail);
            viewHolder.imgThumbnail.setTag(R.id.RecipeId,alRecipe.get(i).getID());
            viewHolder.imgThumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int id = (int) v.getTag(R.id.RecipeId);
                        Intent intent = new Intent(context, ItemDetailActivity.class);
                        intent.putExtra("id",id);
                        context.startActivity(intent);
                    }
                    catch(Exception ex){

                    }
                }
            });
            viewHolder.id = alRecipe.get(i).getID();
            viewHolder.iv_like.setTag(R.id.recipeModelId,alRecipe.get(i));

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

            viewHolder.iv_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(checkIfNet()) {
                        if (mMySharedPreference.isUserLogin()) {


                            RecipeModel item = (RecipeModel) v.getTag(R.id.recipeModelId);
                            int drawable = (int) v.getTag(R.id.likeImageId);
                            if (drawable == R.drawable.like_detail_icon) {
                                ImageView imgLike = v.findViewById(R.id.iv_like);
                                imgLike.setImageResource(R.drawable.liked_detail_icon);
                                imgLike.setTag(R.id.likeImageId, R.drawable.liked_detail_icon);
                                DataCenter.AddDeleteFavourite(context, "fav_recipe", item.getID());
                            } else {
                                ImageView imgLike = v.findViewById(R.id.iv_like);
                                imgLike.setImageResource(R.drawable.like_detail_icon);
                                imgLike.setTag(R.id.likeImageId, R.drawable.like_detail_icon);
                                DataCenter.AddDeleteFavourite(context, "delete_fav_recipe", item.getID());
                            }
                        } else {
                            Utility.showLoginLikeMessage(context);
                        }
                    } else {
                        Toast.makeText(context,R.string.stillNoConnection,Toast.LENGTH_SHORT).show();
                    }
                }
            });
            //viewHolder.imgThumbnail.setImageResource(alRecipe.get(i).getImage());


        }
        catch(Exception ex)
        {

        }
    }
    @Override
    public int getItemCount() {
        return alRecipe.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder  {

        public ImageView imgThumbnail;
        public TextView tvSpecies;
        private ItemClickListener clickListener;
        public ImageView iv_like;
        public ImageView iv_icon;
        public int id;

        public ViewHolder(View itemView) {
            super(itemView);
            imgThumbnail = (ImageView) itemView.findViewById(R.id.img_thumbnail);
            tvSpecies = (TextView) itemView.findViewById(R.id.tv_species);
            iv_like=(ImageView) itemView.findViewById(R.id.iv_like);
            iv_icon=(ImageView) itemView.findViewById(R.id.iv_icon);
//            itemView.setOnClickListener(this);
//            itemView.setOnLongClickListener(this);
        }

        public void setClickListener(ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }

//        @Override
//        public void onClick(View view) {
//            clickListener.onClick(view, getPosition(), false);
//        }
//
//        @Override
//        public boolean onLongClick(View view) {
//            clickListener.onClick(view, getPosition(), true);
//            return true;
//        }
    }
    public Boolean checkIfNet(){
        try {
            SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            Gson gson = new Gson();
            Boolean json = false;
            if (appSharedPrefs.contains("isNetworkConnection")) {
                json = appSharedPrefs.getBoolean("isNetworkConnection", false);

                if (json) {

                } else{
//                    Toast.makeText(currentActivity, "Connection Lost", Toast.LENGTH_LONG).show();


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
}