package com.example.bondoman.ui.settings

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.bondoman.R
import com.example.bondoman.database.KeyStoreManager
import com.example.bondoman.databinding.FragmentSettingsBinding
import com.example.bondoman.ui.login.LoginActivity

class SettingsFragment : Fragment() {

    companion object {
        fun newInstance() = SettingsFragment()
    }

    private lateinit var viewModel: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentSettingsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)
        binding.mailTransactionButton.setOnClickListener{
            sendEmail()
        }
        binding.logoutButton.setOnClickListener {
            logout()
        }

        return binding.root
    }
    @SuppressLint("IntentReset")
    private fun sendEmail(){
        val mIntent = Intent(Intent.ACTION_SEND)
        mIntent.data = Uri.parse("mailto:")
        mIntent.type = "text/plain"
        mIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("h.septianradityo@gmail.com"))
        mIntent.putExtra(Intent.EXTRA_SUBJECT, "Laporan Keuangan (DD-MM-YYYY)")
        mIntent.putExtra(Intent.EXTRA_TEXT, "{Placeholder}")
        try {
            //start email intent
            startActivity(Intent.createChooser(mIntent, "Choose Email Client..."))
        }
        catch (e: Exception){
            //get and show exception message
            Log.e("Email Error", e.message.toString())
        }
    }

    private fun logout() {
        KeyStoreManager.getInstance(requireContext()).removeToken()
        KeyStoreManager.getInstance(requireContext()).removeEmail()
        val intent = Intent(activity, LoginActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}