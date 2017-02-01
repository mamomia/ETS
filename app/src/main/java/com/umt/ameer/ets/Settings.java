package com.umt.ameer.ets;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

/**
 * Created by Ameer on 3/5/2016.
 */
public class Settings extends Activity{

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar_settings);

        toolbar.setTitle("Settings");
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        toolbar.setNavigationIcon(R.drawable.img_back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
