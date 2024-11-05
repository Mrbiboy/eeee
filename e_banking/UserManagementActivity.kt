package com.example.e_banking

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserManagementActivity : AppCompatActivity() {

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userListAdapter: UserAdapter
    private var userList: ArrayList<User> = arrayListOf()
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_management)

        // Enable the back button in the ActionBar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Initialize buttons and set up click listeners
        val addUserButton: Button = findViewById(R.id.add_user_btn)
        val removeUserButton: Button = findViewById(R.id.remove_user_btn)
        val updateUserButton: Button = findViewById(R.id.update_user_btn)

        addUserButton.setOnClickListener {
            val intent = Intent(this, AddUserActivity::class.java)
            startActivity(intent)
        }

        removeUserButton.setOnClickListener {
            val intent = Intent(this, DeleteUserActivity::class.java)
            startActivity(intent)
        }

        updateUserButton.setOnClickListener {
            val intent = Intent(this, UpdateUserActivity::class.java)
            startActivity(intent)
        }

        // Set up RecyclerView
        userRecyclerView = findViewById(R.id.user_list_view)
        userRecyclerView.layoutManager = LinearLayoutManager(this)

        // Configure Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2/")   // Use the address of your local server
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        // Call the method to get the users
        getUsers()
    }

    private fun getUsers() {
        apiService.getUsers().enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful && response.body() != null) {
                    val users = response.body()!!
                    userList.clear()
                    userList.addAll(users)

                    // Update the RecyclerView
                    runOnUiThread {
                        userListAdapter = UserAdapter(this@UserManagementActivity, userList, R.layout.user_item)
                        userRecyclerView.adapter = userListAdapter
                    }
                } else {
                    Log.e("UserManagement", "Error fetching users: ${response.message()}")
                    runOnUiThread {
                        Toast.makeText(this@UserManagementActivity, "Erreur lors de la récupération des utilisateurs", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                Log.e("UserManagement", "API call failed: ${t.message}")
                runOnUiThread {
                    Toast.makeText(this@UserManagementActivity, "Erreur de connexion", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    // Handle the back button click in the ActionBar
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
