package com.example.tapgopay.remote

import com.example.tapgopay.data.alice
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.time.LocalDateTime


data class CreditCard(
    @SerializedName("user_id") val userId: Int,
    val username: String,
    @SerializedName("phone_no") val phoneNo: String,
    @SerializedName("card_no") val cardNo: String,
    @SerializedName("initial_deposit") val initialDeposit: Double,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("created_at") val createdAt: LocalDateTime,
    val balance: Double,
)

data class Contact(
    val name: String = "",
    @SerializedName("card_no") val cardNo: String = "",
    @SerializedName("phone_no") val phoneNo: String = "",
)

data class Transaction(
    @SerializedName("transaction_id") val transactionId: String? = null, // Will be omitted if null
    val sender: String,
    val receiver: String,
    val amount: Double,
    @SerializedName("created_at") val createdAt: LocalDateTime = LocalDateTime.now(),
    var signature: String, // Base64-encoded string
)

fun Transaction.isSuccessful(): Boolean {
    return this.transactionId != null
}

fun Transaction.isIncoming(): Boolean {
    return this.receiver == alice.cardNo || this.receiver == alice.phoneNo
}

interface CreditCardService {
    @POST("/new-credit-card")
    suspend fun newCreditCard(): Response<CreditCard>

    @GET("/credit-cards")
    suspend fun getAllCreditCards(): Response<List<CreditCard>>

    @GET("/credit-cards/{card_no}")
    suspend fun getCreditCard(@Path("card_no") cardNo: String): Response<CreditCard>

    @POST("/credit-cards/{card_no}/freeze")
    suspend fun freezeCreditCard(@Path("card_no") cardNo: String): Response<MessageResponse>

    @POST("/credit-cards/{card_no}/activate")
    suspend fun activateCreditCard(@Path("card_no") cardNo: String): Response<MessageResponse>

    @POST("/transfer-funds")
    suspend fun transferFunds(@Body req: Transaction): Response<Transaction>
}