package com.biodigital.humansdksampleapp;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.biodigital.humansdk.HKModule;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ModuleAdapter extends ArrayAdapter<HKModule> {

    private final Context mContext;
    private ArrayList<HKModule> modules;

    public ModuleAdapter(Context c, ArrayList<HKModule> mods) {
        super(c,R.layout.modulelayout,mods);
        mContext = c;
        modules = mods;
    }

    public void refreshModules(List<HKModule> events) {
        this.modules.clear();
        this.modules.addAll(events);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return modules.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public HKModule getItem(int position) {
        return modules.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.modulelayout, null);
        }
        final TextView titleTextView = (TextView)convertView.findViewById(R.id.textview_module);
        final ImageView thumbView = (ImageView)convertView.findViewById(R.id.imageview_module);
        HKModule m = modules.get(position);
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
        return convertView;
    }

}