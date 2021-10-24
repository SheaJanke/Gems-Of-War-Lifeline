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

    int[] headers = {R.string.tutorial_1_title, R.string.tutorial_1_title, R.string.tutorial_1_title};
    int[] descriptions = {R.string.tutorial_1_text, R.string.tutorial_1_text, R.string.tutorial_1_text};
    int[] images = {R.drawable.tutorial_image_1, R.drawable.tutorial_image_2, R.drawable.tutorial_image_3};

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
        return 3;
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
