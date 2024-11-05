package com.example.e_banking

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class VirementActivity : AppCompatActivity() {

    private lateinit var beneficiaryUsernameInput: EditText
    private lateinit var searchBeneficiaryButton: Button
    private lateinit var beneficiaryFoundText: TextView
    private lateinit var currentAccountText: TextView
    private lateinit var transferAmountInput: EditText
    private lateinit var validateTransferButton: Button

    private lateinit var apiService: ApiService
    private var foundBeneficiaryId: Int? = null
    private var senderId: Int = -1  // ID de l'utilisateur connecté

    // Récupérer l'ID de l'utilisateur depuis SharedPreferences
    private fun getUserId(): Int {
        val sharedPref = getSharedPreferences("e_banking_prefs", Context.MODE_PRIVATE)
        return sharedPref.getInt("userId", -1)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_virement)

        senderId = getUserId()
        Log.d("VirementActivity", "Sender ID: $senderId")

        if (senderId == -1) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Initialisation des vues
        beneficiaryUsernameInput = findViewById(R.id.beneficiary_username_input)
        searchBeneficiaryButton = findViewById(R.id.search_beneficiary_button)
        beneficiaryFoundText = findViewById(R.id.beneficiary_found_text)
        currentAccountText = findViewById(R.id.current_account_text)
        transferAmountInput = findViewById(R.id.transfer_amount_input)
        validateTransferButton = findViewById(R.id.validate_transfer_button)

        setupRetrofit()

        // Rechercher le bénéficiaire
        searchBeneficiaryButton.setOnClickListener {
            val username = beneficiaryUsernameInput.text.toString().trim()
            if (username.length == 11) {
                searchBeneficiary(username)
            } else {
                Toast.makeText(this, "Please enter a valid 11-digit username", Toast.LENGTH_SHORT).show()
            }
        }

        // Valider le transfert
        validateTransferButton.setOnClickListener {
            val amount = transferAmountInput.text.toString().toDoubleOrNull()
            if (amount != null && foundBeneficiaryId != null) {
                // Afficher une boîte de dialogue pour demander le e_code
                showECodeDialog { eCode ->
                    // Une fois le e_code saisi, vérifiez-le et effectuez le transfert
                    verifyECodeAndTransfer(foundBeneficiaryId!!, amount, eCode)
                }
            } else {
                Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
            }
        }
    }


    // Inside your VirementActivity class
    private fun setupRetrofit() {
        // Create OkHttpClient with timeout settings
        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)    // 10 seconds for connection timeout
            .writeTimeout(10, TimeUnit.SECONDS)      // 10 seconds for write timeout
            .readTimeout(10, TimeUnit.SECONDS)       // 10 seconds for read timeout
            .build()

        // Create Retrofit instance with the OkHttpClient
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2/")            // Replace with your server's IP address
            .client(client)                          // Apply the client with timeouts
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)
        Log.d("VirementActivity", "Retrofit instance created with timeout settings")
    }


    private fun searchBeneficiary(username: String) {
        apiService.getUserByUsername(username).enqueue(object : Callback<Useer> {
            override fun onResponse(call: Call<Useer>, response: Response<Useer>) {
                if (response.isSuccessful && response.body() != null) {
                    val beneficiary = response.body()
                    foundBeneficiaryId = beneficiary?.id
                    beneficiaryFoundText.text = "Beneficiary: ${beneficiary?.nom} (${beneficiary?.username})"
                    beneficiaryFoundText.visibility = View.VISIBLE
                    currentAccountText.text = "Current Account: ${beneficiary?.accountNumber}"
                    currentAccountText.visibility = View.VISIBLE
                    transferAmountInput.visibility = View.VISIBLE
                    validateTransferButton.visibility = View.VISIBLE
                } else {
                    Toast.makeText(this@VirementActivity, "Beneficiary not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Useer>, t: Throwable) {
                Toast.makeText(this@VirementActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Fonction pour afficher une boîte de dialogue de saisie du e_code
    private fun showECodeDialog(onECodeEntered: (String) -> Unit) {
        val eCodeInput = EditText(this)
        eCodeInput.hint = "Enter e-code"
        eCodeInput.inputType = android.text.InputType.TYPE_CLASS_NUMBER

        AlertDialog.Builder(this)
            .setTitle("e-Code Required")
            .setMessage("Please enter your 4-digit e-code to confirm the transfer.")
            .setView(eCodeInput)
            .setPositiveButton("Confirm") { dialog, _ ->
                val eCode = eCodeInput.text.toString()
                if (eCode.length == 4) {
                    onECodeEntered(eCode)  // Passez le e_code à la fonction de rappel
                } else {
                    Toast.makeText(this, "Please enter a valid 4-digit e-code", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun verifyECodeAndTransfer(beneficiaryId: Int, amount: Double, eCode: String) {
        Log.d("VirementActivity", "Verifying e_code for userId: $senderId with e_code: $eCode")

        apiService.verifyECode(senderId, eCode).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    performTransfer(beneficiaryId, amount)
                } else {
                    val message = response.body()?.message ?: "Invalid e-code"
                    Toast.makeText(this@VirementActivity, message, Toast.LENGTH_SHORT).show()
                    Log.e("VirementActivity", "Invalid e_code. Server response: $message")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@VirementActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("VirementActivity", "Network error during e_code verification: ${t.message}")
            }
        })
    }


    private fun performTransfer(beneficiaryId: Int, amount: Double) {
        Log.d("VirementActivity", "Initiating transfer from $senderId to $beneficiaryId with amount: $amount")

        if (senderId == -1) {
            Toast.makeText(this, "Error: User not logged in", Toast.LENGTH_SHORT).show()
            Log.e("VirementActivity", "Sender ID is invalid (-1), cannot proceed with transfer.")
            return
        }

        // Perform the transfer through API
        apiService.performTransfer(beneficiaryId, senderId, amount).enqueue(object : Callback<TransferResponse> {
            override fun onResponse(call: Call<TransferResponse>, response: Response<TransferResponse>) {
                if (response.isSuccessful) {
                    // Call the API to update accounts
                    updateAccounts(senderId, beneficiaryId, amount)
                } else {
                    Toast.makeText(this@VirementActivity, "Transfer failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<TransferResponse>, t: Throwable) {
                Toast.makeText(this@VirementActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Function to update account balances after transfer
    private fun updateAccounts(senderId: Int, beneficiaryId: Int, amount: Double) {
        apiService.updateAccounts(senderId, beneficiaryId, amount).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(this@VirementActivity, "Accounts updated successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@VirementActivity, "Account update failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@VirementActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
