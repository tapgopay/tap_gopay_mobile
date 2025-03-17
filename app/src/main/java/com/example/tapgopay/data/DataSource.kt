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

data class LoginDto(
    val email: String,
    val password: String
)

data class ApiResponse(
    val message: String,
    val data: Any? = null,
    val errors: Map<String, String>? = null
)

data class RegisterDto(
    val username: String,
    val email: String,
    val password: String,
    @SerializedName("phone_no") val phoneNumber: String,
)

data class EmailDto(
    val email: String,
)

data class PasswordResetDto(
    @SerializedName("password_reset_token") val passwordResetToken: String,
    val email: String,
    @SerializedName("new_password") val newPassword: String,
)

interface ApiService {
    @POST("/api/login")
    suspend fun loginUser(@Body loginRequest: LoginDto): Response<ApiResponse>

    @POST("/api/register")
    suspend fun registerUser(@Body registerResponse: RegisterDto): Response<ApiResponse>

    @POST("/api/send-email-verification")
    suspend fun sendEmailVerification(@Body email: EmailDto): Response<ApiResponse>

    @POST("/api/verify-auth")
    suspend fun verifyAuth(): Response<ApiResponse>

    @POST("/api/forgot-password")
    suspend fun forgotPassword(@Body email: EmailDto): Response<ApiResponse>

    @POST("/api/reset-password")
    suspend fun resetPassword(@Body passwordResetDto: PasswordResetDto): Response<ApiResponse>
}


