package com.example.visio.Auth

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.Toast
import com.example.visio.DataModel.Login.LoginResponse
import com.example.visio.R
import com.example.visio.Services.ApiClient
import com.example.visio.databinding.ActivitySignUpBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignUpBinding
    private var passwordVisibile = false
    lateinit var progressDialog: ProgressDialog
    val apiClient: ApiClient = ApiClient()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvlogin.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    SigninActivity::class.java
                )
            )
        }
        binding.btnsignup.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.editPassword.text.toString()
            val confirm_pass = binding.editPassword2.text.toString()
            signupApi( email, password, confirm_pass)
            showProgressDialog()

        }

        binding.hidePassword.setOnClickListener {
            passwordVisibile = !passwordVisibile
            showPassword(passwordVisibile)
        }
        binding.hidePassword1.setOnClickListener {
            passwordVisibile = !passwordVisibile
            showPassword1(passwordVisibile)
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

    private fun showPassword1(isShow: Boolean) {
        if (isShow) {
            binding.editPassword2.transformationMethod =
                HideReturnsTransformationMethod.getInstance()
            binding.hidePassword1.setImageResource(R.drawable.visible_icon)
        } else {
            binding.editPassword2.transformationMethod = PasswordTransformationMethod.getInstance()
            binding.hidePassword1.setImageResource(R.drawable.ic_pass_invesible)
        }
        binding.editPassword2.setSelection(binding.editPassword2.text.toString().length)
    }
    //signupApi

    private fun signupApi(
        email: String,
        password: String,
        confirmPass: String
    ) {
        apiClient.getApiService()
            .signUp(email, password, confirmPass, "android", "token")
            .enqueue(object : Callback<LoginResponse?> {
                override fun onResponse(
                    call: Call<LoginResponse?>,
                    response: Response<LoginResponse?>
                ) {
                    val signupResponse = response.body()
                    if (response.isSuccessful){
                        Toast.makeText(applicationContext, signupResponse!!.message, Toast.LENGTH_SHORT).show()
                        val intent = Intent(applicationContext, SigninActivity::class.java)
                        intent.putExtra("email",email)
                        intent.putExtra("password",password)
                        intent.putExtra("confirm password",confirmPass)
                        startActivity(intent)
                        finish()
                        progressDialog.dismiss()

                    }else {
                        Toast.makeText(applicationContext, signupResponse!!.message, Toast.LENGTH_SHORT).show()
                        Log.d("mess", signupResponse.message)
                        progressDialog.dismiss()

                    }
                }

                override fun onFailure(call: Call<LoginResponse?>, t: Throwable) {
                    Toast.makeText(applicationContext,"Check Your Internet", Toast.LENGTH_SHORT).show()
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