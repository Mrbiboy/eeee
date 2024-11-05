package com.example.e_banking

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserModificationActivity : AppCompatActivity() {

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private var userList: ArrayList<User> = arrayListOf()
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Use the activity layout for user modification
        setContentView(R.layout.activity_user_modification)

        // Initialize views and setup RecyclerView
        userRecyclerView = findViewById(R.id.user_list_view)
        userRecyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize API Service (assuming you have Retrofit setup in ApiService)
        apiService = RetrofitClientInstance.getRetrofitInstance().create(ApiService::class.java)

        // Fetch users from the API
        fetchUsers()
    }

    private fun fetchUsers() {
        // API call to fetch user list
        val call = apiService.getUsers()  // Assuming getUsers() fetches all users

        call.enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) {
                    val users = response.body()
                    if (users != null && users.isNotEmpty()) {
                        // Populate the user list
                        userList.clear()
                        userList.addAll(users)

                        // Update RecyclerView with the user data
                        userAdapter = UserAdapter(this@UserModificationActivity, userList, R.layout.user_list_item_with_modification)
                        userRecyclerView.adapter = userAdapter
                    } else {
                        Toast.makeText(this@UserModificationActivity, "No users found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("API", "Failed to fetch users: ${response.message()}")
                    Toast.makeText(this@UserModificationActivity, "Failed to fetch users", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                Log.e("API", "Network Error: ${t.message}")
                Toast.makeText(this@UserModificationActivity, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
