package com.umt.ameer.ets.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.github.mrengineer13.snackbar.SnackBar;
import com.umt.ameer.ets.R;
import com.umt.ameer.ets.ShowOrderedFormActivity;
import com.umt.ameer.ets.adapters.CustomSubmissionAdapter;
import com.umt.ameer.ets.appdata.Constants;
import com.umt.ameer.ets.appdata.GlobalSharedPrefs;
import com.umt.ameer.ets.extras.RequestMethod;
import com.umt.ameer.ets.extras.RestClient;
import com.umt.ameer.ets.models.FormModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SubmissionFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public ListView listView;
    public List<FormModel> listOfForms;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private SwipeRefreshLayout swipeDownToRefresh;

    JSONArray jsonArray = new JSONArray();

    String userId;

    public SubmissionFragment() {
        // Required empty public constructor
    }

    public static SubmissionFragment newInstance() {
        return new SubmissionFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        new GlobalSharedPrefs(getContext());

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_submission, container, false);

        listView = (ListView) rootView.findViewById(R.id.listViewSubmissionForm);

        userId = GlobalSharedPrefs.ETSPrefs.getString("emp_id", "0");

        new GetOrderTask().execute(userId);

        return rootView;
    }

//    public boolean isInternetWorking() {
//        boolean success = false;
//        try {
//            URL url = new URL("https://google.com");
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setConnectTimeout(10000);
//            connection.connect();
//            success = connection.getResponseCode() == 200;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return success;
//    }

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

            //String empId = GlobalSharedPrefs.ETSPrefs.getString("emp_id", "0");
//            String url = Constants.GET_ALL_ORDERS_URL;
//            HashMap<String, String> paramsHash = new HashMap<>();
//            paramsHash.put("emp_id", params[0]);
//            RestClientManager.initialize(getContext());
//            RestClientManager.getInstance().makeJsonArrayRequest(Request.Method.POST, url,
//                    new RequestHandler<>(new RequestCallbacks<ResponseUserSuccess, ErrorModel>() {
//                        @Override
//                        public void onRequestSuccess(final ResponseUserSuccess response) {
//                            try {
//                                String status = response.status;
//                                if (status.equalsIgnoreCase("success")) {
//                                    String MyJSON = null;
//                                    JSONObject jsonObject = new JSONObject(MyJSON);
//                                    JSONArray orderList = jsonObject.getJSONArray(TAG_RESULTS);
//
//                                    listOfForms = new ArrayList<>();
//                                    for (int i = 0; i < orderList.length(); i++) {
//                                        JSONObject c = orderList.getJSONObject(i);
//                                        FormModel temp = new FormModel();
//                                        temp.status = c.getString("status");
//                                        temp.date = c.getString("date");
//
//                                        listOfForms.add(temp);
//                                        JSONObject c = orderList.getJSONObject(i);
//                                        String dstatus = c.getString("status");
//                                        String date = c.getString("date");
//
//                                        HashMap<String, String> lists = new HashMap<String, String>();
//
//                                        lists.put(TAG_STATUS, dstatus);
//                                        lists.put(TAG_DATE, date);
//                                    }

//                                    listView.setAdapter(new CustomSubmissionAdapter(getActivity(), listOfForms));
//                                    ListAdapter adapter = new SimpleAdapter(getActivity(), orderlists, R.layout.listview_items,
//                                            new String[]{TAG_STATUS, TAG_DATE}, new int[]{R.id.ivStatusItem, R.id.tvDate});

//                                    listView.setAdapter(adapter);
//                                    List<OrderInfo> orderInfoList = new ArrayList<OrderInfo>();
//                                    int size = response.order_info.order_id.length();

//                                    listOfForms = new ArrayList<>();
//
//                                    FormModel temp2 = new FormModel();
//                                    temp2.shopname = "shop number first";
//                                    temp2.status = response.order_info.status;
//                                    temp2.date = response.order_info.date;
//
//                                    listOfForms.add(temp2);

