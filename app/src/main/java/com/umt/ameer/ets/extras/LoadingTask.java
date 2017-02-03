package com.umt.ameer.ets.extras;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.umt.ameer.ets.appdata.Constants;
import com.umt.ameer.ets.appdata.GlobalSharedPrefs;
import com.umt.ameer.ets.networkmodels.UserInfoResponse;
import com.umt.ameer.ets.rest.ApiClient;
import com.umt.ameer.ets.rest.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Ameer on 2/27/2016.
 */
public class LoadingTask extends AsyncTask<String, Integer, Integer> {

    // This is the listener that will be told when this task is finished
    private final LoadingTaskFinishedListener finishedListener;
    private String TAG = "LoadingTask";
    private String mUserId = "";
    private Context mContext;

    public LoadingTask(LoadingTaskFinishedListener finishedListener, Context context) {
        this.finishedListener = finishedListener;
        this.mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mUserId = GlobalSharedPrefs.ETSPrefs.getString(Constants.EMP_ID_KEY, "0");
    }

    @Override
    protected Integer doInBackground(String... params) {

        final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<UserInfoResponse> infoCall = apiService.getUserInfo(mUserId);
        infoCall.enqueue(new Callback<UserInfoResponse>() {
            @Override
            public void onResponse(Call<UserInfoResponse> call, Response<UserInfoResponse> response) {
                if (response.body().getStatus().equalsIgnoreCase("success")) {
                    
                    GlobalSharedPrefs.ETSPrefs.edit().putString(Constants.EMP_ID_KEY, response.body().getUserInfo().getId()).apply();
                    GlobalSharedPrefs.ETSPrefs.edit().putString(Constants.EMP_NAME_KEY,
                            response.body().getUserInfo().getEmpName()).apply();
                    GlobalSharedPrefs.ETSPrefs.edit().putString(Constants.EMP_EMAIL_KEY,
                            response.body().getUserInfo().getEmail()).apply();
                    GlobalSharedPrefs.ETSPrefs.edit().putString(Constants.EMP_PHONE_KEY,
                            response.body().getUserInfo().getPhoneNo()).apply();
                    GlobalSharedPrefs.ETSPrefs.edit().putString(Constants.EMP_JOIN_DATE_KEY,
                            response.body().getUserInfo().getJoinDate()).apply();
                    GlobalSharedPrefs.ETSPrefs.edit().putString(Constants.EMP_STATUS_KEY,
                            response.body().getUserInfo().getEmpStatus()).apply();
                    GlobalSharedPrefs.ETSPrefs.edit().putString(Constants.EMP_DP_KEY,
                            response.body().getUserInfo().getEmpDp()).apply();
                    GlobalSharedPrefs.ETSPrefs.edit().putString(Constants.EMP_SUPERIOR_ID_KEY,
                            response.body().getUserInfo().getEmployeeId()).apply();
                    GlobalSharedPrefs.ETSPrefs.edit().putString(Constants.EMP_STATUS_BREAK_CONTENT_KEY,
                            response.body().getUserInfo().getBreakContent()).apply();
                    GlobalSharedPrefs.ETSPrefs.edit().putString(Constants.EMP_RADIUS_KEY,
                            response.body().getArea().getRadius()).apply();
                    GlobalSharedPrefs.ETSPrefs.edit().putString(Constants.EMP_RADIUS_CENTER_KEY,
                            response.body().getArea().getCenterPoint()).apply();

                    finishedListener.onTaskFinished(true);
                } else {
                    finishedListener.onTaskFinished(false);
                }
            }

            @Override
            public void onFailure(Call<UserInfoResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
                Toast.makeText(mContext, "There was a problem connecting to server, Please try again", Toast.LENGTH_LONG).show();
                finishedListener.onTaskFinished(false); // Tell whoever was listening we have finished
            }
        });

        // Perhaps you want to return something to your post execute
        return 1234;
    }

    public interface LoadingTaskFinishedListener {
        void onTaskFinished(boolean status); // If you want to pass something back to the listener add a param to this method
    }
}
