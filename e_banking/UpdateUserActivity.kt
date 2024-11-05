package com.example.e_banking

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import java.io.IOException

class UpdateUserActivity : AppCompatActivity() {

    private lateinit var nomInput: EditText
    private lateinit var prenomInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var addressInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var updateButton: Button
    private var userId: Int = 0  // This will hold the user ID passed from the previous activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_user)

        // Retrieve data passed from UserAdapter via Intent
        userId = intent.getIntExtra("userId", 0)
        val userNom = intent.getStringExtra("userNom")
        val userPrenom = intent.getStringExtra("userPrenom")
        val userEmail = intent.getStringExtra("userEmail")
        val userAddress = intent.getStringExtra("userAddress")
        val userPassword = intent.getStringExtra("userPassword")

        // Initialize the EditText fields
        nomInput = findViewById(R.id.nom_input)
        prenomInput = findViewById(R.id.prenom_input)
        emailInput = findViewById(R.id.email_input)
        addressInput = findViewById(R.id.address_input)
        passwordInput = findViewById(R.id.password_input)
        updateButton = findViewById(R.id.update_user_button)

        // Populate the fields with the current user data
        nomInput.setText(userNom)
        prenomInput.setText(userPrenom)
        emailInput.setText(userEmail)
        addressInput.setText(userAddress)
        passwordInput.setText(userPassword)

        // Set up the click listener to update the user when the button is clicked
        updateButton.setOnClickListener {
            updateUserInAPI(userId)
        }
    }

    private fun updateUserInAPI(userId: Int) {
        val client = OkHttpClient()

        // Prepare the form data for the request
        val requestBody = FormBody.Builder()
            .add("id", userId.toString())  // Send the user ID
            .add("nom", nomInput.text.toString())
            .add("prenom", prenomInput.text.toString())
            .add("email", emailInput.text.toString())
            .add("address", addressInput.text.toString())
            .add("password", passwordInput.text.toString())  // Add updated password
            .build()

        // Make the HTTP POST request to update the user
        val request = Request.Builder()
            .url("http://10.0.2.2/updateUser.php")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Show error message if the request fails
                runOnUiThread {
                    Toast.makeText(this@UpdateUserActivity, "Erreur de connexion", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    // Show success message and close the activity
                    runOnUiThread {
                        Toast.makeText(this@UpdateUserActivity, "Utilisateur mis à jour avec succès", Toast.LENGTH_SHORT).show()
                        finish()  // Go back to the previous activity
                    }
                } else {
                    // Show error message if the update fails
                    runOnUiThread {
                        Toast.makeText(this@UpdateUserActivity, "Échec de la mise à jour", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}
