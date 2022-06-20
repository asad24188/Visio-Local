package com.example.visio.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.visio.Activity.CollectionActivity
import com.example.visio.Activity.SignUpActivity
import com.example.visio.DataModel.ProjectModel
import com.example.visio.R

class AddProjAdapter(
    val context: Context,
    val listModel: ArrayList<ProjectModel>
): RecyclerView.Adapter<AddProjAdapter.ViewHolder>() {

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)  {

        val name: TextView = itemView.findViewById(R.id.tv_projectname)
        val address: TextView = itemView.findViewById(R.id.tv_address)
        val date: TextView = itemView.findViewById(R.id.tv_date)
        val layout:RelativeLayout = itemView.findViewById(R.id.item)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_project, parent, false)
        return AddProjAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model: ProjectModel = listModel.get(position)
        holder.name.text = model.name
        holder.address.text = model.address
        holder.date.text = model.Date
        holder.itemView.setOnClickListener {
            context.startActivity(Intent(context, CollectionActivity::class.java))
        }
    }

    override fun getItemCount(): Int {
        return listModel.size
    }
}