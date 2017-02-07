package com.umt.ameer.ets;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mrengineer13.snackbar.SnackBar;
import com.umt.ameer.ets.appdata.Constants;
import com.umt.ameer.ets.appdata.GlobalSharedPrefs;
import com.umt.ameer.ets.models.FormModel;
import com.umt.ameer.ets.networkmodels.OrdersResponse;
import com.umt.ameer.ets.rest.ApiClient;
import com.umt.ameer.ets.rest.ApiInterface;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowOrderedFormActivity extends AppCompatActivity {

    private String mOrderId, mUserId;
    private TextView mShopName, mOrderDate, mPrice, mStatusCancel, mStatusPending, mStatusDelivered;
    private ListView mProductsListView;
    private ArrayList<Object> listOfProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_ordered_form);

        new GlobalSharedPrefs(this);
        mUserId = GlobalSharedPrefs.ETSPrefs.getString(Constants.EMP_ID_KEY, "0");
        mOrderId = getIntent().getStringExtra("order_id");

        mShopName = (TextView) findViewById(R.id.tvShopNameShowDetail);
        mOrderDate = (TextView) findViewById(R.id.tvDateShowDetail);
        mPrice = (TextView) findViewById(R.id.tvPriceShowDetail);
        mStatusCancel = (TextView) findViewById(R.id.tvStatusCanceledShowDetail);
        mStatusDelivered = (TextView) findViewById(R.id.tvStatusDeliveredShowDetail);
        mStatusPending = (TextView) findViewById(R.id.tvStatusPendingShowDetail);

        mProductsListView = (ListView) findViewById(R.id.listViewProductShowDetail);


    }

    private class GetOrderInfoTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... params) {

            final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<OrdersResponse> infoCall = apiService.getOrdersRequest(params[0]);
            infoCall.enqueue(new Callback<OrdersResponse>() {
                @Override
                public void onResponse(Call<OrdersResponse> call, Response<OrdersResponse> response) {
                    if (response.body().getStatus().equalsIgnoreCase("success")) {

                        listOfProducts = new ArrayList<>();
                        for (int i = 0; i < response.body().getOrderInfo().size(); i++) {
                            OrdersResponse.OrderInfo order = response.body().getOrderInfo().get(i);
                            FormModel temp = new FormModel();
                            temp.orderid = order.getId();
                            temp.status = order.getStatus();
                            temp.date = order.getDate();
                            temp.shopname = order.getShopName();
                            listOfProducts.add(temp);
                        }
                    } else {
                        final String message = response.body().getMessage();
                        ShowOrderedFormActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new SnackBar.Builder(ShowOrderedFormActivity.this, ShowOrderedFormActivity.this.getCurrentFocus())
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
                    ShowOrderedFormActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new SnackBar.Builder(ShowOrderedFormActivity.this, ShowOrderedFormActivity.this.getCurrentFocus())
                                    .withActionMessage("Unable to load info, Please try again later.")
                                    .withDuration((short) 5000)
                                    .show();
                        }
                    });
                }
            });
            return null;
        }
    }
}
