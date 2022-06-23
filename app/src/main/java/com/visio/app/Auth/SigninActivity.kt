package com.visio.app.Auth

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Toast
import com.google.gson.Gson
import com.visio.app.Activity.HomeActivity
import com.visio.app.DataModel.Login.LoginResponse
import com.visio.app.DataModel.Login.User
import com.visio.app.R
import com.visio.app.Services.ApiClient
import com.visio.app.databinding.ActivitySigninBinding
import com.wayprotect.app.utils.Utilities
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SigninActivity : AppCompatActivity() {


    lateinit var binding: ActivitySigninBinding
    private lateinit var utilities: Utilities
    lateinit var context: Context
    private lateinit var user: User

    private var passwordVisibile = false
    lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initt()
        clicks()

    }

    private fun clicks() {

        binding.signup.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    SignUpActivity::class.java
                )
            )
        }
        binding.tvforgot.setOnClickListener {
            startActivity(Intent(this,ForgetPasswordActivity::class.java))
        }
        binding.btnsignIn.setOnClickListener {
            var email=binding.emailEdit.text.toString()
            var password=binding.editPassword.text.toString()

            if (!email.isEmpty()){
                if (!password.isEmpty()){

                    loginApi(email,password)

                }else{
                    utilities.makeToast(context,"Enter Password")
                }
            }else{
                utilities.makeToast(context,"Email is empty")
            }


        }

        binding.hidePassword.setOnClickListener {
            passwordVisibile = !passwordVisibile
            showPassword(passwordVisibile)
        }

    }

    private fun initt() {

        context = this
        if (!::utilities.isInitialized) utilities = Utilities(this)

    }

    private fun showPassword(isShow: Boolean) {
        if (isShow) {
            binding.editPassword.transformationMethod =
                HideReturnsTransformationMethod.getInstance()
            binding.hidePassword.setImageResource(R.drawable.visible_icon)
        } else {
            binding.editPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            binding.hidePassword.setImageResource(R.drawable.ic_pass_invesible)
        }
        binding.editPassword.setSelection(binding.editPassword.text.toString().length)
    }
//loginapi fun

    private fun loginApi(email: String, password: String) {

        utilities.showProgressDialog(context,"Processing ...")
        val apiClient: ApiClient = ApiClient()
        apiClient.getApiService().login(email, password, "android", "token")
            .enqueue(object : Callback<LoginResponse?> { override fun onResponse(call: Call<LoginResponse?>, response: Response<LoginResponse?>) {

                utilities.hideProgressDialog()

                    if (response.isSuccessful) {

                        startActivity(Intent(applicationContext, HomeActivity::class.java))
                        overridePendingTransition(0, 0)

                        user = response.body()!!.user
                        val gson = Gson()
                        val json = gson.toJson(user)
                        utilities.saveString(context, "user", json)
                        utilities.saveString(context, "logged_in", "true")
                        finish()

                    } else {
                        progressDialog.dismiss()
                        utilities.makeToast(context,response.body()?.message)

                    }
                }
                override fun onFailure(call: Call<LoginResponse?>, t: Throwable) {
                    utilities.makeToast(context,t.message.toString())

                }
            })
    }
}