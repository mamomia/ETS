package com.umt.ameer.ets.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.umt.ameer.ets.R;
import com.umt.ameer.ets.models.FormModel;

import org.apache.commons.lang3.text.WordUtils;

import java.util.List;

/**
 * Created by Ameer on 3/5/2016.
 */
public class CustomSubmissionAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<FormModel> formsItems;

    public CustomSubmissionAdapter(Activity activity, List<FormModel> items) {
        this.activity = activity;
        this.formsItems = items;
    }

    @Override
    public int getCount() {
        return formsItems.size();
    }

    @Override
    public Object getItem(int location) {
        return formsItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listview_items, null);
        }

        ViewHolder holder = new ViewHolder();

        holder.tvShopname = (TextView) convertView.findViewById(R.id.tvShopName);
        holder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
        holder.ivStatus = (ImageView) convertView.findViewById(R.id.ivStatusItem);

        // getting movie data for the row
        FormModel m = formsItems.get(position);

        // comment msg
        holder.tvShopname.setText(WordUtils.capitalize(m.shopname));
        holder.tvDate.setText(m.date);

        if (m.status.equalsIgnoreCase("delivered")) {
            holder.ivStatus.setImageResource(R.drawable.img_delivered);
        } else if (m.status.equalsIgnoreCase("pending")) {
            holder.ivStatus.setImageResource(R.drawable.img_pending);
        }
//        holder.ivStatus.setText(m.status);
        return convertView;
    }

    private class ViewHolder {
        public TextView tvShopname;
        public TextView tvDate;
        public ImageView ivStatus;
    }
}
