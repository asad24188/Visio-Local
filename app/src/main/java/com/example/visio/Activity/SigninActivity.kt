package com.example.visio.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import com.example.visio.R
import com.example.visio.databinding.ActivitySignUpBinding
import com.example.visio.databinding.ActivitySigninBinding

class SigninActivity : AppCompatActivity() {
    lateinit var binding: ActivitySigninBinding
    private var passwordVisibile = false
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
            startActivity(
                Intent(
                    this,
                    HomeActivity::class.java
                )
            )
            finish()
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

}