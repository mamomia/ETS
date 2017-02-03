package com.umt.ameer.ets.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.soundcloud.android.crop.Crop;
import com.umt.ameer.ets.LoginActivity;
import com.umt.ameer.ets.R;
import com.umt.ameer.ets.appdata.Constants;
import com.umt.ameer.ets.appdata.GlobalSharedPrefs;
import com.umt.ameer.ets.appdata.SessionManager;
import com.umt.ameer.ets.extras.CircularImageViewSingle;
import com.umt.ameer.ets.extras.RequestMethod;
import com.umt.ameer.ets.extras.RestClient;
import com.umt.ameer.ets.networkmodels.SimpleResponse;
import com.umt.ameer.ets.rest.ApiClient;
import com.umt.ameer.ets.rest.ApiInterface;

import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import fr.ganfra.materialspinner.MaterialSpinner;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Ameer on 10/9/2016.
 */

public class ProfileFragment extends Fragment {

    private static final String TAG = "Settings Fragment";
    private static String mUserId = "";

    CircularImageViewSingle imgProfilePic;

    public ProfileFragment() {
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        new GlobalSharedPrefs(getContext());
        mUserId = GlobalSharedPrefs.ETSPrefs.getString(Constants.EMP_ID_KEY, "0");

        imgProfilePic = (CircularImageViewSingle) view.findViewById(R.id.profile_image);
        view.findViewById(R.id.btnProfilePicChange).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Crop.pickImage(getActivity(), ProfileFragment.this);
            }
        });

        TextView tvName = (TextView) view.findViewById(R.id.profile_name);
        TextView tvEmail = (TextView) view.findViewById(R.id.profile_email);
        TextView tvPhone = (TextView) view.findViewById(R.id.profile_phone);
        TextView tvJoinDate = (TextView) view.findViewById(R.id.profile_join_date);

        MaterialSpinner mSpinnerStatus = (MaterialSpinner) view.findViewById(R.id.spinnerStatus);
        List<String> list = new ArrayList<>();
        list.add("On Duty");
        list.add("Off Duty");
        list.add("On Break");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerStatus.setAdapter(dataAdapter);
        mSpinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                switch (position) {
                    case 0:
                        new StatusService().execute(mUserId, item, "none");
                        break;
                    case 1:
                        new StatusService().execute(mUserId, item, "none");
                        break;
                    case 2:
                        BreakStatusDialog();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        String defaultString = "Unavailable";

        String status = GlobalSharedPrefs.ETSPrefs.getString(Constants.EMP_STATUS_KEY, defaultString);
        if (status.equalsIgnoreCase(list.get(0)))
            mSpinnerStatus.setSelection(0);
        else if (status.equalsIgnoreCase(list.get(1)))
            mSpinnerStatus.setSelection(1);
        else if (status.equalsIgnoreCase(list.get(2)))
            mSpinnerStatus.setSelection(2);

        Log.e(TAG, "STATUS IS : " + status);

        tvName.setText(WordUtils.capitalize(GlobalSharedPrefs.ETSPrefs.getString(Constants.EMP_NAME_KEY, defaultString)));
        tvEmail.setText(GlobalSharedPrefs.ETSPrefs.getString(Constants.EMP_EMAIL_KEY, defaultString));
        tvPhone.setText(GlobalSharedPrefs.ETSPrefs.getString(Constants.EMP_PHONE_KEY, defaultString));
        tvJoinDate.setText(GlobalSharedPrefs.ETSPrefs.getString(Constants.EMP_JOIN_DATE_KEY, defaultString));

        view.findViewById(R.id.btnProfileLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you sure?")
                        .setContentText("Are you sure you want to logout?")
                        .setCancelText("No")
                        .setConfirmText("Yes, Log me Out!")
                        .showCancelButton(true)
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.cancel();
                            }
                        }).setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        new SessionManager(getContext()).logoutUser();
                        GlobalSharedPrefs.ETSPrefs.edit().clear().apply();
                        startActivity(new Intent(getContext(), LoginActivity.class));
                        getActivity().finish();
                    }
                }).show();
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Crop.REQUEST_PICK && resultCode == Activity.RESULT_OK && data != null) {
            Log.e(TAG, "Result Received by Crop : Begin Cropping");
            beginCrop(data.getData());
        } else if (requestCode == Crop.REQUEST_CROP && data != null) {
            Log.e(TAG, "Result Received by Crop : Handle Cropping");
            handleCrop(resultCode, data);
        }
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == Activity.RESULT_OK) {
            convertToBase64(Crop.getOutput(result).getPath());
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(getActivity(), "" + Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void convertToBase64(String path) {
        final int DESIRED_WIDTH = 640;
        final BitmapFactory.Options sizeOptions = new BitmapFactory.Options();
        sizeOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, sizeOptions);
        final float widthSampling = sizeOptions.outWidth / DESIRED_WIDTH;
        sizeOptions.inJustDecodeBounds = false;
        sizeOptions.inSampleSize = (int) widthSampling;
        final Bitmap bm = BitmapFactory.decodeFile(path, sizeOptions);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
        byte[] byteArrayImage = baos.toByteArray();
        String encodedImageString = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
        imgProfilePic.setImageBitmap(bm);
        //new UpdateProfilePictureService().execute(mUserId, encodedImageString);
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getContext().getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(getActivity(), ProfileFragment.this);
    }

    public void BreakStatusDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_action_dialog_status, null);
        dialogBuilder.setView(dialogView);
        final MaterialEditText etStatus = (MaterialEditText) dialogView.findViewById(R.id.et_enter_status);

        dialogBuilder.setTitle("Break");
        dialogBuilder.setMessage("Enter your break reason below");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
                String bStatus = etStatus.getText().toString().trim();
                new StatusService().execute(mUserId, "On Break", bStatus);
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_my_profile, container, false);
    }

    private class StatusService extends AsyncTask<String, Void, String> {
        private ProgressDialog progressBar;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar = new ProgressDialog(getContext());
            progressBar.setMessage("Updating status, Please wait...");
            progressBar.setIndeterminate(true);
            progressBar.setCancelable(false);
            progressBar.show();
        }

        @Override
        protected String doInBackground(final String... params) {
            final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<SimpleResponse> infoCall = apiService.changeStatusRequest(params[0], params[1], params[2]);
            infoCall.enqueue(new Callback<SimpleResponse>() {
                @Override
                public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "Status Updated Successfully", Toast.LENGTH_LONG).show();
                            GlobalSharedPrefs.ETSPrefs.edit().putString(Constants.EMP_STATUS_KEY, params[1]).apply();
                            GlobalSharedPrefs.ETSPrefs.edit().putString(Constants.EMP_STATUS_BREAK_CONTENT_KEY,
                                    params[2]).apply();
                        }
                    });
                    progressBar.dismiss();
                }

                @Override
                public void onFailure(Call<SimpleResponse> call, Throwable t) {
                    // Log error here since request failed
                    Log.e(TAG, t.toString());
                    progressBar.dismiss();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "Unable to connect, Please try again", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
            return null;
        }
    }

    private class UpdateProfilePictureService extends AsyncTask<String, Void, String> {

        private ProgressDialog progressBar;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar = new ProgressDialog(getContext());
            progressBar.setMessage("Uploading picture, Please wait...");
            progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressBar.setIndeterminate(true);
            progressBar.setCancelable(false);
            progressBar.show();
        }

        @Override
        protected String doInBackground(String... params) {
            RestClient client = new RestClient(Constants.GET_UPDATE_PICTURE_URL);
            client.AddParam("emp_id", params[0]);
            client.AddParam("emp_picture", params[1]);

            try {
                client.Execute(RequestMethod.GET);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String result = client.getResponse();
            Log.d("result", result);
            try {
                progressBar.dismiss();
                JSONObject jsonObject = new JSONObject(result);
                final String status = jsonObject.getString("status");
                Log.d("result", status);
                if (status.equalsIgnoreCase("success")) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "Status Updated Successfully", Toast.LENGTH_LONG).show();
                            GlobalSharedPrefs.ETSPrefs.edit().putString("emp_status", status).apply();
                        }
                    });

                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "Status Not Updated", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}