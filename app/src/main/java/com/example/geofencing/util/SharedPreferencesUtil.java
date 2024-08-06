package com.example.geofencing.util;



import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPreferencesUtil {
    private SharedPreferences sp;
    private SharedPreferences.Editor spEditor;
    private Context ctx;

    public SharedPreferencesUtil(Context ctx) {
        this.ctx = ctx;
    }

    public void setPref(String key, String value,Context context){
        sp = PreferenceManager.getDefaultSharedPreferences(context);
        spEditor = sp.edit();
        spEditor.putString(key,value);
        spEditor.apply();
    }

    public String getPref(String key, Context context){
        sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(key,null);
    }
}
