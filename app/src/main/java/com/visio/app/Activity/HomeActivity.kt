package com.visio.app.Activity

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.fed.fedsense.RoomDB.LocalProject
import com.fed.fedsense.RoomDB.MyAppDataBase
import com.mtechsoft.compassapp.networking.Constants
import com.permissionx.guolindev.PermissionX
import com.visio.app.Adapter.AddProjAdapter
import com.visio.app.Adapter.LocalProjectsAdapter
import com.visio.app.Auth.SigninActivity
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
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HomeActivity : AppCompatActivity() {

    lateinit var binding: ActivityHomeBinding
    private lateinit var utilities: Utilities
    lateinit var context: Context

    private lateinit var adapter: AddProjAdapter
    private lateinit var localProjectsAdapter: LocalProjectsAdapter
    private lateinit var list: ArrayList<Project>
    var localProjects: List<LocalProject> = java.util.ArrayList()
    var lat = ""
    var lng = ""

    private lateinit var myAppDatabase: MyAppDataBase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestPermission()
        initt()
        clicks()

        //local work
        loadall()

        //api work
//        getProjects()

    }

    private fun loadall() {

        var userId = utilities.getUserId(context).toInt()
        localProjects = ArrayList()
        localProjects = myAppDatabase.cardDao().loadAll(userId)
        binding.projectRecycler.layoutManager = LinearLayoutManager(context)
        localProjectsAdapter = LocalProjectsAdapter(context,localProjects)
        binding.projectRecycler.adapter = localProjectsAdapter

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

        binding.logout.setOnClickListener {

            utilities.clearSharedPref(context)
            startActivity(Intent(applicationContext, SigninActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
        }

        binding.iconDelete.setOnClickListener {

            //local work for delete
            var size = Constants.localProjects.size

            if (size == 1){

                myAppDatabase.cardDao().deleteProject(Constants.localProjects[0])

            }else if (size > 1){

                for (i in 0.. Constants.localProjects.size-1){
                    myAppDatabase.cardDao().deleteProject(Constants.localProjects[i])
                }

            }else{
                utilities.makeToast(context,"Please select project")
            }

            loadall()
            Constants.localProjects = ArrayList()


            //api work for delete

//            val s: String = TextUtils.join(",", Constants.strings)
//            if (!s.isEmpty()){
//
//                deleteProject(s)
//
//            }else{
//                utilities.makeToast(context,"Please select project")
//            }

        }
    }

    private fun deleteProject(pIds: String) {

        var url = "delete-project/"+pIds
        utilities.showProgressDialog(context,"Deleting ...")
        val apiClient = ApiClient()
        apiClient.getApiService().deleteProject(url)
            .enqueue(object : Callback<BaseResponse> {
                override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {

                    utilities.hideProgressDialog()
                    if (response.isSuccessful) {

                        getProjects()
                        Constants.strings.clear()

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

                //local work
                addLocalProject(name,dialog)
                //api work
//                callApi(name,dialog)
            }else{ utilities.makeToast(context,"Enter Name") }
        }

        close.setOnClickListener { dialog.dismiss() }
        dialog.show()

    }

    private fun addLocalProject(name: String, dialog: Dialog) {

        val gpsTracker = GPSTracker(context)
        if (gpsTracker.canGetLocation()){

             lat = gpsTracker.latitude.toString()
             lng = gpsTracker.longitude.toString()

        }else{

             lat = "0.0"
             lng = "0.0"
        }

        val c: Date = Calendar.getInstance().getTime()
        println("Current time => $c")
        val df = SimpleDateFormat("yyyy-MMM-dd", Locale.getDefault())
        val date: String = df.format(c)
        val r = Random()
        val id = r.nextInt(100000 - 1) + 1
        Log.d("iddd",id.toString())

        var userid = utilities.getUserId(context)
        val project = LocalProject(id,userid,name,date,lat,lng,false)
        myAppDatabase.cardDao().addProject(project)
        utilities.makeToast(context,"added")
        loadall()
        dialog.dismiss()

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
        myAppDatabase = Room.databaseBuilder(this, MyAppDataBase::class.java, "VISIODB").allowMainThreadQueries().build()

    }

    private fun requestPermission() {

        PermissionX.init(this)
            .permissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE

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