package com.example.carbyer.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.carbyer.model.Dealer;

import java.util.List;

public class DealerSpinnerAdapter extends ArrayAdapter<Dealer> {

    public DealerSpinnerAdapter(Context context, List<Dealer> dealers) {
        super(context, android.R.layout.simple_spinner_item, dealers);
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView tv = (TextView) super.getView(position, convertView, parent);
        tv.setText(getItem(position).name);
        return tv;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView tv = (TextView) super.getDropDownView(position, convertView, parent);
        tv.setText(getItem(position).name);
        return tv;
    }
}