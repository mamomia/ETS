package com.umt.ameer.ets.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.util.Log;

import com.umt.ameer.ets.appdata.Constants;
import com.umt.ameer.ets.appdata.GlobalSharedPrefs;
import com.umt.ameer.ets.networkmodels.SimpleResponse;
import com.umt.ameer.ets.rest.ApiClient;
import com.umt.ameer.ets.rest.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Mushi on 1/27/2017.
 */

/**
 * The manifest Receiver is used to detect changes in battery state.
 * When the system broadcasts a "Battery Low" warning we turn off
 * the passive location updates to conserve battery when the app is
 * in the background.
 * <p>
 * When the system broadcasts "Battery OK" to indicate the battery
 * has returned to an okay state, the passive location updates are
 * resumed.
 */
public class BatteryStateChangedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean batteryLow = intent.getAction().equals(Intent.ACTION_BATTERY_LOW);

        new GlobalSharedPrefs(context);
        String emp_status = GlobalSharedPrefs.ETSPrefs.getString(Constants.EMP_STATUS_KEY, "none");
        if (batteryLow && emp_status.equalsIgnoreCase("On Duty")) {

            StringBuilder sb = new StringBuilder();
            sb.append("status:BATTERY_LOW\n");

            int status;
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, ifilter);
            status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            String msg;
            switch (status) {
                case BatteryManager.BATTERY_STATUS_CHARGING:
                    msg = "BATTERY_STATUS_CHARGING\n";
                    break;
                case BatteryManager.BATTERY_STATUS_DISCHARGING:
                    msg = "BATTERY_STATUS_DISCHARGING\n";
                    break;
                case BatteryManager.BATTERY_STATUS_FULL:
                    msg = "BATTERY_STATUS_FULL\n";
                    break;
                case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                    msg = "BATTERY_STATUS_NOT_CHARGING\n";
                    break;
                case BatteryManager.BATTERY_STATUS_UNKNOWN:
                    msg = "BATTERY_STATUS_UNKNOWN\n";
                    break;
                default:
                    msg = "Unknown!\n";
            }
            sb.append("message:" + msg + "\n");
            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int health = batteryStatus.getIntExtra(BatteryManager.EXTRA_HEALTH, 0);

            sb.append("battery_LEVEL:" + level + "\n");
            sb.append("battery_SCALE:" + scale + "\n");
            sb.append("battery_HEALTH:" + health + "\n");
            String strContent = sb.toString();

            String user_id = GlobalSharedPrefs.ETSPrefs.getString(Constants.EMP_ID_KEY, "0");
            new NotificationTask().execute(user_id, strContent);
        }
    }

    private class NotificationTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(final String... params) {

            final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<SimpleResponse> infoCall = apiService.sendNotificationRequest(params[0], params[1], "employee");
            infoCall.enqueue(new Callback<SimpleResponse>() {
                @Override
                public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                    Log.e("LocationTask", response.body().getMessage());
                }

                @Override
                public void onFailure(Call<SimpleResponse> call, Throwable t) {
                    // Log error here since request failed
                    Log.e("LocationTask", t.toString());
                }
            });
            return null;
        }
    }
}
