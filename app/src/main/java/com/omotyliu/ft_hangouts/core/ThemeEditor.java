package com.omotyliu.ft_hangouts.core;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import com.omotyliu.ft_hangouts.R;

public class ThemeEditor
{


    public static int getTheme(Activity ctx)
    {
        return isDarkTheme(ctx) ? R.style.AppTheme : R.style.AppTheme2;
    }


    public static void setLight(Activity ctx)
    {
        saveTheme(ctx, false);
    }

    public static void setDark(Activity ctx)
    {
        saveTheme(ctx, true);
    }



    private static void saveTheme(Activity ctx, boolean flag)
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("dark", flag);
        editor.commit();
    }


    private static boolean isDarkTheme(Activity ctx)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getBoolean("dark", true);
    }

    public static void saveTimeFlag(Activity ctx, boolean flag)
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("time", flag);
        editor.commit();
    }


    public static boolean isTimeFlagEnabled(Activity ctx)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getBoolean("time", false);
    }
}
