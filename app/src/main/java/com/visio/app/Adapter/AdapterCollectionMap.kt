package com.visio.app.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.mtechsoft.compassapp.networking.Constants
import com.visio.app.DataModel.CollectMapDataModel
import com.visio.app.DataModel.projectDetail.Collection
import com.visio.app.R

class AdapterCollectionMap(val context: Context, val list: ArrayList<Collection>) :
    RecyclerView.Adapter<AdapterCollectionMap.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val image: ImageView = itemView.findViewById(R.id.image)
        val name: TextView = itemView.findViewById(R.id.tv_name)
        val id1: TextView = itemView.findViewById(R.id.tv_id1)
        val id2: TextView = itemView.findViewById(R.id.tv_id2)
        val id3: TextView = itemView.findViewById(R.id.tv_id3)
        val selected:ImageView = itemView.findViewById(R.id.selected)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_collection_map, parent, false)
        return AdapterCollectionMap.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model: Collection = list.get(position)

        Glide.with(context).load(Constants.IMAGE_BASE_URL+model.collection_image).diskCacheStrategy(
            DiskCacheStrategy.ALL).into(holder.image)
        holder.name.text = model.collection_name
        holder.id1.text = model.card_id1
        holder.id2.text = model.card_id2
        holder.id3.text = model.card_id3

        holder.itemView.setOnLongClickListener{

            if (model.checked) {

                Constants.collections.remove(model.id.toString())
                model.checked = false
                holder.selected.visibility = View.GONE

            } else {

                model.checked = true
                holder.selected.visibility = View.VISIBLE
                Constants.collections.add(model.id.toString())
            }

            return@setOnLongClickListener true
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }
}