//                                    for (int i = 0; i < size; i++) {
//                                        FormModel temp = new FormModel();
//                                        temp.shopname = "shop number " + i;
//                                        temp.status = "pending";
//                                        temp.date = "05/03/2016";
//
//                                        listOfForms.add(temp);
//                                    }

//                                    listView.setOnItemClickListener(this);
//                                    listView.setAdapter(new CustomSubmissionAdapter(getActivity(), listOfForms));

//                                    GlobalSharedPrefs.ETSPrefs.edit().putString("order_id", response.order_info.order_id).apply();
//                                    GlobalSharedPrefs.ETSPrefs.edit().putString("shop_id", response.order_info.shop_id).apply();
//                                    GlobalSharedPrefs.ETSPrefs.edit().putString("emp_id", response.order_info.emp_id).apply();
//                                    GlobalSharedPrefs.ETSPrefs.edit().putString("date", response.order_info.date).apply();
//                                    GlobalSharedPrefs.ETSPrefs.edit().putString("status", response.order_info.status).apply();
//                                    GlobalSharedPrefs.ETSPrefs.edit().putString("pd_id", response.order_info.pd_id).apply();
//                                    GlobalSharedPrefs.ETSPrefs.edit().putString("price", response.order_info.price).apply();
//                                } else {
//                                    getActivity().runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG);
//                                        }
//                                    });
//                                }
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }

//                        @Override
//                        protected void onRequestError(ErrorModel error) {
//                            getActivity().runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    SnackbarManager.show(com.nispok.snackbar.Snackbar.with(getContext())
//                                            .text("Something went wrong"), getActivity());
//                                }
//                            });
//                        }
//                    }, paramsHash));
            RestClient client = new RestClient(Constants.GET_ALL_ORDERS_URL);
            client.AddParam("employee_id", params[0]);

            try {
                client.Execute(RequestMethod.GET);
            } catch (Exception e) {
                e.printStackTrace();
            }

            String result = client.getResponse();
            Log.d("Response", ">" + result);
            try {
                JSONObject jsonObject = new JSONObject(result);

                String status = jsonObject.getString("status");
                if (status.equalsIgnoreCase("success")) {
                    String arrayStr = jsonObject.getString("order_info");

                    Log.d("ArrayStr", ">" + arrayStr);
//                    JSONObject object = new JSONObject(arrayStr);
                    jsonArray = jsonObject.optJSONArray("order_info");

//                    length_order = jsonObject.getJSONArray(arrayStr);
                    //JSONArray length_order = jsonObject.getJSONArray(TAG_RESULTS);
                    listOfForms = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject c = jsonArray.getJSONObject(i);

                        FormModel temp = new FormModel();
                        temp.orderid = c.getString("id");
                        temp.status = c.getString("status");
                        temp.date = c.getString("date");
                        temp.shopname = c.getString("shop_name");
                        Log.d("OrderID >", temp.orderid);

                        new GlobalSharedPrefs(getContext());
                        GlobalSharedPrefs.ETSPrefs.edit().putString("order_id", temp.orderid).commit();

                        listOfForms.add(temp);
                    }
                    flag_status = 1;
                } else {
                    final String message = jsonObject.getString("message");
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new SnackBar.Builder(getActivity().getApplicationContext(), getView())
                                    .withActionMessage(message)
                                    .withDuration((short) 5000)
                                    .show();
                            Toast.makeText(getContext(), "No Submissions Found", Toast.LENGTH_LONG);
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            FormModel item = (FormModel) parent.getItemAtPosition(position);
            Intent intent = new Intent(getActivity(), ShowOrderedFormActivity.class);
            intent.putExtra("order_id", item.orderid);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.fade_in, R.anim.stable);
//            startActivityForResult(new Intent(getActivity(), ShowOrderedFormActivity.class), 0);
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
