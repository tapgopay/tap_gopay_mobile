package com.example.tapgopay.remote

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    @SerializedName("phone_no") val phone: String,
    @SerializedName("public_key") val publicKey: String,
)

data class LoginRequest(
    val email: String,
    val password: String,
    @SerializedName("public_key") val publicKey: String,
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

    @POST("/auth/register")
    suspend fun registerUser(@Body registerRequest: RegisterRequest): Response<MessageResponse>

    @GET("/auth/verify-login")
    suspend fun verifyLogin(): Response<MessageResponse>

    @POST("/auth/forgot-password")
    suspend fun forgotPassword(@Body email: EmailRequest): Response<MessageResponse>

    @POST("/auth/reset-password")
    suspend fun resetPassword(@Body passwordResetDto: PasswordResetRequest): Response<MessageResponse>
}


