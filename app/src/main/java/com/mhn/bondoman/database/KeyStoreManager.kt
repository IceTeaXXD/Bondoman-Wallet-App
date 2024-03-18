package com.mhn.bondoman.database

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyPairGenerator
import java.security.KeyStore
import javax.crypto.Cipher

class KeyStoreManager(context: Context) {
    private val keyStore: KeyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }

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

    private fun encryptData(alias: String, token: String): String {
        val publicKey = keyStore.getCertificate(alias).publicKey

        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding").apply {
            init(Cipher.ENCRYPT_MODE, publicKey)
        }

        val bytes = token.toByteArray(Charsets.UTF_8)
        val encryptedBytes = cipher.doFinal(bytes)
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }

    private fun decryptData(alias: String, encryptedData: String): String {
        val privateKeyEntry = keyStore.getEntry(alias, null) as KeyStore.PrivateKeyEntry
        val privateKey = privateKeyEntry.privateKey

        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding").apply {
            init(Cipher.DECRYPT_MODE, privateKey)
        }

        val bytes = Base64.decode(encryptedData, Base64.DEFAULT)
        val decryptedBytes = cipher.doFinal(bytes)
        return String(decryptedBytes, Charsets.UTF_8)
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

    fun getPassword(): String? {
        val encryptedPassword = sharedPreferences.getString("password", null)
        return encryptedPassword?.let { decryptData("password", it) }
    }

    fun setPassword(password: String) {
        val encryptedPassword = encryptData("password", password)
        sharedPreferences.edit().putString("password", encryptedPassword).apply()
    }

    fun removePassword() {
        sharedPreferences.edit().remove("password").apply()
    }

    fun getExpiry(): Long {
        return sharedPreferences.getLong("expiry", 0)
    }

    fun setExpiry(expiry: Long) {
        sharedPreferences.edit().putLong("expiry", expiry).apply()
    }

    fun removeExpiry() {
        sharedPreferences.edit().remove("expiry").apply()
    }
}