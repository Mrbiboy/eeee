package com.example.e_banking

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserActivity : AppCompatActivity() {

    private lateinit var apiService: ApiService
    private var userId: Int = -1  // Identifiant unique de l'utilisateur

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)
        val finButton: Button = findViewById(R.id.service_virement_btn)

        finButton.setOnClickListener {
            val intent = Intent(this, VirementActivity::class.java)
            startActivity(intent)
        }

        // Récupérer l'ID de l'utilisateur passé depuis l'activité précédente
        userId = intent.getIntExtra("userId", -1)  // Utilisation de la valeur passée via Intent
        if (userId == -1) {
            Toast.makeText(this, "Invalid user ID", Toast.LENGTH_SHORT).show()
            finish()  // Fermer l'activité si userId est invalide
            return
        }

        // Initialiser Retrofit pour les appels API
        setupRetrofit()

        // Récupérer les détails de l'utilisateur et le solde
        getUserDetails(userId)
        getUserBalance(userId)


    }

    // Configurer Retrofit pour l'API
    private fun setupRetrofit() {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2/")  // Remplacer par l'IP du serveur local
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)
    }


    // Récupérer les détails de l'utilisateur depuis l'API avec userId
    private fun getUserDetails(userId: Int) {
        apiService.getUserDetails(userId).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) {
                        val userTextView = findViewById<TextView>(R.id.welcome_message)
                        userTextView.text = "Name: ${user.nom}\nEmail: ${user.email}"
                    } else {
                        Toast.makeText(this@UserActivity, "User not found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@UserActivity, "Error retrieving user info", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@UserActivity, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Récupérer le solde de l'utilisateur via l'API
    private fun getUserBalance(userId: Int) {
        apiService.getBalance(userId).enqueue(object : Callback<BalanceResponse> {
            override fun onResponse(call: Call<BalanceResponse>, response: Response<BalanceResponse>) {
                if (response.isSuccessful) {
                    val balanceResponse = response.body()
                    if (balanceResponse != null) {
                        val balanceTextView = findViewById<TextView>(R.id.amount_balance)
                        balanceTextView.text = "${balanceResponse.balance}$"
                    } else {
                        Toast.makeText(this@UserActivity, "Balance not found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@UserActivity, "Error retrieving balance", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<BalanceResponse>, t: Throwable) {
                Toast.makeText(this@UserActivity, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Retirer un montant du compte de l'utilisateur via l'API
    private fun withdrawAmountFromAccount(userId: Int, amount: Double) {
        apiService.withdraw(userId, amount).enqueue(object : Callback<BalanceResponse> {
            override fun onResponse(call: Call<BalanceResponse>, response: Response<BalanceResponse>) {
                if (response.isSuccessful) {
                    val balanceResponse = response.body()
                    if (balanceResponse != null) {
                        // Mettre à jour l'affichage du solde
                        val balanceTextView = findViewById<TextView>(R.id.amount_balance)
                        balanceTextView.text = "${balanceResponse.balance}$"
                        Toast.makeText(this@UserActivity, "Withdrawal successful", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@UserActivity, "Error during withdrawal", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@UserActivity, "Error during withdrawal", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<BalanceResponse>, t: Throwable) {
                Toast.makeText(this@UserActivity, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
