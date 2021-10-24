package com.cowbraingames.optimalmatcher_gemsofwar.Tutorial;

import android.app.Dialog;
import android.content.Context;
import android.widget.Button;

import com.cowbraingames.optimalmatcher_gemsofwar.R;

import java.security.InvalidParameterException;

public class TutorialManager {

    private final int START_PAGE = 1;
    private final Context context;
    private Dialog tutorialDialog;

    public TutorialManager(Context context) {
        this.context = context;
    }

    public void startTutorial() {
        tutorialDialog = new Dialog(context);
        tutorialDialog.setCancelable(true);
        setDialogContent(START_PAGE);
        tutorialDialog.show();
    }

    private void setDialogContent(int pageNum) {
        try {
            int contentID = getContentID(pageNum);
            tutorialDialog.setContentView(contentID);
            addClickListenersToButtons(pageNum+1);
        } catch (IllegalArgumentException e) {
            tutorialDialog.dismiss();
        }
    }

    private int getContentID(int pageNum) {
        switch (pageNum) {
            case 1:
                return R.layout.tutorial_1;
            case 2:
                return R.layout.tutorial_2;
        }
        throw new InvalidParameterException();
    }

    private void addClickListenersToButtons(int nextPageNum) {
        Button nextButton = tutorialDialog.findViewById(R.id.tutorial_next);
        Button skipButton = tutorialDialog.findViewById(R.id.tutorial_skip);

        nextButton.setOnClickListener(view -> setDialogContent(nextPageNum));
        skipButton.setOnClickListener(view -> tutorialDialog.dismiss());
    }
}
