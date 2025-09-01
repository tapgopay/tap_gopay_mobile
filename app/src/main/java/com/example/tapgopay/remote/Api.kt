package com.example.tapgopay.remote

import com.example.tapgopay.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Duration

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val originalRequest = chain.request()

        // Add request headers to request
        val newRequest = originalRequest.newBuilder()
            .addHeader("Authorization", "Bearer ${BuildConfig.ANDROID_API_KEY}")
            .addHeader("Content-Type", "application/json").build()
        return chain.proceed(newRequest)
    }
}

object Api {
    private val client =
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
            .cookieJar(CustomCookieJar())
            .callTimeout(Duration.ofSeconds(30))
            .build()

    val authService: AuthService by lazy {
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BuildConfig.REMOTE_URL)
            .client(client)
            .build()
            .create(AuthService::class.java)
    }
}
