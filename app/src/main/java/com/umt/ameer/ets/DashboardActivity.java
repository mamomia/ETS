package com.umt.ameer.ets;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;
import com.umt.ameer.ets.appdata.GlobalSharedPrefs;
import com.umt.ameer.ets.broadcasts.BackgroundLocationService;
import com.umt.ameer.ets.fragments.DetailFragment;
import com.umt.ameer.ets.fragments.MapFragment;
import com.umt.ameer.ets.fragments.ProfileFragment;
import com.umt.ameer.ets.fragments.SubmissionFragment;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DashboardActivity extends AppCompatActivity {

    private String Subm, Form, Map, Profile, Details;
    private String TAG = "Dashboard Activity";
    private SpaceNavigationView spaceNavigationView;
    private static int mCurrentTabSelectedIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        ComponentName comp = new ComponentName(this.getPackageName(), BackgroundLocationService.class.getName());
        ComponentName service = startService(new Intent().setComponent(comp));
        if (service == null) {
            Toast.makeText(this, "Something went wrong, please turn on GPS.", Toast.LENGTH_LONG).show();
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
        }

        new GlobalSharedPrefs(this);
        Subm = "Orders";
        Form = "Form";
        Map = "Map";
        Details = "Products";
        Profile = "My Profile";

        spaceNavigationView = (SpaceNavigationView) findViewById(R.id.space);
        spaceNavigationView.initWithSaveInstanceState(savedInstanceState);
        spaceNavigationView.addSpaceItem(new SpaceItem(Subm, R.drawable.tab_subm));
        spaceNavigationView.addSpaceItem(new SpaceItem(Map, R.drawable.tab_map));
        spaceNavigationView.addSpaceItem(new SpaceItem(Details, R.drawable.tab_details));
        spaceNavigationView.addSpaceItem(new SpaceItem(Profile, R.drawable.tab_profile));
        spaceNavigationView.showIconOnly();
        spaceNavigationView.shouldShowFullBadgeText(true);

        spaceNavigationView.setCentreButtonIcon(R.drawable.tab_form);
        spaceNavigationView.setCentreButtonIconColor(ContextCompat.getColor(this, R.color.white));
        spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                Log.d("onCentreButtonClick ", "onCentreButtonClick");
                spaceNavigationView.shouldShowFullBadgeText(true);
                new SweetAlertDialog(DashboardActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("New Order")
                        .setContentText("Want to place a New Order?")
                        .setCancelText("No")
                        .setConfirmText("Yes, Place!")
                        .showCancelButton(true)
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                            }
                        }).setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                        Intent intent = new Intent(DashboardActivity.this, FormActivity.class);
                        startActivity(intent);
                        sweetAlertDialog.dismissWithAnimation();
                    }
                }).show();
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                Log.d("onItemClick ", "" + itemIndex + " " + itemName);
                mCurrentTabSelectedIndex = itemIndex;
                SwitchToSelectedTab(mCurrentTabSelectedIndex);
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
                Log.d("onItemReselected ", "" + itemIndex + " " + itemName);
                if (mCurrentTabSelectedIndex != itemIndex) {
                    mCurrentTabSelectedIndex = itemIndex;
                    SwitchToSelectedTab(mCurrentTabSelectedIndex);
                }
            }
        });

        mCurrentTabSelectedIndex = 0;
        SwitchToSelectedTab(mCurrentTabSelectedIndex);
    }

    private void SwitchToSelectedTab(int itemIndex) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        int count = manager.getBackStackEntryCount();
        for (int i = 0; i < count; ++i) {
            manager.popBackStack();
        }

        if (itemIndex == 0) {
            transaction.replace(R.id.listFragment, SubmissionFragment.newInstance());
        } else if (itemIndex == 1) {
            transaction.replace(R.id.listFragment, MapFragment.newInstance());
        } else if (itemIndex == 2) {
            transaction.replace(R.id.listFragment, DetailFragment.newInstance());
        } else if (itemIndex == 3) {
            transaction.replace(R.id.listFragment, ProfileFragment.newInstance());
        }
        transaction.commit();
    }
    // Check if there is any connectivity for a Wifi network

//    public boolean isConnectedWifi() {
//
//        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo info = connectivityManager.getNetworkInfo(Context);
//
//        if (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI) {
//            return true;
//        }
//        return false;
//    }
//
//    // Check if there is any connectivity for a mobile network
//    public boolean isConnectedMobile() {
//        NetworkInfo info = Connectivity.getNetworkInfo(Context);
//        if (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_MOBILE) {
//            return true;
//        }
//        return false;
//    }
//
//    // Check all connectivities whether available or not
//    public boolean isNetworkAvailable() {
//        ConnectivityManager cm = (ConnectivityManager)
//                getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
//        // if no network is available networkInfo will be null
//        // otherwise check if we are connected
//        if (networkInfo != null && networkInfo.isConnected()) {
//            return true;
//        }
//        return false;
//    }
}