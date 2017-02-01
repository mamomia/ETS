package com.umt.ameer.ets.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.umt.ameer.ets.R;
import com.umt.ameer.ets.models.CompanyNameModel;

import org.apache.commons.lang3.text.WordUtils;

import java.util.List;

/**
 * Created by Ameer on 10/12/2016.
 */

public class CompanySpinnerAdapter extends ArrayAdapter<CompanyNameModel> {
    private List<CompanyNameModel> items;
    Context context;
    CompanyNameModel tempValues = null;

    public CompanySpinnerAdapter(Context context, int resource, List<CompanyNameModel> items) {
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
        label.setText(WordUtils.capitalize(tempValues.companyname));
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
    public CompanyNameModel getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
}

