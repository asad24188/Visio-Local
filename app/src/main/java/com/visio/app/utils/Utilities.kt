package com.wayprotect.app.utils

import android.R
import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.visio.app.DataModel.Login.User
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class Utilities(context: Context) {

    private val context: Context? = null
    var dialog: ProgressDialog? = null

    fun makeToast(ctx: Context?, msg: String?) {
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show()
    }



    fun showProgressDialog(ctx: Context?, msg: String?) {
        dialog = ProgressDialog(ctx)
        dialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        dialog!!.setMessage(msg)
        dialog!!.setIndeterminate(true)
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.show()
    }

    fun hideProgressDialog() {
        if (dialog != null && dialog!!.isShowing()) {
            dialog!!.cancel()
            dialog = null
        }
    }

    fun saveInt(context: Context, key: String?, value: Int) {

        val sharedPref = context.getSharedPreferences("visioshared", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putInt(key, value)
        editor.apply()
    }


    fun getInt(context: Context, key: String?): Int {
        val sharedPref = context.getSharedPreferences("visioshared", Context.MODE_PRIVATE)
        return sharedPref.getInt(key, 0)
    }


    fun saveString(
        context: Context, key: String, value: String
    ) {
        val sharedPref = context.getSharedPreferences("visioshared", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getString(context: Context, key: String): String {
        val sharedPref = context.getSharedPreferences("visioshared", Context.MODE_PRIVATE)
        return sharedPref.getString(key, "").toString()
    }


    fun clearSharedPref(context: Context) {
        val sharedPref =
            context.getSharedPreferences("visioshared", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.clear()
        editor.apply()
    }

    fun printHashKey(Context: Context) {
        try {
            val info = Context.packageManager.getPackageInfo(
                Context.packageName,
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey: String = String(Base64.encode(md.digest(), 0))
                Log.i(TAG, "printHashKey() Hash Key: $hashKey")
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.e(TAG, "printHashKey()", e)
        } catch (e: Exception) {
            Log.e(TAG, "printHashKey()", e)
        }
    }
    

    fun saveBoolean(
        context: Context, key: String, value: Boolean
    ) {
        val sharedPref = context.getSharedPreferences("visioshared", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getBoolean(context: Context, key: String): Boolean {
        val sharedPref = context.getSharedPreferences("visioshared", Context.MODE_PRIVATE)
        return sharedPref.getBoolean(key, false)
    }



    fun isConnectingToInternet(context: Context): Boolean {
        val connectivity = context.getSystemService(
            AppCompatActivity.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if (connectivity != null) {
            val info = connectivity.allNetworkInfo
            if (info != null) for (i in info.indices) if (info[i].state == NetworkInfo.State.CONNECTED) {
                return true
            }
        }
        return false
    }

    fun changeDateFormate(date: String?, from: String?, to: String?): String? {
        var finalDate = date

//        SimpleDateFormat date_formate = new SimpleDateFormat("MM/dd/yyyy");
        val date_formate = SimpleDateFormat(from)
        var datee: Date? = null
        try {
            datee = date_formate.parse(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

//        SimpleDateFormat df = new SimpleDateFormat("MMM-dd-yyyy");
        val df = SimpleDateFormat(to)
        val calendar = Calendar.getInstance()
        calendar.time = datee
        finalDate = df.format(calendar.time)
        return finalDate
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun setWhiteBars(activity: Activity) {
        val window = activity.window
        val view = window.decorView
        view.systemUiVisibility = view.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        view.systemUiVisibility =
            view.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        window.navigationBarColor = activity.resources.getColor(R.color.white)
        window.statusBarColor = activity.resources.getColor(R.color.white)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun setBlackBars(activity: Activity) {
        val window = activity.window
        val view = window.decorView
        view.systemUiVisibility =
            view.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        view.systemUiVisibility =
            view.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
        window.navigationBarColor = activity.resources.getColor(R.color.black)
        window.statusBarColor = activity.resources.getColor(R.color.black)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun setCustomStatusAndNavColor(activity: Activity, statusColor: Int, navColor: Int) {
        val window = activity.window
        val view = window.decorView
        view.systemUiVisibility = view.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        view.systemUiVisibility =
            view.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        window.navigationBarColor = activity.resources.getColor(navColor)
        window.statusBarColor = activity.resources.getColor(statusColor)
    }


    fun getUserId(context: Context): String {

        val gsonn = Gson()

        val sharedPref = context.getSharedPreferences("visioshared", Context.MODE_PRIVATE)
        val jsonn: String =  sharedPref.getString("user", "").toString()
        val obj: User = gsonn.fromJson(jsonn, User::class.java)
        return obj.id.toString()

    }


    fun getUserEmail(context: Context): String {

        val gsonn = Gson()

        val sharedPref = context.getSharedPreferences("visioshared", Context.MODE_PRIVATE)
        val jsonn: String =  sharedPref.getString("user", "").toString()
        val obj: User = gsonn.fromJson(jsonn, User::class.java)
        return obj.email.toString()

    }

}