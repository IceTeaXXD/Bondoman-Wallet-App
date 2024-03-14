package com.example.bondoman.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bondoman.MainActivity
import com.example.bondoman.api.BondomanApi
import com.example.bondoman.api.KeyStoreManager
import com.example.bondoman.databinding.ActivityLoginBinding
import com.example.bondoman.models.LoginBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        val loginBody = LoginBody(email, password)
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = BondomanApi.getInstance().login(loginBody)
                if (response.token.isNotEmpty()) {
                    Log.i("Login", "TOKEN: ${response.token}")
                    val keyStoreManager = KeyStoreManager(this@LoginActivity)
                    keyStoreManager.createNewKeys("token")
                    keyStoreManager.saveToken("token", response.token)
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java)
                        .putExtra("authenticated", true))
                    finish()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("Login", "Error: ${e.message}")
                    Toast.makeText(this@LoginActivity, "User not found, please try again", Toast.LENGTH_SHORT).show()
                    binding.etPassword.text.clear()
                }
            }
        }
    }
}