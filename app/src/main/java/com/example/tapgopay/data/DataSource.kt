package com.example.tapgopay.data

import android.content.Context
import com.example.tapgopay.BuildConfig
import com.google.gson.annotations.SerializedName
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.time.Duration

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val originalRequest = chain.request()

        // Add request headers to request
        val newRequest = originalRequest.newBuilder()
            .addHeader("Authorization", "Bearer ${BuildConfig.ANDROID_API_KEY}")
            .addHeader("Content-Type", "application/json")
            .build()
        return chain.proceed(newRequest)
    }
}

fun getRetrofitBuilder(context: Context): Retrofit {
    val client = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor())
        .cookieJar(CustomCookieJar(context))
        .callTimeout(Duration.ofSeconds(30))
        .build()

    return Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BuildConfig.REMOTE_URL)
        .client(client)
        .build()
}

data class LoginRequest(
    val email: String,
    val password: String
)

data class ApiResponse(
    val message: String,
    val data: Any? = null,
    val errors: Map<String, String>? = null
)

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    @SerializedName("phone_no") val phoneNumber: String,
)

interface ApiService {
    @POST("/api/login")
    suspend fun loginUser(@Body loginRequest: LoginRequest): Response<ApiResponse>

    @POST("/api/signup")
    suspend fun registerUser(@Body registerResponse: RegisterRequest): Response<ApiResponse>

    @POST("/api/send-verification-email")
    suspend fun sendEmailVerification(@Body email: String): Response<ApiResponse>

    @POST("/api/verify-auth")
    suspend fun verifyAuth(): Response<ApiResponse>
}


