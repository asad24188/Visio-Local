package com.visio.app.Activity

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.permissionx.guolindev.PermissionX
import com.visio.app.Adapter.AddProjAdapter
import com.visio.app.DataModel.BaseResponse
import com.visio.app.DataModel.projects.Project
import com.visio.app.DataModel.projects.ProjectsResponse
import com.visio.app.R
import com.visio.app.Services.ApiClient
import com.visio.app.databinding.ActivityHomeBinding
import com.visio.app.utils.GPSTracker
import com.wayprotect.app.utils.Utilities
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    lateinit var binding: ActivityHomeBinding
    private lateinit var utilities: Utilities
    lateinit var context: Context

    private lateinit var adapter: AddProjAdapter
    private lateinit var list: ArrayList<Project>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestPermission()
        initt()
        clicks()
        getProjects()

    }

    private fun getProjects() {

        var url = "view-project/"+utilities.getUserId(context)

        utilities.showProgressDialog(context,"Processing ...")
        val apiClient = ApiClient()
        apiClient.getApiService().getProjects(url)
            .enqueue(object : Callback<ProjectsResponse> {
                override fun onResponse(call: Call<ProjectsResponse>, response: Response<ProjectsResponse>) {

                utilities.hideProgressDialog()
                if (response.isSuccessful) {

                    list = ArrayList()
                    list = response.body()!!.data
                    if (list.size>0){

                        binding.projectRecycler.layoutManager = LinearLayoutManager(context)
                        adapter = AddProjAdapter(context,list)
                        binding.projectRecycler.adapter = adapter

                    }else{
                        utilities.makeToast(context,"No project found ...")
                    }


                } else {

                    utilities.hideProgressDialog()
                    utilities.makeToast(context,response.body()!!.message)

                }
            }
                override fun onFailure(call: Call<ProjectsResponse>, t: Throwable) {

                    utilities.hideProgressDialog()
                    utilities.makeToast(context,t.message.toString())

                }
            })

    }

    private fun clicks() {

        binding.btnAddproj.setOnClickListener {

            popupwuthdraw()
        }
    }

    private fun popupwuthdraw() {
        val dialog = Dialog(this)

        dialog.setContentView(R.layout.popup_withdraw)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.attributes = lp
        val close = dialog.findViewById<ImageView>(R.id.withCross)
        val pName = dialog.findViewById<EditText>(R.id.pName)
        val save = dialog.findViewById<RelativeLayout>(R.id.save)

        save.setOnClickListener {

            var name = pName.text.toString()
            if (!name.isEmpty()){
                callApi(name,dialog)
            }else{ utilities.makeToast(context,"Enter Name") }
        }

        close.setOnClickListener { dialog.dismiss() }
        dialog.show()

    }

    private fun callApi(name: String, dialog: Dialog) {


        val gpsTracker = GPSTracker(context)
        val lat = gpsTracker.latitude.toString()
        val lng = gpsTracker.longitude.toString()

        var userId = utilities.getUserId(context)
        utilities.showProgressDialog(context,"Processing ...")
        val apiClient = ApiClient()
        apiClient.getApiService().addProject(userId,name,lat,lng)
            .enqueue(object : Callback<BaseResponse> {
                override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {

                    utilities.hideProgressDialog()
                    if (response.isSuccessful) {
                        utilities.makeToast(context,response.body()!!.message)
                        dialog.dismiss()
                        getProjects()
                    } else {

                        utilities.hideProgressDialog()
                        utilities.makeToast(context,response.body()!!.message)

                    }
                }
                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {

                    utilities.hideProgressDialog()
                    utilities.makeToast(context,t.message.toString())

                }
            })



    }

    private fun initt() {

        context = this
        if (!::utilities.isInitialized) utilities = Utilities(this)
    }

    private fun requestPermission() {

        PermissionX.init(this)
            .permissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {

                } else {

                    Toast.makeText(
                        this,
                        "Permissions are denied",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

}