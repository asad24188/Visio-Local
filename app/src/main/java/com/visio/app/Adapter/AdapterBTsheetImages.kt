package com.example.visio.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.visio.DataModel.DataModelBottomSheetImages
import com.visio.app.R

class AdapterBTsheetImages(val context: Context, val list: ArrayList<DataModelBottomSheetImages>,val clicklisnetr: ItemClickListener) :
    RecyclerView.Adapter<AdapterBTsheetImages.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val image: ImageView = itemView.findViewById(R.id.image)
        val deleteImage: CardView = itemView.findViewById(R.id.deleteImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.rcitem_btsheet, parent, false)
        return AdapterBTsheetImages.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model: DataModelBottomSheetImages = list.get(position)
        holder.image.setImageURI(model.image)

        holder.deleteImage.setOnClickListener {

            list.removeAt(position)
            notifyDataSetChanged()
            clicklisnetr.onItemDeleted(position)
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface ItemClickListener {
        fun onItemDeleted(position: Int)
    }

}