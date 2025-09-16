package com.example.tapgopay.data

import com.example.tapgopay.remote.Contact
import com.example.tapgopay.remote.TransactionResult
import com.example.tapgopay.remote.Wallet
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
val alice = Contact("Alice", "+254700111111")
val bob = Contact("Bob", "12345678910", "+254700222222")
val charlie = Contact("Charlie", "12345678910", "+254700333333")
val diana = Contact("Diana", "12345678910", "+254700444444")

fun generateFakeTransaction(): TransactionResult {
    val contacts = mutableListOf<Contact>(alice, bob, charlie, diana)
    val sender = contacts.random()
    val receiver = if (sender == alice) {
        contacts.remove(alice)
        contacts.random()
    } else {
        alice
    }

    val amount = Random.nextDouble() * Random.nextInt(1000)
    val success = Random.nextBoolean()
    val transactionId: String? = if (success) generateRandomTransactionId() else null

    val randomDays = Random.nextInt(365 * 2)
    var timestamp = LocalDateTime.now().minusDays(randomDays.toLong())
    val randomHours = Random.nextInt(24)
    timestamp = timestamp.plusHours(randomHours.toLong())

    return TransactionResult(
        transactionId = transactionId,
        sender = sender,
        receiver = receiver,
        amount = amount,
        timestamp = timestamp.toString(),
        signature = "",
    )
}

fun generateFakeWallet(id: Int): Wallet {
    val usernames = listOf("alice", "bob", "charlie", "diana", "edward")
    val username = usernames.random()
    val phoneNo = "+2547${Random.nextInt(1000000, 9999999)}"
    val walletAddress = "0x" + List(16) { "0123456789abcdef".random() }.joinToString("")
    val initialDeposit = Random.nextDouble(100.0, 5000.0)
    val isActive = Random.nextBoolean()
    val timestamp = LocalDateTime.now().minusDays(Random.nextLong(1, 365))
    val balance = initialDeposit + Random.nextDouble(0.0, 2000.0)

    return Wallet(
        userId = id,
        username = username,
        phoneNo = phoneNo,
        walletAddress = walletAddress,
        initialDeposit = initialDeposit,
        isActive = isActive,
        createdAt = timestamp,
        balance = balance
    )
}