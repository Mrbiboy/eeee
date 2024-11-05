package com.example.e_banking

data class User(
    val id: Int,
    val nom: String,
    val prenom: String,
    val cin: String,
    val address: String,
    val dateNaissance: String,
    val email: String,
    val accountNumber: String,
    val isAdmin: Int // Keep this as an Int

) {
    // Helper method to convert isAdmin to Boolean
    fun isAdminAsBoolean(): Boolean {
        return isAdmin == 1
    }
}
data class Useer(
    val id: Int,
    val nom: String,
    val username: String,
    val accountNumber: String
)


