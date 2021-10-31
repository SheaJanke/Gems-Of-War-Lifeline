package com.cowbraingames.optimalmatcher_gemsofwar.BoardDisplay;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.cowbraingames.optimalmatcher_gemsofwar.R;
import com.cowbraingames.optimalmatcher_gemsofwar.Utils.Constants;
import com.cowbraingames.optimalmatcher_gemsofwar.Utils.GemType;

public class BoardGridAdapter extends BaseAdapter {
    private final Context context;
    private final GemType[][] gems;
    private final boolean[][] selected;
    private final int imgSize;
    private final int BOARD_SIZE = Constants.BOARD_SIZE;
    public static Integer[] orbID = {
            R.drawable.skull,
            R.drawable.super_skull,
            R.drawable.fire,
            R.drawable.water,
            R.drawable.earth,
            R.drawable.ground,
            R.drawable.light,
            R.drawable.dark,
            R.drawable.block,
            R.drawable.lycanthropy,
            R.drawable.dark_potion,
            R.drawable.earth_potion,
            R.drawable.fire_potion,
            R.drawable.ground_potion,
            R.drawable.light_potion,
            R.drawable.water_potion,
            R.drawable.uber_doom_skull,
    };

    public BoardGridAdapter(Context context, GemType[][] gems, boolean[][] selected, int imgSize){
        this.context = context;
        this.gems = gems;
        this.selected = selected;
        this.imgSize = imgSize;
    }

    @Override
    public int getCount() {
        return BOARD_SIZE * BOARD_SIZE;
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
    public View getView(int position, View view, ViewGroup viewGroup) {
        ImageView imageView;
        if(view == null){
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(imgSize, imgSize));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }else{
            imageView = (ImageView) view;
        }

        setImageResource(imageView, position);

        return imageView;
    }

    private void setImageResource(ImageView imageView, int position) {
        if(selected[position/BOARD_SIZE][position%BOARD_SIZE]){
            imageView.setBackgroundResource(R.color.colorAccent);
        }
        GemType gemType = gems[position/BOARD_SIZE][position%BOARD_SIZE];
        imageView.setImageResource(Constants.getResource(gemType));
    }

}
