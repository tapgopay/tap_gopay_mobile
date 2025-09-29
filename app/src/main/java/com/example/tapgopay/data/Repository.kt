package com.example.tapgopay.data

import com.example.tapgopay.R
import com.example.tapgopay.remote.TransactionResult
import com.example.tapgopay.remote.TransactionStatus
import com.example.tapgopay.remote.Wallet
import com.example.tapgopay.remote.WalletOwner
import java.time.LocalDateTime
import java.util.UUID
import kotlin.random.Random


fun generateRandomTransactionId(): String {
    val randomUUID: String = UUID.randomUUID()
        .toString()
        .split("-")
        .take(2)
        .joinToString(separator = "-")
        .uppercase()
    return "TXN-$randomUUID"
}

// Alice will act as the current account holder
val usernames = listOf("Alice", "Bob", "Charlie", "Diana", "Edward")

val alice = WalletOwner(username = "Alice", phoneNo = "+254700111111")
val bob = WalletOwner(username = "Bob", walletAddress = "12345678910", phoneNo = "+254700222222")
val charlie =
    WalletOwner(username = "Charlie", walletAddress = "12345678910", phoneNo = "+254700333333")
val diana =
    WalletOwner(username = "Diana", walletAddress = "12345678910", phoneNo = "+254700444444")

fun generateFakeTransaction(): TransactionResult {
    val walletOwners = mutableListOf<WalletOwner>(alice, bob, charlie, diana)
    val sender = walletOwners.random()
    val receiver = if (sender == alice) {
        walletOwners.remove(alice)
        walletOwners.random()
    } else {
        alice
    }

    val amount = Random.nextDouble() * Random.nextInt(100)
    val status = listOf(TransactionStatus.CONFIRMED, TransactionStatus.PENDING).random()
    val transactionCode: String? = if (status == TransactionStatus.CONFIRMED) {
        generateRandomTransactionId()
    } else {
        null
    }

    val randomDays = Random.nextInt(365 * 2)
    var timestamp = LocalDateTime.now().minusDays(randomDays.toLong())
    val randomHours = Random.nextInt(24)
    timestamp = timestamp.plusHours(randomHours.toLong())

    return TransactionResult(
        transactionCode = transactionCode,
        sender = sender,
        receiver = receiver,
        amount = amount,
        fee = Random.nextDouble() * amount,
        status = status,
        timestamp = timestamp.toString(),
    )
}

fun generateWalletAddress(): String {
    val walletAddress = List(12) { ('0'..'9').random() }
        .chunked(4) { it.joinToString("") }              // Groups of 4 into string
        .joinToString(" ")
    return walletAddress
}

fun generateFakeWallet(id: Int): Wallet {
    val username = usernames.random()
    val phoneNo = "+2547${Random.nextInt(1000000, 9999999)}"
    val walletAddress = generateWalletAddress()
    val walletName = List(8) { "abcdefghijklmnopqrstuvwxyz".random() }.joinToString("")
    val initialDeposit = Random.nextDouble(100.0, 5000.0)
    val isActive = Random.nextBoolean()
    val timestamp = LocalDateTime.now().minusDays(Random.nextLong(1, 365))
    val balance = initialDeposit + Random.nextDouble(0.0, 2000.0)

    return Wallet(
        userId = id,
        username = username,
        phoneNo = phoneNo,
        walletAddress = walletAddress,
        walletName = walletName,
        initialDeposit = initialDeposit,
        isActive = isActive,
        createdAt = timestamp,
        balance = balance,
    )
}

fun randomProfilePic(): Int {
    val profilePics = listOf(
        R.drawable.man, R.drawable.man_2,
        R.drawable.woman, R.drawable.woman_2,
    )
    return profilePics.random()
}