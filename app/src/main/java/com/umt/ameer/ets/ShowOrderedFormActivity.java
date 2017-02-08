package com.umt.ameer.ets;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mrengineer13.snackbar.SnackBar;
import com.umt.ameer.ets.appdata.Constants;
import com.umt.ameer.ets.appdata.GlobalSharedPrefs;
import com.umt.ameer.ets.models.ProductModel;
import com.umt.ameer.ets.networkmodels.OrderInfoResponse;
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
    private DetailListAdapter mListAdapter;
    private ArrayList<ProductModel> listOfProducts;

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
        listOfProducts = new ArrayList<>();
        mListAdapter = new DetailListAdapter(listOfProducts, this);
        mProductsListView.setAdapter(mListAdapter);

        new GetOrderInfoTask().execute(mUserId, mOrderId);
    }

    private class GetOrderInfoTask extends AsyncTask<String, String, String> {
        private ProgressDialog progressBar;

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar = new ProgressDialog(ShowOrderedFormActivity.this);
            progressBar.setMessage("Loading, Please wait...");
            progressBar.setIndeterminate(true);
            progressBar.setCancelable(false);
            progressBar.show();
        }

        @Override
        protected String doInBackground(final String... params) {

            final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<OrderInfoResponse> infoCall = apiService.getOrderInfoRequest(params[0], params[1]);
            infoCall.enqueue(new Callback<OrderInfoResponse>() {
                @Override
                public void onResponse(Call<OrderInfoResponse> call, Response<OrderInfoResponse> response) {
                    progressBar.dismiss();
                    if (response.body().getStatus().equalsIgnoreCase("success")) {

                        listOfProducts.clear();
                        for (int i = 0; i < response.body().getOrderInfo().getProducts().size(); i++) {
                            OrderInfoResponse.Product product = response.body().getOrderInfo().getProducts().get(i);
                            ProductModel temp = new ProductModel();
                            temp.prodid = product.getId();
                            temp.productname = product.getProductName();
                            temp.productquan = product.getProductQuantity();
                            temp.productprice = product.getProductPrice();
                            listOfProducts.add(temp);
                        }
                        final OrderInfoResponse.OrderInfo order = response.body().getOrderInfo();
                        ShowOrderedFormActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mShopName.setText("" + order.getShopName().toUpperCase().trim());
                                mOrderDate.setText("" + order.getDate().trim());
                                mPrice.setText("" + order.getPrice().trim());

                                if (order.getStatus().equalsIgnoreCase("pending")) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        mStatusPending.setTextColor(getResources().getColor(R.color.holo_orange_light, getTheme()));
                                        mStatusDelivered.setTextColor(getResources().getColor(R.color.white, getTheme()));
                                        mStatusCancel.setTextColor(getResources().getColor(R.color.white, getTheme()));
                                    } else {
                                        mStatusPending.setTextColor(getResources().getColor(R.color.holo_orange_light));
                                        mStatusDelivered.setTextColor(getResources().getColor(R.color.white));
                                        mStatusCancel.setTextColor(getResources().getColor(R.color.white));
                                    }
                                } else if (order.getStatus().equalsIgnoreCase("delivered")) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        mStatusPending.setTextColor(getResources().getColor(R.color.white, getTheme()));
                                        mStatusDelivered.setTextColor(getResources().getColor(R.color.colorPrimaryDark, getTheme()));
                                        mStatusCancel.setTextColor(getResources().getColor(R.color.white, getTheme()));
                                    } else {
                                        mStatusPending.setTextColor(getResources().getColor(R.color.white));
                                        mStatusDelivered.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                                        mStatusCancel.setTextColor(getResources().getColor(R.color.white));
                                    }
                                } else if (order.getStatus().equalsIgnoreCase("cancelled")) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        mStatusPending.setTextColor(getResources().getColor(R.color.white, getTheme()));
                                        mStatusDelivered.setTextColor(getResources().getColor(R.color.white, getTheme()));
                                        mStatusCancel.setTextColor(getResources().getColor(R.color.holo_red_light, getTheme()));
                                    } else {
                                        mStatusPending.setTextColor(getResources().getColor(R.color.white));
                                        mStatusDelivered.setTextColor(getResources().getColor(R.color.white));
                                        mStatusCancel.setTextColor(getResources().getColor(R.color.holo_red_light));
                                    }
                                }

                                mListAdapter.notifyDataSetChanged();
                            }
                        });

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
                public void onFailure(Call<OrderInfoResponse> call, Throwable t) {
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

    private class DetailListAdapter extends ArrayAdapter<ProductModel> {
        Context mContext;

        // View lookup cache
        private class ViewHolder {
            TextView txtName;
            TextView txtPrice;
            TextView txtQuant;
        }

        public DetailListAdapter(ArrayList<ProductModel> data, Context context) {
            super(context, R.layout.listitem_product_show_detail, data);
            this.mContext = context;
        }

        private int lastPosition = -1;

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            ProductModel dataModel = (ProductModel) getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            ViewHolder viewHolder; // view lookup cache stored in tag

            final View result;

            if (convertView == null) {

                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.listitem_product_show_detail, parent, false);
                viewHolder.txtName = (TextView) convertView.findViewById(R.id.tvProductNameShowDetailItem);
                viewHolder.txtPrice = (TextView) convertView.findViewById(R.id.tvPriceShowDetailItem);
                viewHolder.txtQuant = (TextView) convertView.findViewById(R.id.tvQuantityShowDetailItem);

                result = convertView;
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
                result = convertView;
            }

            Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
            result.startAnimation(animation);
            lastPosition = position;

            viewHolder.txtName.setText(dataModel.productname);
            viewHolder.txtPrice.setText(dataModel.productprice);
            viewHolder.txtQuant.setText(dataModel.productquan);
            // Return the completed view to render on screen
            return convertView;
        }
    }
}
