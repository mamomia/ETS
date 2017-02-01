package com.umt.ameer.ets.extras;

import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Ameer on 11/4/2016.
 */

public class CustomButtonBaseActivity extends AppCompatActivity {
    public int dimen(@DimenRes int resId) {
        return (int) getResources().getDimension(resId);
    }

    public int color(@ColorRes int resId) {
        return getResources().getColor(resId);
    }

    public int integer(@IntegerRes int resId) {
        return getResources().getInteger(resId);
    }
}
