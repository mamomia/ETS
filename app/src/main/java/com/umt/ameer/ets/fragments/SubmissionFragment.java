package com.umt.ameer.ets.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.github.mrengineer13.snackbar.SnackBar;
import com.umt.ameer.ets.R;
import com.umt.ameer.ets.ShowOrderedFormActivity;
import com.umt.ameer.ets.adapters.CustomSubmissionAdapter;
import com.umt.ameer.ets.appdata.Constants;
import com.umt.ameer.ets.appdata.GlobalSharedPrefs;
import com.umt.ameer.ets.models.FormModel;
import com.umt.ameer.ets.networkmodels.OrdersResponse;
import com.umt.ameer.ets.rest.ApiClient;
import com.umt.ameer.ets.rest.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubmissionFragment extends Fragment {
    public ListView listView;
    public List<FormModel> listOfForms;
    String userId;

    public SubmissionFragment() {
        // Required empty public constructor
    }

    public static SubmissionFragment newInstance() {
        return new SubmissionFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_submission, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        new GlobalSharedPrefs(getContext());
        listView = (ListView) view.findViewById(R.id.listViewSubmissionForm);
        userId = GlobalSharedPrefs.ETSPrefs.getString(Constants.EMP_ID_KEY, "0");
        new GetOrderTask().execute(userId);
    }

    private class GetOrderTask extends AsyncTask<String, String, String> implements AdapterView.OnItemClickListener {
        int flag_status = 0;

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (flag_status == 0) {
                new SnackBar.Builder(getActivity().getApplicationContext(), getView())
                        .withActionMessage("No Submissions Found")
                        .withDuration((short) 5000)
                        .show();
            } else {
                listView.setOnItemClickListener(this);
                listView.setAdapter(new CustomSubmissionAdapter(getActivity(), listOfForms));
            }
        }

        @Override
        protected String doInBackground(String... params) {

            final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<OrdersResponse> infoCall = apiService.getOrdersRequest(params[0]);
            infoCall.enqueue(new Callback<OrdersResponse>() {
                @Override
                public void onResponse(Call<OrdersResponse> call, Response<OrdersResponse> response) {
                    if (response.body().getStatus().equalsIgnoreCase("success")) {

                        listOfForms = new ArrayList<>();
                        for (int i = 0; i < response.body().getOrderInfo().size(); i++) {
                            OrdersResponse.OrderInfo order = response.body().getOrderInfo().get(i);
                            FormModel temp = new FormModel();
                            temp.orderid = order.getId();
                            temp.status = order.getStatus();
                            temp.date = order.getDate();
                            temp.shopname = order.getShopName();
                            listOfForms.add(temp);
                        }
                        flag_status = 1;
                    } else {
                        final String message = response.body().getMessage();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new SnackBar.Builder(getActivity().getApplicationContext(), getView())
                                        .withActionMessage(message)
                                        .withDuration((short) 5000)
                                        .show();
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Call<OrdersResponse> call, Throwable t) {
                    // Log error here since request failed
                }
            });
            return null;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            FormModel item = (FormModel) parent.getItemAtPosition(position);
            Intent intent = new Intent(getActivity(), ShowOrderedFormActivity.class);
            intent.putExtra("order_id", item.orderid);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.fade_in, R.anim.stable);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
