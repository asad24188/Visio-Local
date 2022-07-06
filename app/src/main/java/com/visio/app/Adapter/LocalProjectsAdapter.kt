package com.visio.app.Adapter

import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fed.fedsense.RoomDB.LocalProject
import com.mtechsoft.compassapp.networking.Constants
import com.visio.app.Activity.CollectionActivity
import com.visio.app.DataModel.projects.Project
import com.visio.app.R
import java.text.SimpleDateFormat
import java.util.*


class LocalProjectsAdapter(
    val context: Context,
    val listModel: List<LocalProject>
): RecyclerView.Adapter<LocalProjectsAdapter.ViewHolder>() {

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)  {

        val name: TextView = itemView.findViewById(R.id.tv_projectname)
        val address: TextView = itemView.findViewById(R.id.tv_address)
        val date: TextView = itemView.findViewById(R.id.tv_date)
        val layout:RelativeLayout = itemView.findViewById(R.id.item)
        val selected:ImageView = itemView.findViewById(R.id.selected)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_project, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model: LocalProject = listModel.get(position)
        holder.name.text = model.project_name

        var lat = model.latitude
        var lngg = model.longitude

        if (lat.equals("0.0") || lngg.equals("0.0")){

        }else{

            val geocoder: Geocoder
            val addresses: List<Address>
            geocoder = Geocoder(context, Locale.getDefault())
            addresses = geocoder.getFromLocation(model.latitude.toDouble(), model.longitude.toDouble(), 1)
            val address: String = addresses[0].getAddressLine(0)
            val city = addresses[0].locality
            val country = addresses[0].countryName
            holder.address.text = city+", "+country
        }



//        var format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
//        val newDate: Date = format.parse(model.created_at)
//
////        format = SimpleDateFormat("MMM dd,yyyy hh:mm a")
//        format = SimpleDateFormat("MMM dd,yyyy")
//        val date: String = format.format(newDate)
//        holder.date.text = date

        holder.itemView.setOnClickListener {

                Constants.PROJECT_ID = model.id.toString()
                context.startActivity(Intent(context, CollectionActivity::class.java))
        }

        holder.itemView.setOnLongClickListener{

            if (model.checked) {

                Constants.localProjects.remove(model)
                Constants.strings.remove(model.id.toString())
                model.checked = false
                holder.selected.visibility = View.GONE

            } else {

                model.checked = true
                holder.selected.visibility = View.VISIBLE
                Constants.strings.add(model.id.toString())
                Constants.localProjects.add(model)

            }

            return@setOnLongClickListener true
        }
    }

    override fun getItemCount(): Int {
        return listModel.size
    }
}