package com.example.e_banking

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.*
import org.json.JSONArray
import java.io.IOException

class TransactionActivity : AppCompatActivity() {

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var transactionUserAdapter: TransactionUserAdapter
    private var userList: ArrayList<User> = arrayListOf()
    private var selectedUser: User? = null

    private lateinit var amountInput: EditText
    private lateinit var executeTransactionButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction)

        userRecyclerView = findViewById(R.id.user_recycler_view)
        amountInput = findViewById(R.id.amount)
        executeTransactionButton = findViewById(R.id.execute_transaction)

        userRecyclerView.layoutManager = LinearLayoutManager(this)

        getUsersFromAPI()

        executeTransactionButton.setOnClickListener {
            val amount = amountInput.text.toString().toDoubleOrNull()
            if (selectedUser != null && amount != null) {
                executeTransaction(
                    selectedUser!!.id,
                    amount,
                    selectedUser!!.isAdmin
                )
            } else {
                Toast.makeText(
                    this,
                    "Sélectionnez un utilisateur et entrez un montant valide",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun getUsersFromAPI() {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("http://10.0.2.2/api/getUsers.php")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(
                        this@TransactionActivity,
                        "Erreur de connexion",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val jsonData = response.body?.string()
                if (jsonData != null) {
                    val jsonArray = JSONArray(jsonData)
                    for (i in 0 until jsonArray.length()) {
                        val userJson = jsonArray.getJSONObject(i)
                        val user = User(
                            id = userJson.getInt("id"),
                            nom = userJson.getString("nom"),
                            prenom = userJson.getString("prenom"),
                            cin = userJson.getString("cin"),
                            address = userJson.getString("address"),
                            dateNaissance = userJson.getString("date_naissance"),
                            email = userJson.getString("email"),
                            accountNumber = userJson.getString("accountNumber"), // Make sure accountNumber is retrieved
                            isAdmin = userJson.getInt("isAdmin")
                        )

                        userList.add(user)
                    }

                    runOnUiThread {
                        transactionUserAdapter = TransactionUserAdapter(
                            this@TransactionActivity,
                            userList
                        ) { selectedUser ->
                            this@TransactionActivity.selectedUser = selectedUser
                            Toast.makeText(
                                this@TransactionActivity,
                                "Utilisateur sélectionné: ${selectedUser.nom}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        userRecyclerView.adapter = transactionUserAdapter
                    }
                }
            }
        })
    }

    private fun executeTransaction(
        userId: Int,
        amount: Double,
        isAdmin: Int
    ) {
        val client = OkHttpClient()

        val requestBody = FormBody.Builder()
            .add("userId", userId.toString())
            .add("amount", amount.toString())
            .add("isAdmin", isAdmin.toString())
            .build()

        val request = Request.Builder()
            .url("http://10.0.2.2/api/executeTransaction.php")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(
                        this@TransactionActivity,
                        "Erreur de connexion",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(
                            this@TransactionActivity,
                            "Transaction réussie",
                            Toast.LENGTH_SHORT
                        ).show()
                        amountInput.text.clear()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(
                            this@TransactionActivity,
                            "Échec de la transaction",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }
}
