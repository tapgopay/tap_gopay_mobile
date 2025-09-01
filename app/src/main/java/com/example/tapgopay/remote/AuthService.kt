package com.example.tapgopay.remote

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


data class LoginRequest(
    val email: String,
    val password: String,
)

data class MessageResponse(
    val message: String,
    val errors: Map<String, String> = mapOf(),
)

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    @SerializedName("phone_no") val phoneNumber: String,
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
    suspend fun registerUser(@Body registerResponse: RegisterRequest): Response<MessageResponse>

    @POST("/auth/send-email-verification")
    suspend fun sendEmailVerification(@Body email: EmailRequest): Response<MessageResponse>

    @POST("/auth/verify-auth")
    suspend fun verifyAuth(): Response<MessageResponse>

    @POST("/auth/forgot-password")
    suspend fun forgotPassword(@Body email: EmailRequest): Response<MessageResponse>

    @POST("/auth/reset-password")
    suspend fun resetPassword(@Body passwordResetDto: PasswordResetRequest): Response<MessageResponse>
}


