package com.mhn.bondoman.database

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyPairGenerator

class KeyStoreManager(context: Context) {

    companion object {
        @Volatile
        private var instance: KeyStoreManager? = null

        fun getInstance(context: Context): KeyStoreManager {
            if (instance == null) {
                instance = KeyStoreManager(context)
            }
            return instance!!
        }
    }

    private val sharedPreferences = context.getSharedPreferences("Bondoman", Context.MODE_PRIVATE)

    fun createNewKeys(alias: String) {
        val keyPairGenerator = KeyPairGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore"
        )

        keyPairGenerator.initialize(
            KeyGenParameterSpec.Builder(
                alias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                .build()
        )

        keyPairGenerator.generateKeyPair()
    }

    fun getToken(): String? {
        return sharedPreferences.getString("token", null)
    }

    fun setToken(token: String) {
        sharedPreferences.edit().putString("token", token).apply()
    }

    fun removeToken() {
        sharedPreferences.edit().remove("token").apply()
    }

    fun getEmail(): String? {
        return sharedPreferences.getString("email", null)
    }

    fun setEmail(email: String) {
        sharedPreferences.edit().putString("email", email).apply()
    }

    fun removeEmail() {
        sharedPreferences.edit().remove("email").apply()
    }
}