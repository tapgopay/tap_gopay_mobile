package com.example.tapgopay.remote

import android.content.Context
import com.example.tapgopay.BuildConfig
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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

fun getRetrofit(userId: String, context: Context): Retrofit {
    val cookieJar = CustomCookieJar(userId, context)

    val client =
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
            .cookieJar(cookieJar)
            .callTimeout(Duration.ofSeconds(30))
            .build()

    val gson = GsonBuilder()
        .registerTypeAdapter(
            LocalDateTime::class.java,
            object : JsonDeserializer<LocalDateTime> {
                override fun deserialize(
                    json: JsonElement?,
                    typeOfT: Type?,
                    context: JsonDeserializationContext?
                ): LocalDateTime {
                    return LocalDateTime.parse(
                        json?.asString,
                        DateTimeFormatter.ISO_OFFSET_DATE_TIME
                    )
                }
            }
        ).create()

    val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create(gson))
        .baseUrl(BuildConfig.REMOTE_URL)
        .client(client)
        .build()

    return retrofit
}

object Api {
    fun getAuthApi(userId: String, context: Context): AuthService {
        val retrofit = getRetrofit(userId, context)
        return retrofit.create(AuthService::class.java)
    }

    fun getWalletApi(userId: String, context: Context): WalletService {
        val retrofit = getRetrofit(userId, context)
        return retrofit.create(WalletService::class.java)
    }
}
