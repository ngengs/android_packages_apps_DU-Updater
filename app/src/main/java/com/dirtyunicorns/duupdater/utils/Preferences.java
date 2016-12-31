package com.dirtyunicorns.duupdater.utils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;

import com.dirtyunicorns.duupdater.R;

/**
 * Created by mazda on 12/30/16.
 */
public class Preferences {

    public static final String NavigationTint = "NavigationTint";

    public static void themeMe(Activity activity) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);

        if (sharedPreferences.getBoolean(NavigationTint, false)) {
            activity.getWindow().setNavigationBarColor(ContextCompat.getColor(activity, (R.color.navigation_bar_color_tint_enabled)));
        } else {
            activity.getWindow().setNavigationBarColor(ContextCompat.getColor(activity, (R.color.navigation_bar_color_tint_disabled)));
        }
    }
}