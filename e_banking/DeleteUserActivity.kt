package com.example.e_banking

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DeleteUserActivity : AppCompatActivity() {

    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_list_item_with_delete)

        // Initialize fields
        val usernameInput: TextView = findViewById(R.id.user_name)
        val deleteUserButton: Button = findViewById(R.id.delete_button)

        // Configure Retrofit with logging
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val httpClient = OkHttpClient.Builder().addInterceptor(logging).build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2/")  // Replace with your server's IP
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()

        apiService = retrofit.create(ApiService::class.java)

        // Delete a user when the button is clicked
        deleteUserButton.setOnClickListener {
            val username = usernameInput.text.toString().trim()

            if (username.isNotEmpty()) {
                // Call deleteUser with the username
                deleteUser(username)
            } else {
                Toast.makeText(this, "Please enter a username", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteUser(username: String) {
        apiService.deleteUser(username).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@DeleteUserActivity, "User deleted successfully", Toast.LENGTH_SHORT).show()
                    finish()  // Go back to the previous activity after deleting the user
                } else {
                    Toast.makeText(this@DeleteUserActivity, "Error deleting user", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@DeleteUserActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
