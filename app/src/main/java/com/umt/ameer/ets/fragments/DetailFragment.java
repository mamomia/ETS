package com.umt.ameer.ets.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.umt.ameer.ets.R;
import com.umt.ameer.ets.models.ChildDetailModel;
import com.umt.ameer.ets.networkmodels.ProductsResponse;
import com.umt.ameer.ets.rest.ApiClient;
import com.umt.ameer.ets.rest.ApiInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailFragment extends Fragment {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<ChildDetailModel>> listDataChild;

    public DetailFragment() {
    }

    public static DetailFragment newInstance() {
        return new DetailFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        // get the listview
        expListView = (ExpandableListView) view.findViewById(R.id.lvExp);
        listAdapter = new ExpandableListAdapter(getContext(), listDataHeader, listDataChild);
        // setting list adapter
        expListView.setAdapter(listAdapter);

        new ProductDetailsTask().execute();
    }

    private class ExpandableListAdapter extends BaseExpandableListAdapter {

        private Context _context;
        private List<String> _listDataHeader; // header titles
        // child data in format of header title, child title
        private HashMap<String, List<ChildDetailModel>> _listDataChild;

        public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                     HashMap<String, List<ChildDetailModel>> listChildData) {
            this._context = context;
            this._listDataHeader = listDataHeader;
            this._listDataChild = listChildData;
        }

        @Override
        public Object getChild(int groupPosition, int childPosititon) {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                    .get(childPosititon);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(int groupPosition, final int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {

            final ChildDetailModel childText = (ChildDetailModel) getChild(groupPosition, childPosition);

            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.child_detail_row, null);
            }

            TextView txtProductName = (TextView) convertView
                    .findViewById(R.id.tvProductNameChild);
            txtProductName.setText(childText.getName());
            TextView txtProductSize = (TextView) convertView
                    .findViewById(R.id.tvProductSizeChild);
            txtProductSize.setText(childText.getSize());
            TextView txtProductPrice = (TextView) convertView
                    .findViewById(R.id.tvProductPriceChild);
            txtProductPrice.setText(childText.getPrice());


            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                    .size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return this._listDataHeader.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return this._listDataHeader.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            String headerTitle = (String) getGroup(groupPosition);
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.parent_detail_row, null);
            }

            TextView lblListHeader = (TextView) convertView
                    .findViewById(R.id.tvCompanyNameGroup);
            lblListHeader.setTypeface(null, Typeface.BOLD);
            lblListHeader.setText(headerTitle);

            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }

    private class ProductDetailsTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(final String... params) {

            final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<ProductsResponse> infoCall = apiService.getProductsRequest();
            infoCall.enqueue(new Callback<ProductsResponse>() {
                @Override
                public void onResponse(Call<ProductsResponse> call, Response<ProductsResponse> response) {
                    Log.e("GETPRODUCTSRESP", "msg : " + response.body().getMessage());
                    if (response.body().getStatus().equalsIgnoreCase("success")) {
                        listDataHeader.clear();
                        listDataChild.clear();

                        for (int i = 0; i < response.body().getCompaniesCount(); i++) {
                            ArrayList<ChildDetailModel> mChildsList = new ArrayList<>();

                            String mCompanyTitle = response.body().getProductInfo().get(i).getCompanyTitle();
                            listDataHeader.add(mCompanyTitle);

                            for (int j = 0; j < response.body().getProductInfo().get(i).getProducts().size(); j++) {
                                ProductsResponse.Product product = response.body().getProductInfo().get(i).getProducts().get(j);
                                ChildDetailModel mChild = new ChildDetailModel();
                                mChild.setName(product.getProductName());
                                mChild.setPrice(product.getProductPrice());
                                mChild.setSize(product.getProductSize());
                                mChildsList.add(mChild);
                            }
                            listDataChild.put(mCompanyTitle, mChildsList);
                        }
                        listAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<ProductsResponse> call, Throwable t) {
                    // Log error here since request failed
                }
            });
            return null;
        }
    }
}