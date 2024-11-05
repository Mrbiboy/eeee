package com.example.e_banking

data class Transaction(
    val transactionId: Int,
    val userId: Int,
    val amount: Double,
    val transactionDate: String // or use Date type if preferred
)