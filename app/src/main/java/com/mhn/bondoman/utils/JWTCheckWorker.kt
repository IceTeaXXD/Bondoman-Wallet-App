package com.mhn.bondoman.utils

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.mhn.bondoman.api.BondomanApi
import com.mhn.bondoman.database.KeyStoreManager
import com.mhn.bondoman.models.LoginBody
import kotlinx.coroutines.runBlocking

class JWTCheckWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {
    override fun doWork(): Result {
        try {
            Log.d("JWTCheckWorker", "doWork: ")
            val tokenValue = KeyStoreManager.getInstance(applicationContext).getToken()
            if (tokenValue == null) {
                return Result.failure()
            } else {
                runBlocking {
                    // Check if token is still valid
                    val jwtResponse = BondomanApi.getInstance().token("Bearer $tokenValue")
                    val statusCode = jwtResponse.code()
                    if (statusCode == 200) {
                        Log.i("JWTCheck", "Token is still valid")
                        return@runBlocking Result.success()
                    } else {
                        // re login
                        val loginResponse = BondomanApi.getInstance().login(
                            LoginBody(
                                KeyStoreManager.getInstance(applicationContext).getEmail()!!,
                                KeyStoreManager.getInstance(applicationContext).getPassword()!!
                            )
                        )
                        val loginStatusCode = loginResponse.code()
                        if (loginStatusCode == 200) {
                            val token = loginResponse.body()!!.token
                            KeyStoreManager.getInstance(applicationContext).setToken(token)
                            Log.i("JWTCheck", "Token refreshed")
                            return@runBlocking Result.success()
                        } else {
                            Log.i("JWTCheck", "Re-Login failed")
                            return@runBlocking Result.failure()
                        }
                    }
                }
                return Result.failure()
            }
        } catch (e: Exception) {
            return Result.failure()
        }
    }
}