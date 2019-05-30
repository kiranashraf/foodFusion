package com.foodfusion.foodfusion.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.foodfusion.foodfusion.R;

/**
 * Created by Rameez on 2/28/2018.
 */

public class MyViewHolder extends RecyclerView.ViewHolder {

    public TextView titleTextView;
    public ImageView coverImageView;
    public ImageView likeImageView;
    public ImageView shareImageView;

    public MyViewHolder(View v) {
        super(v);
        titleTextView = (TextView) v.findViewById(R.id.titleTextView);
        coverImageView = (ImageView) v.findViewById(R.id.coverImageView);
        likeImageView = (ImageView) v.findViewById(R.id.likeImageView);
        shareImageView = (ImageView) v.findViewById(R.id.shareImageView);
        likeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = (int)likeImageView.getTag();
                if( id == R.drawable.all_categories_icon){

                    likeImageView.setTag(R.drawable.all_categories_icon);
                    likeImageView.setImageResource(R.drawable.all_categories_icon);

                    //Toast.makeText(this.getActivity(),titleTextView.getText()+"added to favourites",Toast.LENGTH_SHORT).show();

                }else{
                    likeImageView.setTag(R.drawable.all_categories_icon);
                    likeImageView.setImageResource(R.drawable.all_categories_icon);
                   // Toast.makeText(getActivity(),titleTextView.getText()+"removed from favourites",Toast.LENGTH_SHORT).show();
                }
            }
        });

        shareImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
//                        "://" + getResources().getResourcePackageName(coverImageView.getId())
//                        + '/' + "drawable" + '/' +
//                        getResources().getResourceEntryName((int)coverImageView.getTag()));
//                Intent shareIntent = new Intent();
//                shareIntent.setAction(Intent.ACTION_SEND);
//                shareIntent.putExtra(Intent.EXTRA_STREAM,imageUri);
//                shareIntent.setType("image/jpeg");
//                startActivity(Intent.createChooser
//                        (shareIntent, getResources().getText(R.string.send_to)));
            }
        });
    }
}
