package com.example.e_banking

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    val success: Boolean,
    val username: String?,
    val isAdmin: Boolean,
    val userId: Int,  // Assuming you send userId from the PHP backend
    val message: String?
)


// Classe pour représenter la réponse de l'API lors de la récupération du solde
data class BalanceResponse(
    @SerializedName("balance") val balance: Double
)


data class TransferResponse(
    val success: Boolean,
    val message: String
)
data class VerificationResponse(
    val success: Boolean
)
data class UpdateResponse(
    val success: Boolean,
    val message: String
)
