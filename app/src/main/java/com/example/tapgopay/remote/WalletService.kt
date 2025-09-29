package com.example.tapgopay.remote

import com.example.tapgopay.data.SHA256Hash
import com.example.tapgopay.data.alice
import com.example.tapgopay.data.signData
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.security.PrivateKey
import java.time.LocalDateTime
import java.util.Locale

data class Wallet(
    @SerializedName("user_id") val userId: Int,
    val username: String,
    @SerializedName("phone_no") val phoneNo: String,
    @SerializedName("wallet_address") val walletAddress: String,
    @SerializedName("wallet_name") val walletName: String,
    @SerializedName("initial_deposit") val initialDeposit: Double,
    @SerializedName("is_active") var isActive: Boolean,
    @SerializedName("created_at") val createdAt: LocalDateTime,
    val balance: Double,
)

data class WalletOwner(
    val username: String = "",
    val email: String = "",
    @SerializedName("phone_no") val phoneNo: String = "",
    @SerializedName("wallet_address") val walletAddress: String = "",
)

fun WalletOwner.toContact(): Contact {
    return Contact(
        name = this.username,
        phoneNo = this.phoneNo,
    )
}

data class Contact(
    val name: String,
    val phoneNo: String,
)

fun Contact.toWalletOwner(): WalletOwner {
    return WalletOwner(
        username = this.name,
        phoneNo = this.phoneNo,
    )
}

data class TransactionRequest(
    val sender: String,
    val receiver: String,
    val amount: Double,
    var fee: Double = 0.0,
    @SerializedName("timestamp") val timestamp: String = LocalDateTime.now().toString(),
    var signature: String = "", // Base64-encoded string
    @SerializedName("public_key_hash") var pubKeyHash: String = "", // Base64-encoded string
)

fun TransactionRequest.signPayload(privKey: PrivateKey): ByteArray? {
    val payload =
        String.format(Locale.US, "%s|%s|%.2f|%.2f|%s", sender, receiver, amount, fee, timestamp)
    val hashedPayload = SHA256Hash(payload.toByteArray()) ?: return null
    val signature = signData(hashedPayload, privKey)
    return signature
}

fun TransactionRequest.asResult(): TransactionResult {
    return TransactionResult(
        sender = WalletOwner(walletAddress = this.sender),
        receiver = WalletOwner(walletAddress = this.receiver),
        amount = this.amount,
        fee = this.fee,
        status = TransactionStatus.UNKNOWN,
        timestamp = this.timestamp,
    )
}

enum class TransactionStatus {
    @SerializedName("pending")
    PENDING,

    @SerializedName("confirmed")
    CONFIRMED,

    @SerializedName("rejected")
    REJECTED,

    @SerializedName("unknown")
    UNKNOWN,
}

data class TransactionResult(
    @SerializedName("transaction_code") val transactionCode: String? = null,
    val sender: WalletOwner,
    val receiver: WalletOwner,
    val amount: Double,
    val fee: Double,
    val status: TransactionStatus?,
    val timestamp: String,
    @SerializedName("created_at") val createdAt: String? = null,
)

fun TransactionResult.isSuccessful(): Boolean {
    return this.transactionCode != null
}

fun TransactionResult.isIncoming(): Boolean {
    val receiversCardNo: String = this.receiver.walletAddress
    val receiversPhoneNo: String = this.receiver.phoneNo
    val isIncomingTransaction =
        (receiversCardNo.isNotEmpty() && receiversCardNo == alice.walletAddress) ||
                (receiversPhoneNo.isNotEmpty() && receiversPhoneNo == alice.phoneNo)

    return isIncomingTransaction
}

data class CreateWalletRequest(
    @SerializedName("wallet_name") val walletName: String,
    @SerializedName("total_owners") val totalOwners: Int,
    @SerializedName("num_signatures") val numSignatures: Int,
)

data class TransactionFee(
    @SerializedName("min_amount") val minAmount: Double,
    @SerializedName("max_amount") val maxAmount: Double,
    val fee: Double,
)

interface WalletService {
    @POST("/new-wallet")
    suspend fun newWallet(@Body request: CreateWalletRequest): Response<Wallet>

    @GET("/wallets")
    suspend fun getAllWallets(): Response<List<Wallet>>

    @GET("/wallets/{wallet_address}")
    suspend fun getWallet(@Path("wallet_address") walletAddress: String): Response<Wallet>

    @POST("/wallets/{wallet_address}/freeze")
    suspend fun freezeWallet(@Path("wallet_address") walletAddress: String): Response<MessageResponse>

    @POST("/wallets/{wallet_address}/activate")
    suspend fun activateWallet(@Path("wallet_address") walletAddress: String): Response<MessageResponse>

    @POST("/transfer-funds")
    suspend fun sendMoney(@Body req: TransactionRequest): Response<TransactionResult>

    @GET("/all-transaction-fees")
    suspend fun getAllTransactionFees(): Response<List<TransactionFee>>
}