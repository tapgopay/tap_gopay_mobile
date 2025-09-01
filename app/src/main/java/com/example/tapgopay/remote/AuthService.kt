package com.example.tapgopay.remote

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


data class LoginDto(
    val email: String, val password: String
)

data class MessageResponse(
    val message: String, val data: Any? = null, val errors: Map<String, String>? = null
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

interface AuthService {
    @POST("/api/login")
    suspend fun loginUser(@Body loginRequest: LoginDto): Response<MessageResponse>

    @POST("/api/register")
    suspend fun registerUser(@Body registerResponse: RegisterDto): Response<MessageResponse>

    @POST("/api/send-email-verification")
    suspend fun sendEmailVerification(@Body email: EmailDto): Response<MessageResponse>

    @POST("/api/verify-auth")
    suspend fun verifyAuth(): Response<MessageResponse>

    @POST("/api/forgot-password")
    suspend fun forgotPassword(@Body email: EmailDto): Response<MessageResponse>

    @POST("/api/reset-password")
    suspend fun resetPassword(@Body passwordResetDto: PasswordResetDto): Response<MessageResponse>
}


