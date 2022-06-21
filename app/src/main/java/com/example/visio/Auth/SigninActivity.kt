package com.example.visio.Auth

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Toast
import com.example.visio.Activity.HomeActivity
import com.example.visio.DataModel.Login.LoginResponse
import com.example.visio.R
import com.example.visio.Services.ApiClient
import com.example.visio.databinding.ActivitySigninBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SigninActivity : AppCompatActivity() {
    lateinit var binding: ActivitySigninBinding
    private var passwordVisibile = false
    lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signup.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    SignUpActivity::class.java
                )
            )
        }
        binding.btnsignIn.setOnClickListener {
            var email=binding.emailEdit.text.toString()
            var password=binding.editPassword.text.toString()
            loginApi(email,password)
            showProgressDialog()
        }

        binding.hidePassword.setOnClickListener {
            passwordVisibile = !passwordVisibile
            showPassword(passwordVisibile)
        }
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
        val apiClient: ApiClient = ApiClient()
        apiClient.getApiService().login(email, password, "android", "token")
            .enqueue(object : Callback<LoginResponse?> {
                override fun onResponse(
                    call: Call<LoginResponse?>,
                    response: Response<LoginResponse?>
                ) {
                    if (response.isSuccessful) {
                        startActivity(Intent(applicationContext, HomeActivity::class.java))
                        finish()
                        overridePendingTransition(0, 0)
                        progressDialog.dismiss()
                    } else {
                        progressDialog.dismiss()
                        Toast.makeText(
                            applicationContext,
                            response.body()?.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse?>, t: Throwable) {
                    Toast.makeText(applicationContext, "Wrong Entries", Toast.LENGTH_SHORT).show()

                }
            })
    }
    private fun showProgressDialog() {
        progressDialog= ProgressDialog(this)
//                    progressDialog.setTitle("Please Wait...")
        progressDialog.setMessage("Please Wait...")
//                    progressDialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.show()
    }
}