package com.umt.ameer.ets.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.umt.ameer.ets.R;
import com.umt.ameer.ets.models.ProductNameModel;

import org.apache.commons.lang3.text.WordUtils;

import java.util.List;

/**
 * Created by Ameer on 10/12/2016.
 */

public class ProductSpinnerAdapter extends ArrayAdapter<ProductNameModel> {
    private List<ProductNameModel> items;
    Context context;
    ProductNameModel tempValues = null;

    public ProductSpinnerAdapter(Context context, int resource, List<ProductNameModel> items) {
        super(context, resource, items);
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    private View getCustomView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.custom_spinner_items, null);
        TextView label = (TextView) row.findViewById(R.id.textViewSpinner);
        tempValues = items.get(position);
        label.setText(WordUtils.capitalize(tempValues.productname));
        return row;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public ProductNameModel getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
}

//public class ProductSpinnerAdapter extends ArrayAdapter<String> {
//    private List<String> items;
//    Context context;
//
//    public ProductSpinnerAdapter(Context context, int resource, List<String> items) {
//        super(context, resource, items);
//        this.context = context;
//        this.items = items;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        return getCustomView(position, convertView, parent);
//    }
//
//    private View getCustomView(int position, View convertView, ViewGroup parent) {
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View row = inflater.inflate(R.layout.custom_spinner_items, null);
//        TextView label = (TextView) row.findViewById(R.id.textViewSpinner);
//        label.setText(items.get(position));
//        return row;
//    }
//
//    @Override
//    public View getDropDownView(int position, View convertView, ViewGroup parent) {
//        return getCustomView(position, convertView, parent);
//    }
//
//    @Override
//    public int getCount() {
//        return items.size();
//    }
//
//    @Override
//    public String getItem(int i) {
//        return items.get(i);
//    }
//
//    @Override
//    public long getItemId(int i) {
//        return i;
//    }
//}
