package com.example.tapgopay.data

enum class TransactionType {
    Send, Receive
}

data class TransferDetails(
    val sender: Contact,
    val receiver: Contact,
    val amount: Float,
    val success: Boolean,
)