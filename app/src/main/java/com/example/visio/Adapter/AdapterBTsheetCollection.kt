package com.example.visio.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.visio.DataModel.CollectionBTsheetDataModel
import com.example.visio.R

class AdapterBTsheetCollection(val context: Context, val list: ArrayList<CollectionBTsheetDataModel>) :
    RecyclerView.Adapter<AdapterBTsheetCollection.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val name: TextView = itemView.findViewById(R.id.tv_collection)
        val id1: TextView = itemView.findViewById(R.id.tv_id1value)
        val id2: TextView = itemView.findViewById(R.id.tv_id2value)
        val id3: TextView = itemView.findViewById(R.id.tv_id3value)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.item_collection_btsheet, parent, false)
        return AdapterBTsheetCollection.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model: CollectionBTsheetDataModel = list.get(position)
        holder.name.text = model.name
        holder.id1.text = model.id1
        holder.id2.text = model.id2
        holder.id3.text = model.id3
    }

    override fun getItemCount(): Int {
        return list.size
    }
}