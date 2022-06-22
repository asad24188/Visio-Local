package com.example.visio.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.visio.Adapter.AddProjAdapter
import com.example.visio.Services.ApiClient
import com.example.visio.databinding.ActivityHomeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAddproj.setOnClickListener {
            startActivity(Intent(this,CameraActivity::class.java)) }

        binding.homerecycler.layoutManager=
            LinearLayoutManager(this)
        binding.homerecycler.setHasFixedSize(true)

    }

    }
