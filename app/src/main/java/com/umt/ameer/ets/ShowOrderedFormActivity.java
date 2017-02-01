package com.umt.ameer.ets;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.umt.ameer.ets.adapters.CustomProductAdapter;
import com.umt.ameer.ets.appdata.Constants;
import com.umt.ameer.ets.appdata.GlobalSharedPrefs;
import com.umt.ameer.ets.extras.RequestMethod;
import com.umt.ameer.ets.extras.RestClient;
import com.umt.ameer.ets.models.ProductModel;
import com.umt.ameer.ets.models.ShopModel;

import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ShowOrderedFormActivity extends AppCompatActivity {

    TextView tvShopName, tvShopAddress, tvDate;
    ImageView ivOrderStatus;
    String userId, orderId;
    String shopname, status, date;
    String order_id;

    JSONArray jsonArray = new JSONArray();

    ListView listViewProducts;
    private List<ProductModel> listOfProds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_ordered_form);

        order_id = getIntent().getStringExtra("order_id");
//        order_id = orderID;
        Log.d("ORDERID > ", order_id);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarShowOrder);
        toolbar.setNavigationIcon(R.drawable.img_back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setTitle(" Order Detail");
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));

        tvShopName = (TextView) findViewById(R.id.tvShopNameShowDetail);
        tvShopAddress = (TextView) findViewById(R.id.tvShopAddressShowDetail);
        tvDate = (TextView) findViewById(R.id.tvDateShowDetail);

        ivOrderStatus = (ImageView) findViewById(R.id.ivStatusShowDetail);

        listViewProducts = (ListView) findViewById(R.id.listViewProductShowDetail);
        listOfProds = new ArrayList<>();

//        for (int i = 0; i < 6; i++) {
//            ProductModel temp = new ProductModel();
//            temp.productname = "Sting " + i;
//            temp.productquan = "" + i;
//            temp.productprice = i + "00 Rs";
//
//            listOfProds.add(temp);
//        }


        userId = GlobalSharedPrefs.ETSPrefs.getString("emp_id", "0");
//        orderId = GlobalSharedPrefs.ETSPrefs.getString("order_id", "0");
//        orderId = "order_id";
        new GetOrderedForm().execute(order_id, userId);
        new ProductDetail().execute(userId);

//        tvShopName.setText("Bholla Pan Shop");
//        tvShopAddress.setText("Opposite UMT on Collage Road");
//        tvDate.setText("01/01/1992");
//        ivOrderStatus.setImageResource(R.drawable.img_delivered);

    }

    private class GetOrderedForm extends AsyncTask<String, String, String> {

//        private ProgressDialog progressDialog;

//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            Log.d("PreExecute", "");
//            progressDialog = new ProgressDialog(getApplicationContext());
//            progressDialog.setMessage("Getting your previous submissions, Please wait...");
//            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//            progressDialog.setIndeterminate(true);
//            progressDialog.setCancelable(false);
//            progressDialog.show();
//        }

//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            progressDialog.dismiss();
//            ShopModel sm = new ShopModel();
//            tvShopName.setText(sm.shopname);
//            tvDate.setText(sm.date);
//            tvShopAddress.setText(sm.address);
//            if (sm.status == "delivered") {
//                ivOrderStatus.setImageResource(R.drawable.img_delivered);
//            } else {
//                ivOrderStatus.setImageResource(R.drawable.img_pending);
//            }
//        }

        @Override
        protected String doInBackground(String... params) {
            RestClient client = new RestClient(Constants.GET_ORDERED_FORM_URL);
            client.AddParam("order_id", params[0]);
            client.AddParam("emp_id", params[1]);
            Log.d("URL_orderid", params[0]);
            Log.d("URL_empid", params[1]);
            try {
                client.Execute(RequestMethod.GET);
            } catch (Exception e) {
                e.printStackTrace();
            }

            String result = client.getResponse();
            Log.d("ResponseOrderID", ">" + result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = jsonObject.getString("status");
                if (status.equalsIgnoreCase("success")) {
                    String arrayStr = jsonObject.getString("ordered_form_info");

                    JSONObject object = new JSONObject(arrayStr);
                    Log.d("ArrayStr", ">" + object);

                    final ShopModel temp = new ShopModel();
                    temp.shopname = object.getString("shop_name");
                    temp.status = object.getString("status");
                    temp.date = object.getString("date");
                    temp.address = object.getString("address_area");
                    Log.d("Shopname > ", temp.shopname);

                    ShowOrderedFormActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvShopName.setText(WordUtils.capitalize(temp.shopname));
                            tvDate.setText(temp.date);
                            tvShopAddress.setText(WordUtils.capitalize(temp.address));
                            if (temp.status.equalsIgnoreCase("delivered")) {
                                ivOrderStatus.setImageResource(R.drawable.img_delivered);
                            } else {
                                ivOrderStatus.setImageResource(R.drawable.img_pending);
                            }
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class ProductDetail extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            RestClient client = new RestClient(Constants.GET_PRODUCT_DETAIL_URL);
            client.AddParam("emp_id", params[0]);
            try {
                client.Execute(RequestMethod.GET);
            } catch (Exception e) {
                e.printStackTrace();
            }

            String result = client.getResponse();
            Log.d("ResponseOrderID", ">" + result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = jsonObject.getString("status");
                if (status.equalsIgnoreCase("success")) {
                    String arrayStr = jsonObject.getString("product_detail_info");

//                    JSONObject object = new JSONObject(arrayStr);
                    Log.d("ArrayStr", ">" + arrayStr);

                    jsonArray = jsonObject.optJSONArray("product_detail_info");

                    listOfProds = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject c = jsonArray.getJSONObject(i);

                        ProductModel temp = new ProductModel();
                        temp.productquan = c.getString("quantity");
                        temp.productprice = c.getString("price");
                        temp.productname = c.getString("product_name");

                        listOfProds.add(temp);
                    }
                    ShowOrderedFormActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listViewProducts.setAdapter(new CustomProductAdapter(ShowOrderedFormActivity.this, listOfProds));
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
