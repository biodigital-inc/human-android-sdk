package com.biodigital.kotlinapp

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.biodigital.humansdk.HKModel
import com.biodigital.humansdk.HKServices
import com.squareup.picasso.Picasso

class ModelAdapter(val mContext : Context, var models:ArrayList<HKModel>) : ArrayAdapter<HKModel>(mContext,R.layout.model_layout,models) {

    override fun getCount(): Int {
        return models.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItem(position: Int): HKModel? {
        return models.get(position)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var inflator = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val retView = inflator.inflate(R.layout.model_layout, null)
        val titleTextView : TextView = retView.findViewById(R.id.textview_model)
        val thumbView : ImageView = retView.findViewById(R.id.imageview_model)
        val model = models.get(position)
        titleTextView.setText(model.title)
        if (!model.thumbnailurl.isEmpty()) {
            if (model.thumbnailurl.startsWith("https")) {
                Picasso.get().load(model.thumbnailurl).into(thumbView)
            } else {
                var resourceid : Int = context.resources.getIdentifier(model.thumbnailurl,"drawable",context.packageName)
                thumbView.setImageResource(resourceid)
                retView.setBackgroundColor(Color.LTGRAY)
            }
        }
        if (HKServices.getInstance().modelDownloaded(model.id)) {
            retView.setBackgroundColor(Color.GREEN)
        }
        return retView
    }
}