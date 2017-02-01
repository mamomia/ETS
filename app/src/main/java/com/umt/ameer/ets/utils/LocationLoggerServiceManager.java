package com.umt.ameer.ets.utils;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.umt.ameer.ets.appdata.GlobalSharedPrefs;
import com.umt.ameer.ets.broadcasts.BackgroundLocationService;

/**
 * Created by Mushi on 1/28/2017.
 */

public class LocationLoggerServiceManager extends BroadcastReceiver {

    public static final String TAG = "LLoggerServiceManager";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Make sure we are getting the right intent
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            new GlobalSharedPrefs(context);
            String emp_status = GlobalSharedPrefs.ETSPrefs.getString("emp_status", "none");

            if (emp_status.equalsIgnoreCase("On Duty")) {
                ComponentName comp = new ComponentName(context.getPackageName(), BackgroundLocationService.class.getName());
                ComponentName service = context.startService(new Intent().setComponent(comp));

                if (null == service) {
                    // something really wrong here
                    Log.e(TAG, "Could not start service " + comp.toString());
                }
            }

        } else {
            Log.e(TAG, "Received unexpected intent " + intent.toString());
        }
    }
}
