package com.umt.ameer.ets.appdata;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Ameer on 3/12/2016.
 */
public class GlobalSharedPrefs {
    private static final String PREF_NAME = "ETSSharedPrefs";
    public static SharedPreferences ETSPrefs;

    public GlobalSharedPrefs(Context con) {
        ETSPrefs = con.getSharedPreferences(PREF_NAME, 0);
    }
}
