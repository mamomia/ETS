package com.umt.ameer.ets;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.umt.ameer.ets.appdata.Constants;
import com.umt.ameer.ets.appdata.GlobalSharedPrefs;
import com.umt.ameer.ets.appdata.SessionManager;
import com.umt.ameer.ets.extras.LoadingTask;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity implements LoadingTask.LoadingTaskFinishedListener {

    SessionManager session;
    private String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new GlobalSharedPrefs(this);
        session = new SessionManager(this);

        if (GlobalSharedPrefs.ETSPrefs.getBoolean(Constants.KEY_INITIAL_LAUNCH, false) || session.isLoggedIn()) {
            Log.e(TAG, "Not First Launch");
            new LoadingTask(this, this).execute();
        } else {
            Log.e(TAG, "First Launch");
            //First Time App launched, you are putting isInitialAppLaunch to false and calling create password activity.
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    GlobalSharedPrefs.ETSPrefs.edit().putBoolean(Constants.KEY_INITIAL_LAUNCH, true).apply();
                    Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            }, 2000);
        }
    }

    // This is the callback for when your async task has finished
    @Override
    public void onTaskFinished(boolean status) {
        if (status) {
            if (session.isLoggedIn()) {
                Intent in = new Intent(this, DashboardActivity.class);
                startActivityForResult(in, 0);
                overridePendingTransition(R.anim.lefttoright, R.anim.stable);
                finish();
            } else {
                Intent in = new Intent(this, LoginActivity.class);
                startActivityForResult(in, 0);
                overridePendingTransition(R.anim.lefttoright, R.anim.stable);
                finish();
            }
        } else
            finish();
    }
}
