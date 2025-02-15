package com.example.tapgopay.data

import com.google.gson.annotations.SerializedName
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import com.example.tapgopay.BuildConfig
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

val client = OkHttpClient.Builder()
    .addInterceptor(AuthInterceptor())
    .callTimeout(Duration.ofSeconds(30))
    .build()

private val retrofitBuilder = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BuildConfig.REMOTE_URL)
    .client(client)
    .build()

data class LoginCredentials(
    val email: String,
    val password: String
)

data class LoginResponse(
    val message: String,
    @SerializedName("jwt_token") val jwtToken: String? = null
)

data class RegisterCredentials(
    val username: String,
    val email: String,
    val password: String,
)

data class RegisterResponse(
    val message: String,
    val errors: Map<String, List<String>>? = null
)

interface ApiService {
    @POST("/android/api/login/")
    suspend fun loginUser(@Body loginCredentials: LoginCredentials): Response<LoginResponse>

    @POST("/android/api/register/")
    suspend fun registerUser(@Body registerCredentials: RegisterCredentials): Response<RegisterResponse>
}

object Api {
    val retrofitService: ApiService by lazy {
        retrofitBuilder.create(ApiService::class.java)
    }
}

