package com.example.bondoman.database
import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyPairGenerator
import java.security.KeyStore

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

    fun getToken(alias: String): String? {
        return sharedPreferences.getString(alias, null)
    }

    fun saveToken(alias: String, token: String) {
        sharedPreferences.edit().putString(alias, token).apply()
    }

    fun removeToken(alias: String) {
        sharedPreferences.edit().remove(alias).apply()
    }
}