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

        if (networkAdapter.isNetworkConnected()) {
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
        } else {
            noNetworkBinding = NoNetworkLayoutBinding.inflate(layoutInflater)
            setContentView(noNetworkBinding.root)

            backButton = noNetworkBinding.backButton
            backButton.setOnClickListener {
                super.onBackPressed()
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun login(email: String, password: String) {
        val loginBody = LoginBody(email, password)
        GlobalScope.launch(Dispatchers.IO) {
            val response = BondomanApi.getInstance().login(loginBody)
            val statusCode = response.code()
            if (statusCode == 200) {
                val token = response.body()!!.token
                Log.i("Login", "TOKEN: $token")
                KeyStoreManager.getInstance(this@LoginActivity).createNewKeys("token")
                KeyStoreManager.getInstance(this@LoginActivity).createNewKeys("email")
                KeyStoreManager.getInstance(this@LoginActivity).createNewKeys("password")
                KeyStoreManager.getInstance(this@LoginActivity).setToken(token)
                KeyStoreManager.getInstance(this@LoginActivity).setEmail(email)
                KeyStoreManager.getInstance(this@LoginActivity).setPassword(password)
                startActivity(
                    Intent(this@LoginActivity, MainActivity::class.java)
                        .putExtra("authenticated", true)
                )
                finish()
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@LoginActivity,
                        "User not found, please try again",
                        Toast.LENGTH_SHORT
                    ).show()
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