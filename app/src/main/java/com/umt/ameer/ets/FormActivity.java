package com.umt.ameer.ets;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.umt.ameer.ets.adapters.CompanySpinnerAdapter;
import com.umt.ameer.ets.adapters.ProductSpinnerAdapter;
import com.umt.ameer.ets.appdata.Constants;
import com.umt.ameer.ets.appdata.GlobalSharedPrefs;
import com.umt.ameer.ets.extras.RequestMethod;
import com.umt.ameer.ets.extras.RestClient;
import com.umt.ameer.ets.models.CompanyNameModel;
import com.umt.ameer.ets.models.ProductNameModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fr.ganfra.materialspinner.MaterialSpinner;
import info.hoang8f.widget.FButton;

public class FormActivity extends AppCompatActivity {

    private List<ProductInfo> mProductsList;
    private ProductAdapter mAdapter;
    private SwipeMenuListView mListView;

    private List<CompanyNameModel> mCompanySpinnerList;
    private CompanySpinnerAdapter mCompanyAdapter;

    private List<ProductNameModel> mProductsSpinnerList;
    private ProductSpinnerAdapter mProductsAdapter;


    MaterialEditText etShopName, etQuantity, etShopAddress;
    TextView tvDate;
    MaterialSpinner mSpinnerProductName, mSpinnerCompanyName;
    FButton btnAddProduct;
    LinearLayout layout;


    JSONArray jsonArray = new JSONArray();

    CompanyNameModel tempValues = null;
    ProductNameModel tempValues1 = null;

    String companyid;
    private Toolbar toolbar;
    String productprice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        toolbar = (Toolbar) findViewById(R.id.toolbarForm);
        toolbar.inflateMenu(R.menu.menu_dashboard);

