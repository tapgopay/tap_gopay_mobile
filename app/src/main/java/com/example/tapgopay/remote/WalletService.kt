package com.example.tapgopay.remote

import com.example.tapgopay.data.alice
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.time.LocalDateTime


data class Wallet(
    @SerializedName("user_id") val userId: Int,
    val username: String,
    @SerializedName("phone_no") val phoneNo: String,
    @SerializedName("wallet_address") val walletAddress: String,
    @SerializedName("initial_deposit") val initialDeposit: Double,
    @SerializedName("is_active") var isActive: Boolean,
    @SerializedName("created_at") val createdAt: LocalDateTime,
    val balance: Double,
)

data class Contact(
    val username: String = "",
    @SerializedName("wallet_address") val walletAddress: String = "",
    @SerializedName("phone_no") val phoneNo: String = "",
)

data class TransactionRequest(
    val sender: String,
    val receiver: String,
    val amount: Double,
    @SerializedName("created_at") val createdAt: LocalDateTime = LocalDateTime.now(),
    var signature: String, // Base64-encoded string
)

fun TransactionRequest.asResult(): TransactionResult {
    return TransactionResult(
        sender = Contact(walletAddress = this.sender),
        receiver = Contact(walletAddress = this.receiver),
        amount = this.amount,
        createdAt = this.createdAt,
        signature = this.signature,
    )
}

data class TransactionResult(
    @SerializedName("transaction_id") val transactionId: String? = null, // Will be omitted if null
    val sender: Contact,
    val receiver: Contact,
    val amount: Double,
    @SerializedName("created_at") val createdAt: LocalDateTime = LocalDateTime.now(),
    var signature: String, // Base64-encoded string
)

fun TransactionResult.isSuccessful(): Boolean {
    return this.transactionId != null
}

fun TransactionResult.isIncoming(): Boolean {
    val receiversCardNo: String = this.receiver.walletAddress
    val receiversPhoneNo: String = this.receiver.phoneNo
    val isIncomingTransaction = (receiversCardNo.isNotEmpty() && receiversCardNo == alice.walletAddress) ||
            (receiversPhoneNo.isNotEmpty() && receiversPhoneNo == alice.phoneNo)

    return isIncomingTransaction
}

interface WalletService {
    @POST("/new-wallet")
    suspend fun newWallet(): Response<Wallet>

    @GET("/wallets")
    suspend fun getAllWallets(): Response<List<Wallet>>

    @GET("/wallets/{wallet_address}")
    suspend fun getWallet(@Path("wallet_address") walletAddress: String): Response<Wallet>

    @POST("/wallets/{wallet_address}/freeze")
    suspend fun freezeWallet(@Path("wallet_address") walletAddress: String): Response<MessageResponse>

    @POST("/wallets/{wallet_address}/activate")
    suspend fun activateWallet(@Path("wallet_address") walletAddress: String): Response<MessageResponse>

    @POST("/transfer-funds")
    suspend fun transferFunds(@Body req: TransactionRequest): Response<TransactionResult>
}