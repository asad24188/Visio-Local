package com.visio.app.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mtechsoft.compassapp.networking.Constants
import com.visio.app.Activity.CollectionActivity
import com.visio.app.DataModel.projects.Project
import com.visio.app.R

class AddProjAdapter(
    val context: Context,
    val listModel: ArrayList<Project>
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
        val model: Project = listModel.get(position)

        holder.name.text = model.project_name
        holder.address.text = "Pakistan"
        holder.date.text = model.created_at

        holder.itemView.setOnClickListener {
            Constants.PROJECT_ID = model.id.toString()
            context.startActivity(Intent(context, CollectionActivity::class.java))
        }
    }

    override fun getItemCount(): Int {
        return listModel.size
    }
}