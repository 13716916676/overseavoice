package com.example.hotwordsample;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreUtils {
    public static final String KEY_ISSHOW_DIALOG = "dialogshow";

    private final String SPNAME = "sample";
    SharedPreferences sp ;
    private static SharedPreUtils spUtils;
    private SharedPreUtils(Context context){
        sp = context.getSharedPreferences(SPNAME, Context.MODE_PRIVATE);
    }

    public static SharedPreUtils getInstance(Context context){
        if(spUtils == null)
            spUtils = new SharedPreUtils(context);
        return spUtils;
    }

    public void commitIntValue(String key, int value){
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public int getIntValue(String key){
        return sp.getInt(key, 0);
    }

    public void commitStringValue(String key, String value){
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getStringValue(String key){
        return sp.getString(key, null);
    }

    public boolean getBooleanValue(String key, boolean defaultValue){
        return sp.getBoolean(key, defaultValue);
    }

    public void commitBooleanValue(String key, boolean value){
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

}
