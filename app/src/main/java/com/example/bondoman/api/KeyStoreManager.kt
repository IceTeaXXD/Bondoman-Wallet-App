package com.example.bondoman.api
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

    fun encryptData(alias: String, token: String): String {
        val publicKey = keyStore.getCertificate(alias).publicKey

        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding").apply {
            init(Cipher.ENCRYPT_MODE, publicKey)
        }

        val bytes = cipher.doFinal(token.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    fun decryptData(alias: String, encryptedData: String): String {
        val privateKeyEntry = keyStore.getEntry(alias, null) as KeyStore.PrivateKeyEntry
        val privateKey = privateKeyEntry.privateKey

        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding").apply {
            init(Cipher.DECRYPT_MODE, privateKey)
        }

        val bytes = Base64.decode(encryptedData, Base64.DEFAULT)
        val decryptedBytes = cipher.doFinal(bytes)
        return String(decryptedBytes, Charsets.UTF_8)
    }

    fun getToken(alias: String): String? {
        val encryptedToken = sharedPreferences.getString(alias, null)
        return encryptedToken?.let { decryptData(alias, it) }
    }

    fun saveToken(alias: String, token: String) {
        val encryptedToken = encryptData(alias, token)
        sharedPreferences.edit().putString(alias, encryptedToken).apply()
    }
}