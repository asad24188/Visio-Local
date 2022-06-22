package com.visio.app.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.visio.app.DataModel.CollectMapDataModel
import com.visio.app.R

class AdapterCollectionMap(val context: Context, val list: ArrayList<CollectMapDataModel>) :
    RecyclerView.Adapter<AdapterCollectionMap.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val image: ImageView = itemView.findViewById(R.id.image)
        val name: TextView = itemView.findViewById(R.id.tv_name)
        val id1: TextView = itemView.findViewById(R.id.tv_id1)
        val id2: TextView = itemView.findViewById(R.id.tv_id2)
        val id3: TextView = itemView.findViewById(R.id.tv_id3)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_collection_map, parent, false)
        return AdapterCollectionMap.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model: CollectMapDataModel = list.get(position)
        holder.image.setImageResource(model.image)
        holder.name.text = model.name
        holder.id1.text = model.id1
        holder.id2.text = model.id2
        holder.id3.text = model.id3
    }

    override fun getItemCount(): Int {
        return list.size
    }
}