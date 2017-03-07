package com.umt.ameer.ets.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.applidium.shutterbug.utils.ShutterbugManager;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.soundcloud.android.crop.Crop;
import com.umt.ameer.ets.LoginActivity;
import com.umt.ameer.ets.R;
import com.umt.ameer.ets.appdata.Constants;
import com.umt.ameer.ets.appdata.GlobalSharedPrefs;
import com.umt.ameer.ets.appdata.SessionManager;
import com.umt.ameer.ets.extras.CircularImageViewSingle;
import com.umt.ameer.ets.networkmodels.SimpleResponse;
import com.umt.ameer.ets.rest.ApiClient;
import com.umt.ameer.ets.rest.ApiInterface;

import org.apache.commons.lang3.text.WordUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Ameer on 10/9/2016.
 */

public class ProfileFragment extends Fragment {

    private static final String TAG = "Settings Fragment";
    private static String mUserId = "", mSupId = "";

    private CircularImageViewSingle imgProfilePic;
    private TextView tvCurrentStatus;
    private TextView tvWorkingHours, tvInTime, tvOutTime, tvChangeStatus;

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
        mSupId = GlobalSharedPrefs.ETSPrefs.getString(Constants.EMP_SUPERIOR_ID_KEY, "0");

        imgProfilePic = (CircularImageViewSingle) view.findViewById(R.id.ivProfileImageProfile);
        tvCurrentStatus = (TextView) view.findViewById(R.id.profile_current_status);
        view.findViewById(R.id.layoutProfileImageProfile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Crop.pickImage(getActivity(), ProfileFragment.this);
            }
        });
        TextView tvDate = (TextView) view.findViewById(R.id.tvDateProfile);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy", Locale.US);
        String currentDateTime = sdf.format(new Date());
        tvDate.setText(currentDateTime);

        tvWorkingHours = (TextView) view.findViewById(R.id.tvWorkingHoursProfile);
        tvInTime = (TextView) view.findViewById(R.id.tvInTimeProfile);
        tvOutTime = (TextView) view.findViewById(R.id.tvOutTimeProfile);
        tvChangeStatus = (TextView) view.findViewById(R.id.tvChangeStatusBtnProfile);

        TextView tvName = (TextView) view.findViewById(R.id.profile_name);
        TextView tvEmail = (TextView) view.findViewById(R.id.profile_email);
        TextView tvPhone = (TextView) view.findViewById(R.id.profile_phone);
        TextView tvJoinDate = (TextView) view.findViewById(R.id.profile_join_date);

        final List<String> statusList = new ArrayList<>();
        statusList.add("On Duty");
        statusList.add("Off Duty");
        statusList.add("On Break");

        //setting up default or updated values

        String defaultString = "Unavailable";

        String status = GlobalSharedPrefs.ETSPrefs.getString(Constants.EMP_STATUS_KEY, defaultString);
        Log.e(TAG, "STATUS IS : " + status);
        tvCurrentStatus.setText("Current status : " + status);

        if (status.equalsIgnoreCase("On Duty"))
            tvChangeStatus.setText("Punch Out\nor\nBreak");
        else if (status.equalsIgnoreCase("Off Duty"))
            tvChangeStatus.setText("Punch In");
        else
            tvChangeStatus.setText("Finish Break!");

        tvName.setText(WordUtils.capitalize(GlobalSharedPrefs.ETSPrefs.getString(Constants.EMP_NAME_KEY, defaultString)));
        tvEmail.setText(GlobalSharedPrefs.ETSPrefs.getString(Constants.EMP_EMAIL_KEY, defaultString));
        tvPhone.setText(GlobalSharedPrefs.ETSPrefs.getString(Constants.EMP_PHONE_KEY, defaultString));
        tvJoinDate.setText(GlobalSharedPrefs.ETSPrefs.getString(Constants.EMP_JOIN_DATE_KEY, defaultString));
        String url = GlobalSharedPrefs.ETSPrefs.getString(Constants.EMP_DP_KEY, defaultString);
        ShutterbugManager
                .getSharedImageManager(getContext())
                .download(url, new ShutterbugManager.ShutterbugManagerListener() {
                    @Override
                    public void onImageSuccess(ShutterbugManager shutterbugManager, Bitmap bitmap, String s) {
                        imgProfilePic.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onImageFailure(ShutterbugManager shutterbugManager, String s) {

                    }
                });

        tvChangeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // custom dialog
                final Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.custom_list_dialog);
                dialog.setTitle("Select Company");
                dialog.setCanceledOnTouchOutside(true);

                ListView list = (ListView) dialog.findViewById(R.id.mDialogList);
                StatusAdapter adapter = new StatusAdapter(dialog.getContext(), R.layout.custom_spinner_items, statusList);
                list.setAdapter(adapter);

                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String row = statusList.get(i);
                        switch (i) {
                            case 0:
                                new StatusService().execute(mUserId, mSupId, row, "none");
                                break;
                            case 1:
                                new StatusService().execute(mUserId, mSupId, row, "none");
                                break;
                            case 2:
                                BreakStatusDialog();
                                break;
                        }
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

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

        CalculateWorkingHours();
    }

    private void CalculateWorkingHours() {
        String status = GlobalSharedPrefs.ETSPrefs.getString(Constants.EMP_STATUS_KEY, "");
        if (status.equalsIgnoreCase("On Duty")) {
            SimpleDateFormat formatter = new SimpleDateFormat("hh:mm", Locale.US);
            String onDutyTimeString = GlobalSharedPrefs.ETSPrefs.getString(Constants.EMP_ON_DUTY_TIME_KEY, "00:00");
            try {
                Date inTime = formatter.parse(onDutyTimeString);

                long startTime = inTime.getTime();
                long endTime = System.currentTimeMillis();
                long differenceTime = endTime - startTime;

                tvInTime.setText(formatter.format(inTime));
                tvWorkingHours.setText(GetTimeStringFrom(differenceTime));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (status.equalsIgnoreCase("Off Duty"))
            tvOutTime.setText(GlobalSharedPrefs.ETSPrefs.getString(Constants.EMP_OFF_DUTY_TIME_KEY, "00:00"));
    }

    private String GetTimeStringFrom(long milliseconds) {
        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
        int hours = (int) ((milliseconds / (1000 * 60 * 60)) % 24);
        return String.format(Locale.US, "%02d:%02d", hours, minutes);
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
                new StatusService().execute(mUserId, mSupId, "On Break", bStatus);
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
                String bStatus = "N/A";
                new StatusService().execute(mUserId, mSupId, "On Break", bStatus);
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_profile, container, false);
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
            Call<SimpleResponse> infoCall = apiService.changeStatusRequest(params[0], params[1], params[2], params[3]);
            infoCall.enqueue(new Callback<SimpleResponse>() {
                @Override
                public void onResponse(Call<SimpleResponse> call, final Response<SimpleResponse> response) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "Status Updated Successfully", Toast.LENGTH_LONG).show();
                            tvCurrentStatus.setText("Current status : " + params[2]);
                            GlobalSharedPrefs.ETSPrefs.edit().putString(Constants.EMP_STATUS_KEY, params[2]).apply();
                            GlobalSharedPrefs.ETSPrefs.edit().putString(Constants.EMP_STATUS_BREAK_CONTENT_KEY,
                                    params[3]).apply();

                            if (params[2].equalsIgnoreCase("On Duty")) {
                                tvChangeStatus.setText("Punch Out\nor\nBreak");

                                if (response.body().getIsFirstTimeIn().equalsIgnoreCase("true")) {
                                    SimpleDateFormat formatter = new SimpleDateFormat("hh:mm", Locale.US);
                                    String now = formatter.format(new Date());
                                    GlobalSharedPrefs.ETSPrefs.edit().putString(Constants.EMP_ON_DUTY_TIME_KEY, now).apply();
                                }

                            } else if (params[2].equalsIgnoreCase("Off Duty")) {
                                tvChangeStatus.setText("Punch In");

                                if (response.body().getIsFirstTimeIn().equalsIgnoreCase("true")) {
                                    SimpleDateFormat formatter = new SimpleDateFormat("hh:mm", Locale.US);
                                    String now = formatter.format(new Date());
                                    GlobalSharedPrefs.ETSPrefs.edit().putString(Constants.EMP_OFF_DUTY_TIME_KEY, now).apply();

                                    tvOutTime.setText(now);
                                }

                            } else {
                                tvChangeStatus.setText("Finish Break!");
                            }

                            CalculateWorkingHours();
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

    private class StatusAdapter extends ArrayAdapter {

        private List<String> data;

        public StatusAdapter(Context context, int resource, List<String> items) {
            super(context, resource, items);
            data = items;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public String getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(getContext(), R.layout.custom_spinner_items, null);
                new ViewHolder(convertView);
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            String item = getItem(position);
            holder.name.setText(item.toUpperCase());
            return convertView;
        }

        private class ViewHolder {
            TextView name;

            ViewHolder(View view) {
                name = (TextView) view.findViewById(R.id.textViewSpinner);
                view.setTag(this);
            }
        }
    }
}