package com.example.assignments.ui.notifications;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.assignments.R;

import java.util.ArrayList;

public class listviewAdapter extends BaseAdapter {

    public ArrayList<Model> productList;
    Activity activity;

    public listviewAdapter(Activity activity, ArrayList<Model> productList) {
        super();
        this.activity = activity;
        this.productList = productList;
    }

    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public Object getItem(int position) {
        return productList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        TextView activity;
        TextView accuracy;
        TextView latitude;
        TextView longitude;
        TextView timestamp;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        LayoutInflater inflater = activity.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listview_row, null);
            holder = new ViewHolder();
            holder.activity = (TextView) convertView.findViewById(R.id.activity);
            holder.accuracy = (TextView) convertView.findViewById(R.id.accuracy);
            holder.latitude = (TextView) convertView
                    .findViewById(R.id.latitude);
            holder.longitude = (TextView) convertView.findViewById(R.id.longitude);
            holder.timestamp = (TextView) convertView.findViewById(R.id.timestamp);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Model item = productList.get(position);
        holder.activity.setText(item.getActivity().toString());
        holder.accuracy.setText(item.getAccuracy().toString());
        holder.latitude.setText(item.getLatitude().toString());
        holder.longitude.setText(item.getLongitude().toString());
        holder.timestamp.setText(item.getTimestamp().toString());

        return convertView;
    }
}
