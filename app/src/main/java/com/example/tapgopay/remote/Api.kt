package com.example.tapgopay.remote

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

object Api {
    private val client =
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
            .cookieJar(CustomCookieJar())
            .callTimeout(Duration.ofSeconds(30))
            .build()

    private val gson = GsonBuilder()
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

    private val retrofitBuilder by lazy {
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(BuildConfig.REMOTE_URL)
            .client(client)
            .build()
    }

    val authService: AuthService by lazy {
        retrofitBuilder
            .create(AuthService::class.java)
    }

    val creditCardsService: CreditCardService by lazy {
        retrofitBuilder
            .create(CreditCardService::class.java)
    }
}
