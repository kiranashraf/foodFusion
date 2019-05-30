package com.foodfusion.foodfusion.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import com.foodfusion.foodfusion.Listeners.FilterClickListener;
import com.foodfusion.foodfusion.Model.FilterModel;
import com.foodfusion.foodfusion.R;

import java.util.ArrayList;

/**
 * Created by Rameez on 3/29/2018.
 */

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterViewHolder>
{
    ArrayList<FilterModel> alRecipe;
    Context context;
    private FilterClickListener mListener;

    public FilterAdapter(Context context, ArrayList<FilterModel> alRecipe, FilterClickListener listener) {
        super();
        this.context = context;
        this.alRecipe = alRecipe;
        this.mListener = listener;
    }
public void unToggleAll(){
    for(int i=0;i<alRecipe.size();i++){
        alRecipe.get(i).setChecked(false);
    }
    notifyDataSetChanged();
}
    @Override
    public FilterAdapter.FilterViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.viewholder_filter, viewGroup, false);

        final FilterAdapter.FilterViewHolder viewHolder = new FilterAdapter.FilterViewHolder(v, mListener);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final FilterAdapter.FilterViewHolder viewHolder, int i) {
        try {
            viewHolder.toggleButton.setText(Html.fromHtml(alRecipe.get(i).getCategory()));
            viewHolder.toggleButton.setTextOff(Html.fromHtml(alRecipe.get(i).getCategory()));
            viewHolder.toggleButton.setTextOn(Html.fromHtml(alRecipe.get(i).getCategory()));
            viewHolder.toggleButton.setChecked(alRecipe.get(i).getChecked());
//            viewHolder.bindTo(alRecipe.get(i).getCategory());
//            ViewGroup.LayoutParams lp = viewHolder.toggleButton.getLayoutParams();
//            if (lp instanceof FlexboxLayoutManager.LayoutParams) {
//                FlexboxLayoutManager.LayoutParams flexboxLp = (FlexboxLayoutManager.LayoutParams) lp;
//                flexboxLp.setFlexGrow(1.0f);
//                flexboxLp.setAlignSelf(AlignSelf.FLEX_END);
//            }
//            viewHolder.tvSpecies.setTag(R.id.RecipeId,alRecipe.get(i).getID());
//            viewHolder.tvSpecies.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    try {
//                        int id = (int) v.getTag(R.id.RecipeId);
//                        Intent intent = new Intent(context, ItemDetailActivity.class);
//                        intent.putExtra("id",id);
//                        context.startActivity(intent);
//                    }
//                    catch(Exception ex){
//
//                    }
//                }
//            });
//            viewHolder.tvSpecies.setTypeface(Utility.SetTextFontRegular(context));
//            Picasso.with(context)
//                    .load(alRecipe.get(i).getImage()).into(viewHolder.imgThumbnail);
//            viewHolder.imgThumbnail.setTag(R.id.RecipeId,alRecipe.get(i).getID());
//            viewHolder.imgThumbnail.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    try {
//                        int id = (int) v.getTag(R.id.RecipeId);
//                        Intent intent = new Intent(context, ItemDetailActivity.class);
//                        intent.putExtra("id",id);
//                        context.startActivity(intent);
//                    }
//                    catch(Exception ex){
//
//                    }
//                }
//            });
            viewHolder.id = alRecipe.get(i).getId();

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

    public static class FilterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ToggleButton toggleButton;
        public int id;
        private FilterClickListener mListener;

        public FilterViewHolder(View itemView, FilterClickListener listener) {
            super(itemView);
            toggleButton = (ToggleButton) itemView.findViewById(R.id.tgl_btn);
            mListener = listener;
            itemView.setOnClickListener(this);
            toggleButton.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            mListener.onClick(view, getAdapterPosition());
        }
        void bindTo(String drawable) {
            toggleButton.setText(drawable);
//            ViewGroup.LayoutParams lp = toggleButton.getLayoutParams();
//            if (lp instanceof FlexboxLayoutManager.LayoutParams) {
//                FlexboxLayoutManager.LayoutParams flexboxLp = (FlexboxLayoutManager.LayoutParams)lp;
//                flexboxLp.setFlexGrow(1.0f);        }
        }
    }
    }

