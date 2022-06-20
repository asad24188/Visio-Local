package com.example.visio.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.visio.R

class SplashActivity : AppCompatActivity() {
    private val SPLASH_TIME_OUT = 2000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            val intent = Intent(this, SigninActivity::class.java)
            startActivity(intent)
            finish()
        }, SPLASH_TIME_OUT.toLong())
    }
}