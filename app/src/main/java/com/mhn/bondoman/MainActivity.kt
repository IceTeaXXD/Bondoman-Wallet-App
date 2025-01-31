package com.mhn.bondoman

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.mhn.bondoman.databinding.ActivityMainBinding
import com.mhn.bondoman.ui.login.LoginActivity
import com.mhn.bondoman.utils.JWTAdapter
import com.mhn.bondoman.utils.NetworkAdapter

class MainActivity : AppCompatActivity(), NetworkAdapter.NetworkListener, JWTAdapter.JWTListener {

    private lateinit var binding: ActivityMainBinding

    private lateinit var networkAdapter: NetworkAdapter
    private lateinit var jwtAdapter: JWTAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        networkAdapter = NetworkAdapter.getInstance(applicationContext)
        jwtAdapter = JWTAdapter.getInstance(this)

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        binding.navView?.setupWithNavController(navController)
        binding.navViewDrawer?.setupWithNavController(navController)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_transactions,
                R.id.navigation_scan,
                R.id.navigation_graph,
                R.id.navigation_settings,
                R.id.navigation_twibbon
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        // Set custom action bar
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setDisplayShowCustomEnabled(true)
        actionBar?.setDisplayShowTitleEnabled(false)
        actionBar?.setCustomView(R.layout.action_bar_title_layout)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
        )
        actionBar?.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.app_background))

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val title = when (destination.id) {
                R.id.navigation_transactions -> getString(R.string.title_transactions)
                R.id.navigation_scan -> getString(R.string.title_scan)
                R.id.navigation_graph -> getString(R.string.title_graph)
                R.id.navigation_settings -> getString(R.string.title_settings)
                R.id.navigation_twibbon -> getString(R.string.title_twibbon)
                else -> "Bondoman"
            }
            actionBar?.customView?.findViewById<android.widget.TextView>(R.id.action_bar_title)?.text =
                title
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onStart() {
        super.onStart()
        if (!jwtAdapter.isJWTValidated()) {
            val intent = Intent(
                this,
                LoginActivity::class.java
            )
            startActivity(intent)
            finish()
        } else {
            networkAdapter.subscribe(this)
            jwtAdapter.subscribe(this)
        }
    }

    override fun onNetworkAvailable() {
        Toast.makeText(applicationContext, "Network Connection Restored", Toast.LENGTH_SHORT).show()
    }

    override fun onNetworkLost() {
        Toast.makeText(applicationContext, "Network Connection Lost", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        networkAdapter.unsubscribe(this)
        jwtAdapter.unsubscribe(this)
    }

    override fun onJWTValid() {
        // Do nothing
    }

    override fun onJWTInvalid() {
        Toast.makeText(applicationContext, "Session expired, please re-login.", Toast.LENGTH_SHORT)
            .show()
        val intent = Intent(
            this,
            LoginActivity::class.java
        )
        startActivity(intent)
        finish()
    }
}
