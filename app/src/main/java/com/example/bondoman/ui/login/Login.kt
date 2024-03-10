package com.example.bondoman.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.bondoman.MainActivity
import com.example.bondoman.databinding.ActivityLoginBinding

class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val email = binding.etEmail
        val password = binding.etPassword
        val buttonLogin = binding.btnLogin

        buttonLogin.setOnClickListener{
            login(email.text.toString(), password.text.toString())
        }
    }

    private fun login(email: String, password: String){
        val intent = Intent(
            this,
            MainActivity::class.java
        )

        startActivity(intent)
        finish()
    }
}