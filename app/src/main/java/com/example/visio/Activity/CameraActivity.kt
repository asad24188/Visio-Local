package com.example.visio.Activity

import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.visio.R
import com.example.visio.databinding.ActivityCameraBinding

class CameraActivity : AppCompatActivity() {
    lateinit var binding: ActivityCameraBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}