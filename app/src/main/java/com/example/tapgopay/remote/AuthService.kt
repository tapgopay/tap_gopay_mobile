package com.example.tapgopay.remote

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part


data class LoginRequest(
    val email: String,
    val signature: String,
)

data class MessageResponse(
    val message: String,
)

data class EmailRequest(
    val email: String,
)

data class PasswordResetRequest(
    @SerializedName("password_reset_token") val passwordResetToken: String,
    val email: String,
    @SerializedName("new_password") val newPassword: String,
)

interface AuthService {
    @POST("/auth/login")
    suspend fun loginUser(@Body loginRequest: LoginRequest): Response<MessageResponse>

    @Multipart
    @POST("/auth/register")
    suspend fun registerUser(
        @Part file: MultipartBody.Part,
        @Part("username") username: RequestBody,
        @Part("email") email: RequestBody,
        @Part("phone_no") phoneNo: RequestBody,
    ): Response<MessageResponse>

    @GET("/auth/verify-login")
    suspend fun verifyLogin(): Response<MessageResponse>

    @POST("/auth/forgot-password")
    suspend fun forgotPassword(@Body email: EmailRequest): Response<MessageResponse>

    @POST("/auth/reset-password")
    suspend fun resetPassword(@Body passwordResetDto: PasswordResetRequest): Response<MessageResponse>
}


