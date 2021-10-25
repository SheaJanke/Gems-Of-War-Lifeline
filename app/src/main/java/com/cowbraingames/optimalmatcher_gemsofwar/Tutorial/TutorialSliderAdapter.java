package com.cowbraingames.optimalmatcher_gemsofwar.Tutorial;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cowbraingames.optimalmatcher_gemsofwar.R;

public class TutorialSliderAdapter extends RecyclerView.Adapter<TutorialSliderAdapter.TutorialViewHolder> {

    public static final int NUM_TUTORIAL_SCREENS = 5;
    private final int[] headers = {R.string.tutorial_1_title, R.string.tutorial_2_title, R.string.tutorial_3_title, R.string.tutorial_4_title, R.string.tutorial_5_title};
    private final int[] descriptions = {R.string.tutorial_1_text, R.string.tutorial_2_text, R.string.tutorial_3_text, R.string.tutorial_4_text, R.string.tutorial_5_text };
    private final int[] images = {R.drawable.tutorial_image_1, R.drawable.tutorial_image_2, R.drawable.tutorial_image_3, R.drawable.tutorial_image_4, R.drawable.tutorial_image_5};

    public TutorialSliderAdapter() {

    }

    @NonNull
    @Override
    public TutorialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.tutorial_slide, parent, false);
        return new TutorialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TutorialViewHolder holder, int position) {
        holder.tutorialHeader.setText(headers[position]);
        holder.tutorialDescription.setText(descriptions[position]);
        holder.tutorialImage.setImageResource(images[position]);
    }

    @Override
    public int getItemCount() {
        return NUM_TUTORIAL_SCREENS;
    }

    public class TutorialViewHolder extends RecyclerView.ViewHolder {
        TextView tutorialHeader, tutorialDescription;
        ImageView tutorialImage;

        public TutorialViewHolder(@NonNull View itemView) {
            super(itemView);
            tutorialHeader = itemView.findViewById(R.id.tutorial_header);
            tutorialDescription = itemView.findViewById(R.id.tutorial_description);
            tutorialImage = itemView.findViewById(R.id.tutorial_image);
        }
    }
}
