package com.example.visio.Activity

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.visio.Adapter.AdapterCollectionMap
import com.example.visio.DataModel.CollectMapDataModel
import com.example.visio.R
import com.example.visio.databinding.ActivityCollectionBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class CollectionActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    lateinit var binding: ActivityCollectionBinding

    private lateinit var mMap: GoogleMap

    private lateinit var lastLocation: Location
    private lateinit var fusedlocationclient: FusedLocationProviderClient

    private lateinit var adapter: AdapterCollectionMap
    private lateinit var list: ArrayList<CollectMapDataModel>
    companion object {
        private const val LOCATION_REQUEST_CODE = 1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCollectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.goback.setOnClickListener { finish() }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedlocationclient = LocationServices.getFusedLocationProviderClient(this)


        list = ArrayList()
        list.add(CollectMapDataModel(R.drawable.ic_collection_image, "Collection 1","ID 1 : 123","ID 2 : 345","ID 3 : 789"))
        list.add(CollectMapDataModel(R.drawable.ic_collection_image, "Collection 1","ID 1 : 123","ID 2 : 345","ID 3 : 789"))
        list.add(CollectMapDataModel(R.drawable.ic_collection_image, "Collection 1","ID 1 : 123","ID 2 : 345","ID 3 : 789"))
        list.add(CollectMapDataModel(R.drawable.ic_collection_image, "Collection 1","ID 1 : 123","ID 2 : 345","ID 3 : 789"))
        list.add(CollectMapDataModel(R.drawable.ic_collection_image, "Collection 1","ID 1 : 123","ID 2 : 345","ID 3 : 789"))


        binding.collectionRecycler.setLayoutManager(
            LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        )
        adapter = AdapterCollectionMap(this, list)
        binding.collectionRecycler.adapter = adapter
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

//        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMarkerClickListener(this)
        setUpMap()
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
                placeMarkerOnMap(currentLatLong)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 12f))
            }
        }

    }

    private fun placeMarkerOnMap(currentLatLng: LatLng) {
        val markerOptions = MarkerOptions().position(currentLatLng)
        markerOptions.title("$currentLatLng")
        mMap.addMarker(markerOptions)

    }

    override fun onMarkerClick(p0: Marker) = false
}