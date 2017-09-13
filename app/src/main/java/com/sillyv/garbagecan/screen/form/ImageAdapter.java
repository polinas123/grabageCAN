package com.sillyv.garbagecan.screen.form;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sillyv.garbagecan.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vasili on 9/14/2017.
 *
 */

public class ImageAdapter
        extends RecyclerView.Adapter<ImageHolder> {

    private List<Uri> items;

    @Override
    public ImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_layout, parent, false);
        return new ImageHolder(v);
    }

    @Override
    public void onBindViewHolder(ImageHolder holder, int position) {
        holder.setData(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    void addPhoto(String mParam1) {
        if (items == null) {
            items = new ArrayList<>();
        }
        items.add(Uri.fromFile(new File(mParam1)));
    }
}
