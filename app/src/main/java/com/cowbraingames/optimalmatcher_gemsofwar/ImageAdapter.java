package com.cowbraingames.optimalmatcher_gemsofwar;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    public Integer[] orbID = {
            R.drawable.super_skull,
            R.drawable.skull,
            R.drawable.fire,
            R.drawable.water,
            R.drawable.earth,
            R.drawable.light,
            R.drawable.dark
    };

    public Integer[] highlightedOrbID = {
            R.drawable.super_skull_h,
            R.drawable.skull_h,
            R.drawable.fire_h,
            R.drawable.water_h,
            R.drawable.earth_h,
            R.drawable.light_h,
            R.drawable.dark_h
    };

    public ImageAdapter(Context c){
        mContext = c;
    }

    @Override
    public int getCount() {
        return 64;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ImageView imageView;
        if(view == null){
            imageView = new ImageView(mContext);
            int height = viewGroup.getLayoutParams().height/8;
            int width = viewGroup.getLayoutParams().width/8;
            imageView.setLayoutParams(new GridView.LayoutParams(width, height));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }else{
            imageView = (ImageView) view;
        }
        imageView.setImageResource(orbID[i%orbID.length]);
        return imageView;
    }

}