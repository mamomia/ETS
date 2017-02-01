package com.umt.ameer.ets.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.umt.ameer.ets.appdata.Constants;
import com.umt.ameer.ets.appdata.GlobalSharedPrefs;
import com.umt.ameer.ets.extras.RequestMethod;
import com.umt.ameer.ets.extras.RestClient;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Mushi on 1/28/2017.
 */

public class LocationReceiver extends BroadcastReceiver {
    private String TAG = this.getClass().getSimpleName();
    private LocationResult mLocationResult;

    @Override
    public void onReceive(Context context, Intent intent) {
        // Need to check and grab the Intent's extras like so
        new GlobalSharedPrefs(context);
        String emp_status = GlobalSharedPrefs.ETSPrefs.getString(Constants.EMP_STATUS_KEY, "none");
        if (LocationResult.hasResult(intent) && emp_status.equalsIgnoreCase("On Duty")) {
            this.mLocationResult = LocationResult.extractResult(intent);
            Log.i(TAG, "Location Received: " + this.mLocationResult.toString());
            Location loc = this.mLocationResult.getLastLocation();
            String user_id = GlobalSharedPrefs.ETSPrefs.getString(Constants.EMP_ID_KEY, "0");
            new LocationTask().execute(user_id, Double.toString(loc.getLatitude()), Double.toString(loc.getLongitude()));

            //check if user is outside radius
            double mRadiusRange = Double.parseDouble(GlobalSharedPrefs.ETSPrefs.getString(Constants.EMP_RADIUS_KEY, "0"));
            mRadiusRange = mRadiusRange * 1609.344;
            String temp = GlobalSharedPrefs.ETSPrefs.getString(Constants.EMP_RADIUS_CENTER_KEY, "0,0");
            String[] centerRad = temp.split(",");
            LatLng latLngCenter = new LatLng(Double.parseDouble(centerRad[0]), Double.parseDouble(centerRad[1]));
            CircleOptions circleOptions = new CircleOptions()
                    .center(latLngCenter)   //set center
                    .radius(mRadiusRange)   //set radius in meters
                    .fillColor(0x40ADFF2F)  //default
                    .strokeColor(Color.RED)
                    .strokeWidth(5);
            //checking the marker if it is in the radius
            float[] distance = new float[2];
            Location.distanceBetween(loc.getLatitude(), loc.getLongitude(),
                    circleOptions.getCenter().latitude, circleOptions.getCenter().longitude, distance);

            if (distance[0] > circleOptions.getRadius()) {
                Toast.makeText(context, "Employee is Outside radius", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "Employee is Inside radius", Toast.LENGTH_LONG).show();
            }
            //end of checking
        }
    }

    private class LocationTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(final String... params) {
            RestClient client = new RestClient(Constants.SEND_LOCATION_UPDATE_URL);
            client.AddParam("emp_id", params[0]);
            client.AddParam("emp_lat", params[1]);
            client.AddParam("emp_lng", params[2]);
            try {
                client.Execute(RequestMethod.GET);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String result = client.getResponse();
            Log.d("result", result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                final String status = jsonObject.getString("status");
                Log.d(TAG, "result" + status);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
