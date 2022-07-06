package com.visio.app.Activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.fed.fedsense.RoomDB.LocalCollection
import com.fed.fedsense.RoomDB.LocalProject
import com.fed.fedsense.RoomDB.MyAppDataBase
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.mtechsoft.compassapp.networking.Constants
import com.visio.app.Adapter.AdapterCollectionMap
import com.visio.app.Adapter.LocalCollectionsAdapter
import com.visio.app.Adapter.LocalProjectsAdapter
import com.visio.app.DataModel.BaseResponse
import com.visio.app.DataModel.projectDetail.Collection
import com.visio.app.DataModel.projectDetail.ProjectDetailResponse
import com.visio.app.R
import com.visio.app.Services.ApiClient
import com.visio.app.databinding.ActivityCollectionBinding
import com.wayprotect.app.utils.Utilities
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CollectionActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    lateinit var binding: ActivityCollectionBinding
    private lateinit var utilities: Utilities
    lateinit var context: Context
    private lateinit var projectDetailResponse: ProjectDetailResponse


    private lateinit var mMap: GoogleMap
    private lateinit var lastLocation: Location
    private lateinit var fusedlocationclient: FusedLocationProviderClient
    private lateinit var adapter: AdapterCollectionMap
    private lateinit var localAdapter: LocalCollectionsAdapter
    private lateinit var collections: ArrayList<Collection>
    var localCollections: List<LocalCollection> = java.util.ArrayList()

    companion object {
        private const val LOCATION_REQUEST_CODE = 1
    }

    private lateinit var myAppDatabase: MyAppDataBase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCollectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initt()
        clicks()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedlocationclient = LocationServices.getFusedLocationProviderClient(this)


        binding.collectionRecycler.setLayoutManager(
            LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        )
    }

    override fun onResume() {
        super.onResume()

        //local work
        getLocalProjectDetail()

        //api work
//        getProjectDetail()

    }

    private fun getLocalProjectDetail() {

        localCollections = java.util.ArrayList()
        var projectId = Constants.PROJECT_ID.toInt()
        localCollections = myAppDatabase.collectionDao().loadAllCollections(projectId)

        binding.collectionRecycler.setLayoutManager(LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false))
        localAdapter = LocalCollectionsAdapter(context, localCollections)
        binding.collectionRecycler.adapter = localAdapter


    }

    private fun getLocalProjectDetailafterDelete() {

        localCollections = java.util.ArrayList()
        var projectId = Constants.PROJECT_ID.toInt()
        localCollections = myAppDatabase.collectionDao().loadAllCollections(projectId)

        binding.collectionRecycler.setLayoutManager(LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false))
        localAdapter = LocalCollectionsAdapter(context, localCollections)
        binding.collectionRecycler.adapter = localAdapter
        placeLocalMarkerOnMap(localCollections)


    }


    private fun getProjectDetail() {

        collections = ArrayList()

        var url = "view-collection/"+Constants.PROJECT_ID
        utilities.showProgressDialog(context,"Processing ...")
        val apiClient = ApiClient()
        apiClient.getApiService().projectDetail(url)
            .enqueue(object : Callback<ProjectDetailResponse> {
                override fun onResponse(call: Call<ProjectDetailResponse>, response: Response<ProjectDetailResponse>) {

                    utilities.hideProgressDialog()
                    if (response.isSuccessful) {

                        projectDetailResponse = response.body()!!
                        val gson = Gson()
                        val json = gson.toJson(projectDetailResponse)
                        utilities.saveString(context, "pDetail", json)

                        collections = response.body()!!.data
                        if (collections.size > 0){

                            binding.collectionRecycler.visibility = View.VISIBLE
                            placeMarkerOnMap(collections)
                            adapter = AdapterCollectionMap(context, collections)
                            binding.collectionRecycler.adapter = adapter

                        }else{

                            binding.collectionRecycler.visibility = View.GONE

                        }

                    } else {

                        utilities.hideProgressDialog()
                        utilities.makeToast(context,response.body()!!.message)
                        binding.collectionRecycler.visibility = View.GONE

                    }
                }
                override fun onFailure(call: Call<ProjectDetailResponse>, t: Throwable) {

                    utilities.hideProgressDialog()
                    utilities.makeToast(context,t.message.toString())

                }
            })

    }

    private fun deleteColection(pIds: String) {

        var url = "delete-collection/"+pIds
        utilities.showProgressDialog(context,"Deleting ...")
        val apiClient = ApiClient()
        apiClient.getApiService().deleteProject(url)
            .enqueue(object : Callback<BaseResponse> {
                override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {

                    utilities.hideProgressDialog()
                    if (response.isSuccessful) {

                        mMap.clear()

                        getProjectDetail()
                        Constants.collections.clear()

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


    private fun clicks() {

        binding.goback.setOnClickListener { finish() }

        binding.addCollection.setOnClickListener {
            startActivity(Intent(this,CameraActivity::class.java))
        }

        binding.iconDelete.setOnClickListener {

            //local work
            var size = Constants.localCollections.size

            if (size == 1){

                myAppDatabase.collectionDao().deleteCollection(Constants.localCollections[0])

            }else if (size > 1){

                for (i in 0.. Constants.localCollections.size-1){
                    myAppDatabase.collectionDao().deleteCollection(Constants.localCollections[i])
                }

            }else{
                utilities.makeToast(context,"Please select collection")
            }

            mMap.clear()
            getLocalProjectDetailafterDelete()
            Constants.localCollections = ArrayList()

            //api work for delete
//            val s: String = TextUtils.join(",", Constants.collections)
//            if (!s.isEmpty()){
//
//                deleteColection(s)
//
//            }else{
//                utilities.makeToast(context,"Please select collection")
//            }


        }
    }

    private fun initt() {
        context = this
        if (!::utilities.isInitialized) utilities = Utilities(this)
        myAppDatabase = Room.databaseBuilder(this, MyAppDataBase::class.java, "VISIODB").allowMainThreadQueries().build()

    }

    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMarkerClickListener(this)
        setUpMap()
        if (localCollections.size > 0){

            placeLocalMarkerOnMap(localCollections)
        }

    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE
            )
            return
        }
        mMap.isMyLocationEnabled = true
        fusedlocationclient.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                lastLocation = location
                val currentLatLong = LatLng(location.latitude, location.longitude)
//                placeMarkerOnMap(currentLatLong)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 12f))
            }
        }
    }

    private fun placeLocalMarkerOnMap(collections: List<LocalCollection>) {

        val height = 120
        val width = 120
        val bitmapdraw = resources.getDrawable(com.visio.app.R.drawable.black_marker) as BitmapDrawable
        val b = bitmapdraw.bitmap
        val smallMarker = Bitmap.createScaledBitmap(b, width, height, false)

        for (i in 0 .. collections.size-1){

            var latt = collections[i].latitude
            var lngg = collections[i].longitude

            if (latt.equals("0.0") || lngg.equals("0.0")){

                Log.d("nomaker",latt+" "+lngg)
            }else{

                var latlng = LatLng(collections[i].latitude.toDouble(),collections[i].longitude.toDouble())
                Log.d("latlng",latlng.toString())
                val markerOptions = MarkerOptions().position(latlng)
                markerOptions.title(collections[i].collection_name)
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                mMap.addMarker(markerOptions)
            }

        }


    }

    private fun placeMarkerOnMap(collections: ArrayList<Collection>) {

        val height = 120
        val width = 120
        val bitmapdraw = resources.getDrawable(com.visio.app.R.drawable.black_marker) as BitmapDrawable
        val b = bitmapdraw.bitmap
        val smallMarker = Bitmap.createScaledBitmap(b, width, height, false)

        for (i in 0 .. collections.size-1){

            var latlng = LatLng(collections[i].latitude.toDouble(),collections[i].longitude.toDouble())
            Log.d("latlng",latlng.toString())
            val markerOptions = MarkerOptions().position(latlng)
            markerOptions.title(collections[i].collection_name)
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
            mMap.addMarker(markerOptions)
        }


    }

    override fun onMarkerClick(p0: Marker) = false
}