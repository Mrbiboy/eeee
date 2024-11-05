package com.example.e_banking

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginBtn: Button

    private val CHANNEL_ID = "login_notification_channel"
    private val NOTIFICATION_ID = 101

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (!isGranted) {
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        usernameInput = findViewById(R.id.username_input)
        passwordInput = findViewById(R.id.password_input)
        loginBtn = findViewById(R.id.login_btn)

        Log.d("MainActivity", "onCreate: Activity started")

        createNotificationChannel()

        requestNotificationPermission()

        loginBtn.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            Log.d("MainActivity", "Login button clicked - username: $username")

            if (username.isNotEmpty() && password.isNotEmpty()) {
                Log.d("MainActivity", "Fields are not empty, proceeding to authenticate user")
                authenticateUser(username, password)
            } else {
                Log.e("MainActivity", "Empty fields - username: $username, password: $password")
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Login Notifications"
            val descriptionText = "Channel for login success notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

            Log.d("MainActivity", "Notification channel created")
        }
    }

    private fun sendNotification(message: String) {
        Log.d("MainActivity", "Sending notification with message: $message")
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Login Successful")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)

            with(NotificationManagerCompat.from(this)) {
                notify(NOTIFICATION_ID, builder.build())
            }
        } else {
            Log.e("MainActivity", "Notification permission not granted")
            Toast.makeText(this, "Notification permission not granted", Toast.LENGTH_SHORT).show()
        }
    }

    private fun authenticateUser(username: String, password: String) {
        Log.d("MainActivity", "authenticateUser called for username: $username")

        val apiService = RetrofitClientInstance.getRetrofitInstance().create(ApiService::class.java)
        val call = apiService.loginUser(username, password)

        Log.d("MainActivity", "API call to loginUser initiated")

        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()

                    Log.d("MainActivity", "Raw server response: ${response.body().toString()}")

                    if (loginResponse?.success == true) {
                        val isAdmin = loginResponse.isAdmin
                        val userId = loginResponse.userId // Retrieve the userId from the response

                        // Store userId in SharedPreferences
                        val sharedPref = getSharedPreferences("e_banking_prefs", Context.MODE_PRIVATE)
                        with(sharedPref.edit()) {
                            putInt("userId", userId)
                            apply()
                        }

                        val message = if (isAdmin) {
                            "Welcome, Admin ${loginResponse.username}!"
                        } else {
                            "Welcome, Mr./Mrs. ${loginResponse.username}!"
                        }

                        Toast.makeText(this@MainActivity, "Login successful", Toast.LENGTH_SHORT).show()
                        sendNotification(message)

                        // Navigate to the appropriate activity
                        val intent = if (isAdmin) {
                            Intent(this@MainActivity, AdminActivity::class.java)
                        } else {
                            Intent(this@MainActivity, UserActivity::class.java).apply {
                                putExtra("userId", userId) // Pass userId to UserActivity
                            }
                        }
                        startActivity(intent)

                        // Close MainActivity to avoid it staying in the background
                        finish()
                    } else {
                        Log.e("MainActivity", "Login failed: ${loginResponse?.message}")
                        Toast.makeText(this@MainActivity, loginResponse?.message ?: "Login failed", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("MainActivity", "Server response is not valid: ${response.errorBody()?.string()}")
                    Toast.makeText(this@MainActivity, "Server error or invalid response", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("MainActivity", "Network error during login: ${t.message}")
                Toast.makeText(this@MainActivity, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    Log.d("MainActivity", "Notification permission already granted")
                }
                else -> {
                    Log.d("MainActivity", "Requesting notification permission")
                    requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }
}
