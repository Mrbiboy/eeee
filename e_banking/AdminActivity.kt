package com.example.e_banking

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AdminActivity : AppCompatActivity() {

    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        val usersManagButton: Button = findViewById(R.id.service1_btn)
        val transacButton: Button = findViewById(R.id.service2_btn)
        val roleButton: Button = findViewById(R.id.service3_btn)
        val finButton: Button = findViewById(R.id.service4_btn)

        usersManagButton.setOnClickListener {
            val intent = Intent(this, UserManagementActivity::class.java)
            startActivity(intent)
        }
        transacButton.setOnClickListener {
            val intent = Intent(this, TransactionActivity::class.java) // Assuming TransactionActivity exists
            startActivity(intent)
        }
        roleButton.setOnClickListener {
            val intent = Intent(this, RoleManagementActivity::class.java) // Assuming RoleManagementActivity exists
            startActivity(intent)
        }
        finButton.setOnClickListener {
            val intent = Intent(this, FinancialReportsActivity::class.java) // Assuming FinancialOverviewActivity exists
            startActivity(intent)
        }

        // Configuring Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2/")  // Use your local server IP
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        // Optionally call this to fetch users if needed
        getUsers()
    }

    // Method to retrieve all users
    private fun getUsers() {
        apiService.getUsers().enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) {
                    val users = response.body()
                    if (users != null) {
                        for (user in users) {
                            Log.d("AdminActivity", "User: ${user.nom}, Email: ${user.email}") // Removed username
                        }
                    }
                } else {
                    Toast.makeText(
                        this@AdminActivity,
                        "Erreur lors de la récupération des utilisateurs",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                Log.e("AdminActivity", "Erreur réseau : ${t.message}")
                Toast.makeText(this@AdminActivity, "Erreur réseau : ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
