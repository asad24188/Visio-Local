package com.visio.app.Auth

import android.Manifest
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.permissionx.guolindev.PermissionX
import com.visio.app.Activity.HomeActivity
import com.visio.app.R
import com.wayprotect.app.utils.Utilities

class SplashActivity : AppCompatActivity() {

    private val SPLASH_TIME_OUT = 3000
    private lateinit var utilities: Utilities
    lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        requestPermission()
        initt()

        Handler().postDelayed({

            var loggedIn = utilities.getString(context,"logged_in")
            if (loggedIn.equals("true")){

                val mainIntent = Intent(this@SplashActivity,HomeActivity ::class.java)
                startActivity(mainIntent)
                finish()
            }else{

                val mainIntent = Intent(this@SplashActivity, SigninActivity::class.java)
                startActivity(mainIntent)
                finish()

            }

        }, SPLASH_TIME_OUT.toLong())
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


    private fun initt() {
        context = this
        if (!::utilities.isInitialized) utilities = Utilities(this)
    }
}