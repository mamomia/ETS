package com.umt.ameer.ets;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umt.ameer.ets.appdata.Constants;
import com.umt.ameer.ets.appdata.GlobalSharedPrefs;
import com.umt.ameer.ets.networkmodels.CompanyInfoResponse;
import com.umt.ameer.ets.networkmodels.ProductsInfoResponse;
import com.umt.ameer.ets.networkmodels.SimpleResponse;
import com.umt.ameer.ets.rest.ApiClient;
import com.umt.ameer.ets.rest.ApiInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FormActivity extends AppCompatActivity {

    private ProductsAdapter mProductsAdapter;

    private ArrayList<CompanySpinnerModel> mCompaniesSpinnerList;
    private ArrayList<ProductsInfoResponse.Product> mProductsSpinnerList;
    private ArrayList<ProductInfo> mProductsList;

    private TextView tvTotalPrice, tvProductSize, tvProductPrice, tvCompanies, tvProducts;
    private EditText etShopName, etProductQuantity;

    private int mSelectedCompanyIndex = -1, mSelectedProductIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        new GlobalSharedPrefs(this);

        //Making Keyboard hide on click outside
        final RelativeLayout parentContainer = (RelativeLayout) findViewById(R.id.parentLayoutFormActivity);
        parentContainer.setClickable(true);
        parentContainer.setFocusable(true);
        parentContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentContainer.requestFocus();
            }
        });

        TextView tvDate = (TextView) findViewById(R.id.tvDateForm);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        String currentDateTime = sdf.format(new Date());
        tvDate.setText(currentDateTime);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbarForm);
        mToolbar.setTitle(" ADD NEW ORDER");
        mToolbar.setTitleTextColor(Color.WHITE);
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        etShopName = (EditText) findViewById(R.id.etShopNameForm);
        etProductQuantity = (EditText) findViewById(R.id.etProductQuanForm);
        tvTotalPrice = (TextView) findViewById(R.id.tvTotalAmountForm);
        tvProductSize = (TextView) findViewById(R.id.tvSizeProductForm);
        tvProductPrice = (TextView) findViewById(R.id.tvPriceProductForm);
        tvCompanies = (TextView) findViewById(R.id.tvCompanyForm);
        tvProducts = (TextView) findViewById(R.id.tvProductForm);

        //initialising data
        tvTotalPrice.setText("0");
        tvProductPrice.setText("");
        tvProductSize.setText("");

        //spinners
        mCompaniesSpinnerList = new ArrayList<>();
        mProductsSpinnerList = new ArrayList<>();
        mProductsList = new ArrayList<>();

        mProductsAdapter = new ProductsAdapter(this, R.layout.product_list_item, mProductsList);

        ListView productsList = (ListView) findViewById(R.id.listViewProductsForm);
        productsList.setAdapter(mProductsAdapter);

        tvCompanies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // custom dialog
                final Dialog dialog = new Dialog(FormActivity.this);
                dialog.setContentView(R.layout.custom_list_dialog);
                dialog.setTitle("Select Company");
                dialog.setCanceledOnTouchOutside(true);

                ListView list = (ListView) dialog.findViewById(R.id.mDialogList);
                CompanyAdapter adapter = new CompanyAdapter(dialog.getContext(), R.layout.custom_spinner_items, mCompaniesSpinnerList);
                list.setAdapter(adapter);

                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        mSelectedCompanyIndex = i;
                        CompanySpinnerModel row = mCompaniesSpinnerList.get(i);
                        tvCompanies.setText(row.name.toUpperCase());
                        new ProductsInfoTask().execute(row.id);
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        tvProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // custom dialog
                final Dialog dialog = new Dialog(FormActivity.this);
                dialog.setContentView(R.layout.custom_list_dialog);
                dialog.setTitle("Select Product");
                dialog.setCanceledOnTouchOutside(true);

                ListView list = (ListView) dialog.findViewById(R.id.mDialogList);
                ProductAdapter adapter = new ProductAdapter(dialog.getContext(), R.layout.custom_spinner_items, mProductsSpinnerList);
                list.setAdapter(adapter);

                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        mSelectedProductIndex = i;
                        ProductsInfoResponse.Product row = mProductsSpinnerList.get(i);
                        tvProducts.setText(row.getProductName().toUpperCase());

                        tvProductSize.setText("Size: " + row.getProductSize());
                        tvProductPrice.setText("Price: RS. " + row.getProductPrice());

                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        //button click listners
        findViewById(R.id.layoutAddProductForm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateAddProductForm()) {

                    ProductsInfoResponse.Product item = mProductsSpinnerList.get(mSelectedProductIndex);
                    ProductInfo temp = new ProductInfo();
                    temp.id = item.getId();
                    temp.productName = item.getProductName();
                    temp.price = item.getProductPrice();
                    temp.quantity = etProductQuantity.getText().toString().trim();
                    mProductsList.add(temp);
                    mProductsAdapter.notifyDataSetChanged();

                    //clear spinner and update price
                    mSelectedProductIndex = -1;
                    mSelectedCompanyIndex = -1;
                    tvCompanies.setText("Select Company");
                    tvProducts.setText("Select Product");
                    tvProductPrice.setText("");
                    tvProductSize.setText("");

                    mProductsSpinnerList.clear();
                    updateTotalPrice();
                } else
                    Toast.makeText(FormActivity.this, "Please validate product info before adding it.", Toast.LENGTH_LONG).show();
            }
        });

        findViewById(R.id.btnCheckoutForm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateOrderForm()) {
                    new SweetAlertDialog(FormActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Checkout")
                            .setContentText("Are you sure you want to checkout now?")
                            .setCancelText("No")
                            .setConfirmText("Yes, Done!")
                            .showCancelButton(true)
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismissWithAnimation();
                                }
                            }).setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            String mUserId = GlobalSharedPrefs.ETSPrefs.getString(Constants.EMP_ID_KEY, "0");
                            String sup_id = GlobalSharedPrefs.ETSPrefs.getString(Constants.EMP_SUPERIOR_ID_KEY, "0");
                            new CheckoutOrderTask().execute(mUserId, sup_id, etShopName.getText().toString(), tvTotalPrice.getText().toString());
                        }
                    }).show();
                } else
                    Toast.makeText(FormActivity.this, "Please validate order info before checkout.", Toast.LENGTH_LONG).show();
            }
        });

        //Fetch Initial Data
        new CompanyInfoTask().execute();
    }

    private void updateTotalPrice() {
        tvTotalPrice.setText("0");
        int total = 0;
        for (ProductInfo item : mProductsList) {
            total = total + (Integer.parseInt(item.price) * Integer.parseInt(item.quantity));
        }
        tvTotalPrice.setText("" + total);
    }

    //validation on form
    private boolean validateAddProductForm() {
        if (etProductQuantity.getText().toString().trim().length() == 0)
            return false;
        else if (mSelectedCompanyIndex == -1)
            return false;
        else if (mSelectedProductIndex == -1)
            return false;

        return true;
    }

    private boolean validateOrderForm() {
        if (etShopName.getText().toString().trim().length() == 0)
            return false;
        else if (mProductsList.size() < 1)
            return false;

        return true;
    }

    //Adapters
    private class ProductsAdapter extends ArrayAdapter {

        public ProductsAdapter(Context context, int resource, List<ProductInfo> items) {
            super(context, resource, items);
        }

        @Override
        public int getCount() {
            return mProductsList.size();
        }

        @Override
        public ProductInfo getItem(int position) {
            return mProductsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(getContext(), R.layout.product_list_item, null);
                new ViewHolder(convertView);
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            ProductInfo item = getItem(position);
            final int index = position;

            holder.product_name.setText(item.productName.toUpperCase());
            holder.product_price.setText("RS. " + item.price);
            holder.product_quan.setText("Quantity: " + item.quantity);
            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //remove product from array
                    mProductsList.remove(index);
                    notifyDataSetChanged();
                    updateTotalPrice();
                }
            });
            return convertView;
        }

        private class ViewHolder {
            TextView product_name, product_quan, product_price;
            ImageView btnDelete;

            ViewHolder(View view) {
                product_name = (TextView) view.findViewById(R.id.tvProductNameFormItem);
                product_quan = (TextView) view.findViewById(R.id.tvProductQuantityFormItem);
                product_price = (TextView) view.findViewById(R.id.tvProductPriceFormItem);
                btnDelete = (ImageView) view.findViewById(R.id.ivDeleteProductFormItem);
                view.setTag(this);
            }
        }
    }

    private class CompanyAdapter extends ArrayAdapter {

        private List<CompanySpinnerModel> data;

        public CompanyAdapter(Context context, int resource, List<CompanySpinnerModel> items) {
            super(context, resource, items);
            data = items;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public CompanySpinnerModel getItem(int position) {
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
            CompanySpinnerModel item = getItem(position);
            holder.name.setText(item.name.toUpperCase());
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

    private class ProductAdapter extends ArrayAdapter {

        private List<ProductsInfoResponse.Product> data;

        public ProductAdapter(Context context, int resource, List<ProductsInfoResponse.Product> items) {
            super(context, resource, items);
            data = items;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public ProductsInfoResponse.Product getItem(int position) {
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
            ProductsInfoResponse.Product item = getItem(position);
            holder.name.setText(item.getProductName().toUpperCase());
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

    //Models
    public class ProductInfo {
        public String id;
        public String productName;
        public String quantity;
        public String price;
    }

    private class CompanySpinnerModel {
        public String id;
        public String name;
    }

    //webservices interfaces task
    private class CompanyInfoTask extends AsyncTask<String, String, String> {

        private ProgressDialog progressBar;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar = new ProgressDialog(FormActivity.this);
            progressBar.setMessage("Please wait...");
            progressBar.setIndeterminate(true);
            progressBar.setCancelable(false);
            progressBar.show();
        }

        @Override
        protected String doInBackground(final String... params) {

            final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<CompanyInfoResponse> infoCall = apiService.getCompaniesRequest();
            infoCall.enqueue(new Callback<CompanyInfoResponse>() {
                @Override
                public void onResponse(Call<CompanyInfoResponse> call, Response<CompanyInfoResponse> response) {
                    progressBar.dismiss();

                    if (response.body().getStatus().equalsIgnoreCase("success")) {

                        mCompaniesSpinnerList.clear();
                        for (int i = 0; i < response.body().getCompanies().size(); i++) {
                            CompanyInfoResponse.Company company = response.body().getCompanies().get(i);
                            CompanySpinnerModel temp = new CompanySpinnerModel();
                            temp.id = company.getId();
                            temp.name = company.getCompanyName();
                            mCompaniesSpinnerList.add(temp);
                        }

                    } else {
                        FormActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(FormActivity.this, "No company found.", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Call<CompanyInfoResponse> call, Throwable t) {
                    // Log error here since request failed
                    progressBar.dismiss();
                    FormActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(FormActivity.this, "Unable to connect, Please try again", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
            return null;
        }
    }

    private class ProductsInfoTask extends AsyncTask<String, String, String> {

        private ProgressDialog progressBar;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar = new ProgressDialog(FormActivity.this);
            progressBar.setMessage("Please wait...");
            progressBar.setIndeterminate(true);
            progressBar.setCancelable(false);
            progressBar.show();
        }

        @Override
        protected String doInBackground(final String... params) {

            final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<ProductsInfoResponse> infoCall = apiService.getCompaniesProductsRequest(params[0]);
            infoCall.enqueue(new Callback<ProductsInfoResponse>() {
                @Override
                public void onResponse(Call<ProductsInfoResponse> call, Response<ProductsInfoResponse> response) {
                    progressBar.dismiss();

                    tvProducts.setText("Select Product");
                    tvProductPrice.setText("");
                    tvProductSize.setText("");

                    if (response.body().getStatus().equalsIgnoreCase("success")) {

                        mProductsSpinnerList.clear();
                        for (int i = 0; i < response.body().getProducts().size(); i++) {
                            ProductsInfoResponse.Product product = response.body().getProducts().get(i);
                            mProductsSpinnerList.add(product);
                        }

                    } else {
                        FormActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(FormActivity.this, "No product found.", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Call<ProductsInfoResponse> call, Throwable t) {
                    // Log error here since request failed
                    progressBar.dismiss();
                    FormActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(FormActivity.this, "Unable to connect, Please try again", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
            return null;
        }
    }

    private class CheckoutOrderTask extends AsyncTask<String, String, String> {

        private ProgressDialog progressBar;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar = new ProgressDialog(FormActivity.this);
            progressBar.setMessage("Sending information, Please wait...");
            progressBar.setIndeterminate(true);
            progressBar.setCancelable(false);
            progressBar.show();
        }

        @Override
        protected String doInBackground(final String... params) {

            JSONArray products = makProductsJsonObject(mProductsList);
            final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<SimpleResponse> infoCall = apiService.addNewOrderRequest(params[0], params[1], params[2], params[3], products);
            infoCall.enqueue(new Callback<SimpleResponse>() {
                @Override
                public void onResponse(Call<SimpleResponse> call, final Response<SimpleResponse> response) {
                    progressBar.dismiss();
                    if (response.body().getStatus().equalsIgnoreCase("success")) {
                        FormActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(FormActivity.this, FormDoneActivity.class);
                                intent.putExtra("order_id", response.body().getOrderId());
                                startActivity(intent);
                                finish();
                            }
                        });
                    } else {
                        FormActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(FormActivity.this, "Failed to send order, " + response.body().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Call<SimpleResponse> call, Throwable t) {
                    // Log error here since request failed
                    Log.e("error", t.getMessage());
                    progressBar.dismiss();
                    FormActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(FormActivity.this, "Unable to connect, Please try again", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
            return null;
        }
    }

    private JSONArray makProductsJsonObject(ArrayList<ProductInfo> prds) {
        try {
            JSONObject obj = null;
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < prds.size(); i++) {
                obj = new JSONObject();
                obj.put("id", prds.get(i).id);
                obj.put("quantity", prds.get(i).quantity);
                jsonArray.put(obj);
            }
            return jsonArray;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }
}