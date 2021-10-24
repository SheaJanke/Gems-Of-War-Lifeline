package com.cowbraingames.optimalmatcher_gemsofwar.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.cowbraingames.optimalmatcher_gemsofwar.R;
import com.cowbraingames.optimalmatcher_gemsofwar.Tutorial.TutorialSliderAdapter;

public class TutorialActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial_base);

        ViewPager2 tutorialSlider = findViewById(R.id.tutorial_slider);
        TutorialSliderAdapter tutorialSliderAdapter = new TutorialSliderAdapter();
        tutorialSlider.setAdapter(tutorialSliderAdapter);

        tutorialSlider.setOffscreenPageLimit(3);
    }
}
