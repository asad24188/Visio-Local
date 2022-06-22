package com.example.visio.Auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.visio.databinding.ActivityVerificationBinding

class VerificationActivity : AppCompatActivity() {
    lateinit var binding: ActivityVerificationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.goBack.setOnClickListener {
            finish()
            overridePendingTransition(0, 0)
        }
        binding.verifyBtn.setOnClickListener {
            startActivity(
                Intent(this, CreatePasswordActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
        }
    }
}