package com.example.visio.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import com.example.visio.R
import com.example.visio.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignUpBinding
    private var passwordVisibile = false
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
            startActivity(
                Intent(
                    this,
                    SigninActivity::class.java
                )
            )
            finish()
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
            binding.editCunfPass.transformationMethod =
                HideReturnsTransformationMethod.getInstance()
            binding.hidePassword1.setImageResource(R.drawable.visible_icon)
        } else {
            binding.editCunfPass.transformationMethod = PasswordTransformationMethod.getInstance()
            binding.hidePassword1.setImageResource(R.drawable.ic_pass_invesible)
        }
        binding.editCunfPass.setSelection(binding.editCunfPass.text.toString().length)
    }
}