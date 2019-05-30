package com.foodfusion.foodfusion.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.foodfusion.foodfusion.DB.MySharedPreference;
import com.foodfusion.foodfusion.ImageCache.ImageFetcherNew;
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

public class GridAdapter extends BaseAdapter {

    ArrayList<RecipeModel> alRecipe;
    Context context;
    LayoutInflater inflater;
    private ImageFetcherNew mImageFetcherNew;
    MySharedPreference mMySharedPreference;
    public GridAdapter(Context context, ArrayList<RecipeModel> alRecipe,ImageFetcherNew mImageFetcherNew) {
        super();
        this.context = context;
        this.alRecipe = alRecipe;
        this.mImageFetcherNew=mImageFetcherNew;
        mMySharedPreference=new MySharedPreference(context);
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
    public ArrayList<RecipeModel> getAll() {
        return alRecipe;
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

      //  viewHolder.tvSpecies.setText(alRecipe.get(i).getTitle().replace("&","and").replace("#038;",""));
    viewHolder.tvSpecies.setText(Html.fromHtml(alRecipe.get(i).getTitle()));
        viewHolder.tvSpecies.setTypeface(Utility.SetTextFontRegular(context));
//    Global.picassoWithCache.with(context)
//                .load(alRecipe.get(i).getImage()).into(viewHolder.imgThumbnail);
    mImageFetcherNew.loadImage(alRecipe.get(i).getImage(),viewHolder.imgThumbnail);
        viewHolder.id = alRecipe.get(i).getID();
        if(alRecipe.get(i).getIsFavourite()==1){
            viewHolder.iv_like.setImageResource(R.drawable.liked_detail_icon);
            viewHolder.iv_like.setTag(R.id.likeImageId,R.drawable.liked_detail_icon);
        } else {
            viewHolder.iv_like.setImageResource(R.drawable.like_detail_icon);
            viewHolder.iv_like.setTag(R.id.likeImageId,R.drawable.like_detail_icon);
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