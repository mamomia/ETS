package com.umt.ameer.ets.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.umt.ameer.ets.R;
import com.umt.ameer.ets.appdata.Constants;
import com.umt.ameer.ets.extras.RequestMethod;
import com.umt.ameer.ets.extras.RestClient;
import com.umt.ameer.ets.models.ChildDetailModel;
import com.umt.ameer.ets.models.ParentDetailModel;
import com.umt.ameer.ets.utils.ExpandableListFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class DetailFragment extends ExpandableListFragment {

    ArrayList<ParentDetailModel> parents;

    public DetailFragment() {
    }

    public static DetailFragment newInstance() {
        return new DetailFragment();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new ProductDetailsTask().execute();
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_detail, container, false);
//    }

    private void loadHosts(final ArrayList<ParentDetailModel> newParents) {
        if (newParents == null)
            return;
        parents = newParents;
        // Check for ExpandableListAdapter object
        if (this.getExpandableListAdapter() == null) {
            //Create ExpandableListAdapter Object
            final MyExpandableListAdapter mAdapter = new MyExpandableListAdapter();
            // Set Adapter to ExpandableList Adapter
            this.setListAdapter(mAdapter);
        } else {
            // Refresh ExpandableListView data
            ((MyExpandableListAdapter) getExpandableListAdapter()).notifyDataSetChanged();
        }
    }

    /**
     * A Custom adapter to create Parent view (Used grouprow.xml) and Child View((Used childrow.xml).
     */
    private class MyExpandableListAdapter extends BaseExpandableListAdapter {
        private LayoutInflater inflater;

        public MyExpandableListAdapter() {
            // Create Layout Inflator
            inflater = LayoutInflater.from(getContext());
        }

        // This Function used to inflate parent rows view
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parentView) {
            final ParentDetailModel parent = parents.get(groupPosition);

            // Inflate grouprow.xml file for parent rows
            convertView = inflater.inflate(R.layout.fragment_detail, parentView, false);

            // Get grouprow.xml file elements and set values
            ((TextView) convertView.findViewById(R.id.textCompanyName)).setText(parent.getName());
            ImageView image = (ImageView) convertView.findViewById(R.id.image);

//            image.setImageResource(
//                    getResources().getIdentifier(
//                            "com.androidexample.customexpandablelist:drawable/setting" + parent.getName(), null, null));
//            ImageView rightcheck = (ImageView) convertView.findViewById(R.id.arrowExpand);
            return convertView;
        }


        // This Function used to inflate child rows view
        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                                 View convertView, ViewGroup parentView) {
            final ParentDetailModel parent = parents.get(groupPosition);
            final ChildDetailModel child = parent.getChildren().get(childPosition);

            // Inflate childrow.xml file for child rows
            convertView = inflater.inflate(R.layout.child_detail_row, parentView, false);

            // Get childrow.xml file elements and set values
            ((TextView) convertView.findViewById(R.id.textProductName)).setText(child.getName());
            ((TextView) convertView.findViewById(R.id.textProductPrice)).setText(child.getPrice());
            ((TextView) convertView.findViewById(R.id.textProductSize)).setText(child.getSize());
//            ImageView image = (ImageView) convertView.findViewById(R.id.image);
//            image.setImageResource(
//                    getResources().getIdentifier(
//                            "com.androidexample.customexpandablelist:drawable/setting" + parent.getName(), null, null));
            return convertView;
        }


        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return parents.get(groupPosition).getChildren().get(childPosition);
        }

        //Call when child row clicked
        @Override
        public long getChildId(int groupPosition, int childPosition) {
            /****** When Child row clicked then this function call *******/
            return childPosition;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            int size = 0;
            if (parents.get(groupPosition).getChildren() != null)
                size = parents.get(groupPosition).getChildren().size();
            return size;
        }


        @Override
        public Object getGroup(int groupPosition) {
            return parents.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return parents.size();
        }

        //Call when parent row clicked
        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public void notifyDataSetChanged() {
            // Refresh List rows
            super.notifyDataSetChanged();
        }

        @Override
        public boolean isEmpty() {
            return ((parents == null) || parents.isEmpty());
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return true;
        }
    }

    //Company details
    private class ProductDetailsTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(final String... params) {
            RestClient client = new RestClient(Constants.GET_ALL_PRODUCTS_URL);
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

                    ArrayList<ParentDetailModel> mCompanies = new ArrayList<>();
                    JSONArray jsonArrayCompany = jsonObject.optJSONArray("product_info");

                    for (int i = 0; i < jsonArrayCompany.length(); i++) {
                        ArrayList<ChildDetailModel> mChildsList = new ArrayList<>();
                        ParentDetailModel mParent = new ParentDetailModel();

                        JSONObject productObj = jsonArrayCompany.getJSONObject(i);

                        JSONArray jsonArrayProducts = productObj.optJSONArray("products");
                        String mCompanyTitle = productObj.getString("company_title");

                        mParent.setName(mCompanyTitle);
                        for (int j = 0; j < jsonArrayProducts.length(); j++) {
                            JSONObject productInfoObj = jsonArrayProducts.getJSONObject(j);

                            ChildDetailModel mChild = new ChildDetailModel();
                            mChild.setName(productInfoObj.getString("product_name"));
                            mChild.setPrice(productInfoObj.getString("product_price"));
                            mChild.setSize(productInfoObj.getString("product_size"));
                            mChildsList.add(mChild);
                        }
                        mParent.setChildren(mChildsList);
                        mCompanies.add(mParent);
                    }
                    loadHosts(mCompanies);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}