package com.example.bondoman

import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.appcompat.app.ActionBar
import com.example.bondoman.databinding.ActivityMainBinding
import com.example.bondoman.ui.login.LoginActivity

class MainActivity : AppCompatActivity() {

private lateinit var binding: ActivityMainBinding
private var authenticated: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        authenticated = intent.getBooleanExtra("authenticated", false)
         binding = ActivityMainBinding.inflate(layoutInflater)
         setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_transactions,
                R.id.navigation_scan,
                R.id.navigation_graph,
                R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Set custom action bar
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setDisplayShowCustomEnabled(true)
        actionBar?.setDisplayShowTitleEnabled(false)
        actionBar?.setCustomView(R.layout.action_bar_title_layout)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val title = when (destination.id) {
                R.id.navigation_home -> "Home"
                R.id.navigation_transactions -> "Transactions"
                R.id.navigation_scan -> "Scan"
                R.id.navigation_graph -> "Graph"
                R.id.navigation_settings -> "Settings"
                else -> "Bondoman"
            }
            actionBar?.customView?.findViewById<android.widget.TextView>(R.id.action_bar_title)?.text = title
        }
    }

    override fun onStart() {
        super.onStart()

        // TODO: Validate if Key is Still valid
        if(!authenticated){
            authenticated = true
            val intent = Intent(
                this,
                LoginActivity::class.java
            )

            startActivity(intent)
            finish()
        }
    }
}