        tvDate = (TextView) findViewById(R.id.tvDateForm);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US);
        String currentDateTime = sdf.format(new Date());
        tvDate.setText(currentDateTime);

        btnAddProduct = (FButton) findViewById(R.id.btnAddProductItems);
        etQuantity = (MaterialEditText) findViewById(R.id.etProductQuantity);
        etShopName = (MaterialEditText) findViewById(R.id.etShopName);
        etShopAddress = (MaterialEditText) findViewById(R.id.etShopAddress);
        layout = (LinearLayout) findViewById(R.id.form_added_product_layout);


        mSpinnerCompanyName = (MaterialSpinner) findViewById(R.id.spinnerCompanyNameForm);
        mSpinnerProductName = (MaterialSpinner) findViewById(R.id.spinnerProductNameForm);
        mCompanySpinnerList = new ArrayList<>();
        mProductsSpinnerList = new ArrayList<>();

        mCompanyAdapter = new CompanySpinnerAdapter(this, R.layout.custom_spinner_items, mCompanySpinnerList);
        mProductsAdapter = new ProductSpinnerAdapter(this, R.layout.custom_spinner_items, mProductsSpinnerList);

        mSpinnerCompanyName.setAdapter(mCompanyAdapter);
        mSpinnerProductName.setAdapter(mProductsAdapter);

        mListView = (SwipeMenuListView) findViewById(R.id.listViewProductsForm);
        mProductsList = new ArrayList<>();
        mAdapter = new ProductAdapter(FormActivity.this, R.layout.product_list_item, mProductsList);
        mListView.setAdapter(mAdapter);

        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.setVisibility(View.VISIBLE);
                if (!etQuantity.getText().toString().trim().isEmpty()) {
                    ProductInfo mProductItem = new ProductInfo();
                    mProductItem.productName = (ProductNameModel) mSpinnerProductName.getSelectedItem();
                    mProductItem.companyName = (CompanyNameModel) mSpinnerCompanyName.getSelectedItem();
                    int quan = Integer.parseInt(etQuantity.getText().toString().trim());
                    int price = Integer.parseInt(productprice.toString());
                    int multi = quan * price;
                    String q = String.valueOf(multi);
                    mProductItem.quantity = etQuantity.getText().toString().trim();
                    mProductItem.price = q;
                    mProductsList.add(mProductItem);
                    FormActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                            Toast.makeText(FormActivity.this, "Added to Cart", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

        // step 1. create a MenuCreator
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        FormActivity.this);
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        // set creator
        mListView.setMenuCreator(creator);
        // step 2. listener item click event
        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        // delete
                        layout.setVisibility(View.INVISIBLE);
                        mProductsList.remove(position);
                        mAdapter.notifyDataSetChanged();
                        break;
                }
                return false;
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Snackbar.make(view, "Swipe left to remove the item", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });
        new CompanyName().execute();
    }

    //Company details
    private class CompanyName extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(final String... params) {
            RestClient client = new RestClient(Constants.GET_COMPANY_NAME_URL);
            try {
                client.Execute(RequestMethod.GET);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String result = client.getResponse();
            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = jsonObject.getString("status");
                if (status.equalsIgnoreCase("success")) {
                    mCompanySpinnerList.clear();
                    String arrayStr = jsonObject.getString("company_name_info");
                    Log.d("Company Name", arrayStr);
                    jsonArray = jsonObject.optJSONArray("company_name_info");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);

                        CompanyNameModel temp1 = new CompanyNameModel();
                        temp1.companyname = object.getString("company_name");
                        temp1.companyid = object.getString("id");

                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(FormActivity.this);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("company_id", temp1.companyid);
                        Log.d("SharedPref", temp1.companyid);
                        editor.apply();
                        mCompanySpinnerList.add(temp1);
                    }
                    FormActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mSpinnerCompanyName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    CompanyNameModel item = (CompanyNameModel) parent.getItemAtPosition(position);
                                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(FormActivity.this);
                                    companyid = preferences.getString("company_name", item.companyid);
                                    Log.d("GlobalSharedPrefs", companyid);
                                    new ProductName().execute(companyid);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                            mCompanyAdapter.notifyDataSetChanged();
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    //Product details
    private class ProductName extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            RestClient client = new RestClient(Constants.GET_PRODUCT_NAME_URL);
            new GlobalSharedPrefs(FormActivity.this);
            client.AddParam("id", params[0]);
            try {
                client.Execute(RequestMethod.GET);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String result = client.getResponse();
            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = jsonObject.getString("status");
                if (status.equalsIgnoreCase("success")) {
                    mProductsSpinnerList.clear();
                    String arrayStr = jsonObject.getString("product_name_info");
                    Log.d("Product Name", arrayStr);
                    jsonArray = jsonObject.optJSONArray("product_name_info");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);

                        ProductNameModel temp2 = new ProductNameModel();
                        temp2.productname = object.getString("product_name");
                        temp2.productprice = object.getString("product_price");

                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(FormActivity.this);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("product_name", temp2.productprice);
                        Log.d("SharedPref", temp2.productprice);
                        editor.apply();
                        mProductsSpinnerList.add(temp2);
                    }
                    FormActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mSpinnerProductName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    ProductNameModel item = (ProductNameModel) parent.getItemAtPosition(position);
                                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(FormActivity.this);
                                    productprice = preferences.getString("product_price", item.productprice);
                                    Log.d("GlobalSharedPrefs", productprice);
//                                    new ProductName().execute(companyid);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                            mProductsAdapter.notifyDataSetChanged();
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    class ProductAdapter extends ArrayAdapter {

        public ProductAdapter(Context context, int resource, List<ProductInfo> items) {
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
            return getCustomView(position, convertView, parent);
        }

        private View getCustomView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(getContext(),
                        R.layout.product_list_item, null);
                new ProductAdapter.ViewHolder(convertView);
            }
            ProductAdapter.ViewHolder holder = (ProductAdapter.ViewHolder) convertView.getTag();
            ProductInfo item = getItem(position);

            holder.product_price.setText(item.price);
            holder.product_quan.setText(item.quantity);
            tempValues = item.companyName;
            tempValues1 = item.productName;

            holder.product_name.setText(tempValues1.productname);
            holder.company_name.setText(tempValues.companyname);
            return convertView;
        }

        class ViewHolder {
            TextView product_name, product_quan, company_name, product_price;

            public ViewHolder(View view) {
                product_name = (TextView) view.findViewById(R.id.tvProductNameFormItem);
                product_quan = (TextView) view.findViewById(R.id.tvProductQuantityFormItem);
                company_name = (TextView) view.findViewById(R.id.tvCompanyNameFormItem);
                product_price = (TextView) view.findViewById(R.id.tvProductPriceFormItem);
                view.setTag(this);
            }
        }
    }

    private class ProductInfo {
        public int id;
        public ProductNameModel productName;
        public String quantity;
        public CompanyNameModel companyName;
        public String price;
    }
}
