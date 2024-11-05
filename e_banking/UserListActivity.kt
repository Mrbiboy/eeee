package com.example.e_banking

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class UserListActivity : AppCompatActivity() {

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userListAdapter: UserAdapter
    private var userList: ArrayList<User> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)

        userRecyclerView = findViewById(R.id.user_list_view)
        userRecyclerView.layoutManager = LinearLayoutManager(this)

        val username = intent.getStringExtra("username") ?: ""
        Log.d("UserListActivity", "Username received: $username")

        getUserDetails(username)
    }

    private fun getUserDetails(username: String) {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("http://10.0.2.2/api/getUsers.php?username=$username")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@UserListActivity, "Erreur de connexion", Toast.LENGTH_SHORT).show()
                }
                Log.e("UserListActivity", "Network Error: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val jsonData = response.body?.string()
                if (jsonData != null) {
                    try {
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
                                accountNumber = userJson.getString("accountNumber"), // Updated to include accountNumber
                                isAdmin = if (userJson.has("isAdmin")) userJson.optInt("isAdmin", 0) else 0
                            )
                            userList.add(user)
                        }

                        runOnUiThread {
                            userListAdapter = UserAdapter(this@UserListActivity, userList, R.layout.user_item)
                            userRecyclerView.adapter = userListAdapter
                        }
                    } catch (e: Exception) {
                        runOnUiThread {
                            Toast.makeText(this@UserListActivity, "Erreur lors du traitement des données", Toast.LENGTH_SHORT).show()
                        }
                        Log.e("UserListActivity", "Error parsing JSON: ${e.message}")
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@UserListActivity, "Erreur: réponse vide", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}
