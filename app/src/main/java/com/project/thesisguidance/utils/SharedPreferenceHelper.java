package com.project.thesisguidance.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceHelper {

    public static void putString(Context context, String key, String value){
        SharedPreferences pref = context.getSharedPreferences(Constant.MY_PREFERENCES,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString(key, value);
        editor.apply();
    }

    public static String getString(Context context, String key){
        SharedPreferences pref = context.getSharedPreferences(Constant.MY_PREFERENCES,Context.MODE_PRIVATE);
        return pref.getString(key, "");
    }

    public static void removeString(Context context, String key){
        SharedPreferences pref = context.getSharedPreferences(Constant.MY_PREFERENCES,Context.MODE_PRIVATE);
        pref.edit().remove(key).apply();
    }
}
