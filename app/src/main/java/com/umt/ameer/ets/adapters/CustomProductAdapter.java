package com.umt.ameer.ets.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.umt.ameer.ets.R;
import com.umt.ameer.ets.models.ProductModel;

import org.apache.commons.lang3.text.WordUtils;

import java.util.List;

/**
 * Created by Ameer on 3/6/2016.
 */
public class CustomProductAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<ProductModel> prodsItems;

    public CustomProductAdapter(Activity activity, List<ProductModel> items) {
        this.activity = activity;
        this.prodsItems = items;
    }

    @Override
    public int getCount() {
        return prodsItems.size();
    }

    @Override
    public Object getItem(int location) {
        return prodsItems.get(location);
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
            convertView = inflater.inflate(R.layout.listitem_product_show_detail, null);
        }

        ViewHolder holder = new ViewHolder();

        holder.tvProductName = (TextView) convertView.findViewById(R.id.tvProductNameShowDetailItem);
        holder.tvProductQuan = (TextView) convertView.findViewById(R.id.tvQuantityShowDetailItem);
        holder.tvProductPrice = (TextView) convertView.findViewById(R.id.tvPriceShowDetailItem);

        // getting movie data for the row
        ProductModel m = prodsItems.get(position);

        holder.tvProductName.setText(WordUtils.capitalize(m.productname));
        holder.tvProductQuan.setText(m.productquan);
        holder.tvProductPrice.setText(m.productprice);

        return convertView;
    }

    private class ViewHolder {
        public TextView tvProductName;
        public TextView tvProductQuan;
        public TextView tvProductPrice;
    }
}
