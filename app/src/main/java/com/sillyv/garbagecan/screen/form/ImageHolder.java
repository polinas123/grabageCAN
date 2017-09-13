package com.sillyv.garbagecan.screen.form;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.sillyv.garbagecan.R;
import com.squareup.picasso.Picasso;

/**
 * Created by Vasili on 9/14/2017.
 *
 */

class ImageHolder
        extends RecyclerView.ViewHolder {

    private ImageView imageView;


    ImageHolder(View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.image_item);
    }

    public void setData(Uri data) {
        Picasso.with(imageView.getContext()).load(data).into(imageView);

    }
}
