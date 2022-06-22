package com.example.visio.Auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import com.example.visio.R
import com.example.visio.databinding.ActivityCreatePasswordBinding

class CreatePasswordActivity : AppCompatActivity() {
    private var passwordVisibile = false
lateinit var binding: ActivityCreatePasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityCreatePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.hidePassword.setOnClickListener {
            passwordVisibile = !passwordVisibile
            showPassword(passwordVisibile)
        }
        binding.hidePassword2.setOnClickListener {
            passwordVisibile = !passwordVisibile
            showPassword2(passwordVisibile)
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
    private fun showPassword2(isShow: Boolean) {
        if (isShow) {
            binding.editPassword2.transformationMethod =
                HideReturnsTransformationMethod.getInstance()
            binding.hidePassword2.setImageResource(R.drawable.visible_icon)
        } else {
            binding.editPassword2.transformationMethod = PasswordTransformationMethod.getInstance()
            binding.hidePassword2.setImageResource(R.drawable.ic_pass_invesible)
        }
        binding.editPassword2.setSelection(binding.editPassword2.text.toString().length)
    }
}