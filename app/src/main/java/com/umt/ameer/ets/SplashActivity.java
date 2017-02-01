package com.umt.ameer.ets;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.umt.ameer.ets.appdata.SessionManager;
import com.umt.ameer.ets.extras.CircleProgressBar;
import com.umt.ameer.ets.extras.LoadingTask;

public class SplashActivity extends AppCompatActivity implements LoadingTask.LoadingTaskFinishedListener {

    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        session = new SessionManager(this);
        CircleProgressBar circleProgressBar = (CircleProgressBar) findViewById(R.id.custom_progressBar);
        new LoadingTask(circleProgressBar, this).execute();
    }

    @Override
    public void onTaskFinished() {
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
    }
}
