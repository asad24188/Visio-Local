package com.visio.app.Auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.visio.app.databinding.ActivityForgetPasswordBinding

class ForgetPasswordActivity : AppCompatActivity() {
    lateinit var binding: ActivityForgetPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityForgetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.goBack.setOnClickListener {
            finish()
            overridePendingTransition(0, 0)
        }
        binding.btnsend.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    VerificationActivity::class.java
                )
            )
        }
    }
}