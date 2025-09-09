package com.example.tapgopay.data

import com.example.tapgopay.remote.Contact
import com.example.tapgopay.remote.TransactionResult
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

fun generateRandomTransactions(): List<TransactionResult> {
    val bob = Contact("Bob", "+254700222222")
    val charlie = Contact("Charlie", "+254700333333")
    val diana = Contact("Diana", "+254700444444")
    val transactions = mutableListOf<TransactionResult>()
    val today = LocalDateTime.now()

    for (i in 0..10) {
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
        var timestamp = today.minusDays(randomDays.toLong())
        val randomHours = Random.nextInt(24)
        timestamp = timestamp.plusHours(randomHours.toLong())

        val transaction = TransactionResult(
            transactionId = transactionId,
            sender = sender,
            receiver = receiver,
            amount = amount,
            createdAt = timestamp,
            signature = "",
        )
        transactions.add(transaction)
    }

    return transactions
}