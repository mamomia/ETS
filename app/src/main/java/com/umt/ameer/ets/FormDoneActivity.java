package com.umt.ameer.ets;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.umt.ameer.ets.appdata.GlobalSharedPrefs;

public class FormDoneActivity extends AppCompatActivity {

    private String mCurrentOrderId = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_done);

        new GlobalSharedPrefs(this);
        mCurrentOrderId = getIntent().getStringExtra("order_id");

        //after order things
        findViewById(R.id.tvFinishCloseForm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        findViewById(R.id.layoutViewOrderForm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FormDoneActivity.this, ShowOrderedFormActivity.class);
                intent.putExtra("order_id", mCurrentOrderId);
                startActivity(intent);
                finish();
            }
        });
    }
}
