package com.cowbraingames.optimalmatcher_gemsofwar.Storage;

import android.content.Context;
import android.content.SharedPreferences;

public class LocalStorageManager {
    private static final String PREFERENCES_NAME = "GoW_PREFERENCES";
    private static final String AUTO_CROP_KEY = "AUTO-CROP";
    private static final String TUTORIAL_COMPLETED_KEY = "TUTORIAL-COMPLETED";

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public static boolean getAutoCropPreference(Context context) {
        SharedPreferences preferences = getPreferences(context);
        return preferences.getBoolean(AUTO_CROP_KEY, true);
    }

    public static void setAutoCropPreference(Context context, Boolean autoCropPreference) {
        SharedPreferences preferences = getPreferences(context);
        preferences.edit().putBoolean(AUTO_CROP_KEY, autoCropPreference).apply();
    }

    public static boolean getTutorialCompleted(Context context) {
        SharedPreferences preferences = getPreferences(context);
        return preferences.getBoolean(TUTORIAL_COMPLETED_KEY, false);
    }

    public static void setTutorialCompleted(Context context, Boolean tutorialCompleted) {
        SharedPreferences preferences = getPreferences(context);
        preferences.edit().putBoolean(TUTORIAL_COMPLETED_KEY, tutorialCompleted).apply();
    }

}
