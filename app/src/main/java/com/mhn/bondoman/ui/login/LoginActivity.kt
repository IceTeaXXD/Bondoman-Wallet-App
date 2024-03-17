package com.mhn.bondoman.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mhn.bondoman.MainActivity
import com.mhn.bondoman.api.BondomanApi
import com.mhn.bondoman.database.KeyStoreManager
import com.mhn.bondoman.databinding.ActivityLoginBinding
import com.mhn.bondoman.databinding.NoNetworkLayoutBinding
import com.mhn.bondoman.models.LoginBody
import com.mhn.bondoman.utils.NetworkAdapter
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity(), NetworkAdapter.NetworkListener {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var noNetworkBinding: NoNetworkLayoutBinding
    private lateinit var networkAdapter: NetworkAdapter
    private lateinit var backButton: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        networkAdapter = NetworkAdapter(applicationContext)
        networkAdapter.subscribe(this)
        supportActionBar?.hide()

        if(networkAdapter.isNetworkConnected()) {
            binding = ActivityLoginBinding.inflate(layoutInflater)
            setContentView(binding.root)

            val email = binding.etEmail
            val password = binding.etPassword
            val buttonLogin = binding.btnLogin

            buttonLogin.setOnClickListener {
                if (email.text.toString().isNotEmpty() && password.text.toString().isNotEmpty()) {
                    login(email.text.toString(), password.text.toString())
                } else {
                    Toast.makeText(this, "Email and password must not be empty", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }else{
            noNetworkBinding = NoNetworkLayoutBinding.inflate(layoutInflater)
            setContentView(noNetworkBinding.root)

            backButton = noNetworkBinding.backButton
            backButton.setOnClickListener {
                super.onBackPressed()
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun login(email: String, password: String){
        val loginBody = LoginBody(email, password)
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = BondomanApi.getInstance().login(loginBody)
                if (response.token.isNotEmpty()) {
                    Log.i("Login", "TOKEN: ${response.token}")
                    KeyStoreManager.getInstance(this@LoginActivity).createNewKeys("token")
                    KeyStoreManager.getInstance(this@LoginActivity).createNewKeys("email")
                    KeyStoreManager.getInstance(this@LoginActivity).setToken(response.token)
                    KeyStoreManager.getInstance(this@LoginActivity).setEmail(email)
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java)
                        .putExtra("authenticated", true))
                    finish()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("Login", "Error: ${e.message}")
                    Toast.makeText(this@LoginActivity, "User not found, please try again", Toast.LENGTH_SHORT).show()
                    binding.etPassword.text?.clear()
                    binding.tvError.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onNetworkAvailable() {
        runOnUiThread {
            binding = ActivityLoginBinding.inflate(layoutInflater)
            setContentView(binding.root)
            val email = binding.etEmail
            val password = binding.etPassword
            val buttonLogin = binding.btnLogin

            buttonLogin.setOnClickListener {
                if (email.text.toString().isNotEmpty() && password.text.toString().isNotEmpty()) {
                    login(email.text.toString(), password.text.toString())
                } else {
                    Toast.makeText(this, "Email and password must not be empty", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    override fun onNetworkLost() {
        runOnUiThread {
            noNetworkBinding = NoNetworkLayoutBinding.inflate(layoutInflater)
            setContentView(noNetworkBinding.root)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        networkAdapter.unsubscribe(this)
    }
}