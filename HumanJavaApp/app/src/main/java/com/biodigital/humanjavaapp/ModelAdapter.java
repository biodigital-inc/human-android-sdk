package com.biodigital.humanjavaapp;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.biodigital.humansdk.HKModel;
import com.biodigital.humansdk.HKServices;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ModelAdapter extends ArrayAdapter<HKModel> {

    private final Context mContext;
    private ArrayList<HKModel> models;

    public ModelAdapter(Context c, ArrayList<HKModel> mods) {
        super(c,R.layout.model_layout,mods);
        mContext = c;
        models = mods;
    }

    public void refreshModels(List<HKModel> events) {
        this.models.clear();
        this.models.addAll(events);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return models.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public HKModel getItem(int position) {
        return models.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.model_layout, null);
        }
        final TextView titleTextView = (TextView)convertView.findViewById(R.id.textview_model);
        final ImageView thumbView = (ImageView)convertView.findViewById(R.id.imageview_model);
        HKModel m = models.get(position);
        titleTextView.setText(m.title);
        if (!m.thumbnailurl.isEmpty()) {
            if (m.thumbnailurl.startsWith("https")) {
                Picasso.get().load(m.thumbnailurl).into(thumbView);
                convertView.setBackgroundColor(Color.WHITE);
            } else {
                Resources res =  mContext.getResources();
                int resourceId = res.getIdentifier(m.thumbnailurl, "drawable", mContext.getPackageName() );
                thumbView.setImageResource( resourceId );
                convertView.setBackgroundColor(Color.LTGRAY);
            }
        }
        if (HKServices.getInstance().modelDownloaded(m.id)) {
            convertView.setBackgroundColor(Color.GREEN);
        }
        return convertView;
    }

}