package com.biodigital.kotlinapp

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.biodigital.humansdk.HKModule
import com.squareup.picasso.Picasso

class ModuleAdapter(val mContext : Context, var modules:ArrayList<HKModule>) : ArrayAdapter<HKModule>(mContext,R.layout.modulelayout,modules) {

    override fun getCount(): Int {
        return modules.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItem(position: Int): HKModule? {
        return modules.get(position)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var inflator = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val retView = inflator.inflate(R.layout.modulelayout, null)
        val titleTextView : TextView = retView.findViewById(R.id.textview_module)
        val thumbView : ImageView = retView.findViewById(R.id.imageview_module)
        val module = modules.get(position)
        titleTextView.setText(module.title)
        if (!module.thumbnailurl.isEmpty()) {
            if (module.thumbnailurl.startsWith("https")) {
                Picasso.get().load(module.thumbnailurl).into(thumbView)
            } else {
                var resourceid : Int = context.resources.getIdentifier(module.thumbnailurl,"drawable",context.packageName)
                thumbView.setImageResource(resourceid)
                retView.setBackgroundColor(Color.LTGRAY)
            }
        }
        return retView
    }
}