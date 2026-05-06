package com.example.lostandfoundapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class AdvertAdapter extends ArrayAdapter<Advert> {

    public AdvertAdapter(Context context, List<Advert> adverts) {
        super(context, 0, adverts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Advert advert = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_advert, parent, false);
        }

        TextView tvType = convertView.findViewById(R.id.tvListType);
        TextView tvName = convertView.findViewById(R.id.tvListName);
        TextView tvCategory = convertView.findViewById(R.id.tvListCategory);
        TextView tvLocation = convertView.findViewById(R.id.tvListLocation);
        TextView tvTimestamp = convertView.findViewById(R.id.tvListTimestamp);

        if (advert != null) {
            tvType.setText(advert.getPostType());
            tvName.setText(advert.getName());
            tvCategory.setText("Category: " + advert.getCategory());
            tvLocation.setText("Location: " + advert.getLocation());
            tvTimestamp.setText("Posted on: " + advert.getTimestamp());
        }

        return convertView;
    }
}
