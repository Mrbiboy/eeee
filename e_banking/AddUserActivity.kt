package com.example.e_banking

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AddUserActivity : AppCompatActivity() {

    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_user)

        // Initialize fields
        val nomInput: EditText = findViewById(R.id.nom_input)
        val prenomInput: EditText = findViewById(R.id.prenom_input)
        val cinInput: EditText = findViewById(R.id.cin_input)
        val addressInput: EditText = findViewById(R.id.address_input)
        val dateNaissanceInput: EditText = findViewById(R.id.date_naissance_input)
        val emailInput: EditText = findViewById(R.id.email_input)
        val isAdminCheckbox: CheckBox = findViewById(R.id.is_admin_checkbox)
        val addUserButton: Button = findViewById(R.id.add_user_button)

        // Configure Retrofit with logging
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val httpClient = OkHttpClient.Builder().addInterceptor(logging).build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2/")  // Make sure this points to the correct server IP
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()

        apiService = retrofit.create(ApiService::class.java)

        // Add a user when the button is clicked
        addUserButton.setOnClickListener {
            val nom = nomInput.text.toString().trim()
            val prenom = prenomInput.text.toString().trim()
            val cin = cinInput.text.toString().trim()
            val address = addressInput.text.toString().trim()
            val dateNaissance = dateNaissanceInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val isAdmin = isAdminCheckbox.isChecked  // Boolean

            Log.d("AddUserActivity", "Attempting to add user")
            Log.d("AddUserActivity", "nom: $nom, prenom: $prenom, cin: $cin, address: $address, dateNaissance: $dateNaissance, email: $email, isAdmin: $isAdmin")

            if (nom.isNotEmpty() && prenom.isNotEmpty() && cin.isNotEmpty() && address.isNotEmpty() &&
                dateNaissance.isNotEmpty() && email.isNotEmpty()) {
                addUser(nom, prenom, cin, address, dateNaissance, email, isAdmin)
            } else {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addUser(
        nom: String, prenom: String, cin: String, address: String, dateNaissance: String,
        email: String, isAdmin: Boolean
    ) {
        Log.d("AddUserActivity", "Sending API request")

        // Convert `isAdmin` from Boolean to Int (1 for true, 0 for false)
        val isAdminInt = if (isAdmin) 1 else 0

        apiService.registerUser(nom, prenom, cin, address, dateNaissance, email, isAdmin)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@AddUserActivity, "Utilisateur ajouté avec succès", Toast.LENGTH_SHORT).show()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("AddUserActivity", "Error response: Code ${response.code()}, Body: $errorBody")
                        Toast.makeText(this@AddUserActivity, "Erreur lors de l'ajout", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("AddUserActivity", "API call failed: ${t.message}")
                    Toast.makeText(this@AddUserActivity, "Erreur réseau : ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
