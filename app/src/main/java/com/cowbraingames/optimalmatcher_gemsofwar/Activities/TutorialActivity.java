package com.cowbraingames.optimalmatcher_gemsofwar.Activities;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.cowbraingames.optimalmatcher_gemsofwar.R;
import com.cowbraingames.optimalmatcher_gemsofwar.Tutorial.TutorialSliderAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TutorialActivity extends AppCompatActivity {

    private final int NUM_TUTORIAL_SCREENS = TutorialSliderAdapter.NUM_TUTORIAL_SCREENS;
    private TextView[] dots;
    private LinearLayout dotsLayout;
    private ViewPager2 tutorialSlider;
    private Button finishButton;
    private FloatingActionButton exitButton;
    private Context context;
    private int currentPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.tutorial_base);

        tutorialSlider = findViewById(R.id.tutorial_slider);
        dotsLayout = findViewById(R.id.dots_layout);
        finishButton = findViewById(R.id.tutorial_finish);
        exitButton = findViewById(R.id.tutorial_exit);

        TutorialSliderAdapter tutorialSliderAdapter = new TutorialSliderAdapter();
        tutorialSlider.setAdapter(tutorialSliderAdapter);
        tutorialSlider.setOffscreenPageLimit(3);

        buildDots();
        addPageSwitchListener();
        addNavigationClickListeners();
    }

    private void buildDots() {
        dots = new TextView[NUM_TUTORIAL_SCREENS];
        dotsLayout.removeAllViews();

        for(int i = 0; i < NUM_TUTORIAL_SCREENS; i++){
            dots[i] = new TextView(context);
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(40);
            dotsLayout.addView(dots[i]);
        }
        resetDisplay();
    }

    private void resetDisplay() {
        resetHighlightedDot();
        resetNavigationVisibility();
    }

    private void resetHighlightedDot() {
        for(int i = 0; i < NUM_TUTORIAL_SCREENS; i++){
            if(i == currentPage) {
                dots[i].setTextColor(getResources().getColor(R.color.colorAccent));
            }else{
                dots[i].setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            }
        }
    }

    private void resetNavigationVisibility() {
        if(isLastPage()){
            finishButton.setVisibility(View.VISIBLE);
        }else{
            finishButton.setVisibility(View.INVISIBLE);
        }
    }

    private void addPageSwitchListener() {
        tutorialSlider.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currentPage = position;
                resetDisplay();
            }
        });
    }

    private void addNavigationClickListeners() {
        finishButton.setOnClickListener(view -> finish());
        exitButton.setOnClickListener(view -> finish());
    }

    private boolean isLastPage() {
        return currentPage == NUM_TUTORIAL_SCREENS - 1;
    }
}
