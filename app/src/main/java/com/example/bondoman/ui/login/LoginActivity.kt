package com.example.bondoman.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bondoman.MainActivity
import com.example.bondoman.api.BackendService
import com.example.bondoman.api.LoginBody
import com.example.bondoman.api.ServiceFactory
import com.example.bondoman.databinding.ActivityLoginBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val email = binding.etEmail
        val password = binding.etPassword
        val buttonLogin = binding.btnLogin

        buttonLogin.setOnClickListener{
            if(email.text.toString().isNotEmpty() && password.text.toString().isNotEmpty()) {
                login(email.text.toString(), password.text.toString())
            }else{
                Toast.makeText(this, "Email and password must not be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun login(email: String, password: String){
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://pbd-backend-2024.vercel.app")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val serviceFactory = ServiceFactory(retrofit.create(BackendService::class.java))
        val loginBody = LoginBody(email, password)

        GlobalScope.launch(Dispatchers.IO) {
            val response = serviceFactory.login(loginBody)
            Log.i("Login", "TOKEN: ${response.token}")
            if (response.token.isNotEmpty()) {
                //TODO Save the Token
                startActivity(Intent(this@LoginActivity, MainActivity::class.java)
                    .putExtra("authenticated", true))
                finish()
            }else{
                Toast.makeText(this@LoginActivity,"User not found, please try again", Toast.LENGTH_SHORT).show()
            }
        }
    }
}