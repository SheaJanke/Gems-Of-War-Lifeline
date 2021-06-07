package com.cowbraingames.optimalmatcher_gemsofwar;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private int[][] grid;
    private boolean[][] selcted;
    public static Integer[] orbID = {
            R.drawable.skull,
            R.drawable.super_skull,
            R.drawable.fire,
            R.drawable.water,
            R.drawable.earth,
            R.drawable.ground,
            R.drawable.light,
            R.drawable.dark,
            R.drawable.block
    };

    public ImageAdapter(Context c, int[][] grid, boolean[][] selected){
        mContext = c;
        this.grid = grid;
        this.selcted = selected;
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
        if(i == -1){
            return imageView;
        }
        if(selcted[i/8][i%8]){
            imageView.setBackgroundResource(R.color.colorAccent);
        }
        int orbType = grid[i/8][i%8];
        imageView.setImageResource(orbType == -1 ? R.drawable.unknown : orbID[orbType]);
        return imageView;
    }

}
