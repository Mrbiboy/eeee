package com.example.e_banking

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import okhttp3.*
import java.io.IOException

class UserAdapter(
    private val context: Context,
    private val userList: MutableList<User>,
    private val layoutResource: Int  // Accept a layout resource for flexibility
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userName: TextView = view.findViewById(R.id.user_name)
        val updateButton: Button? = view.findViewById(R.id.update_button)  // Optional button for update
        val deleteButton: Button? = view.findViewById(R.id.delete_button)  // Optional button for delete
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutResource, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.userName.text = "${user.nom} ${user.prenom}"

        // Handle the update button click (if present)
        holder.updateButton?.setOnClickListener {
            val intent = Intent(context, UpdateUserActivity::class.java).apply {
                putExtra("userId", user.id)
                putExtra("userNom", user.nom)
                putExtra("userPrenom", user.prenom)
                putExtra("userEmail", user.email)
                putExtra("userAddress", user.address)
                putExtra("userCin", user.cin)
                putExtra("userDateNaissance", user.dateNaissance)
            }
            context.startActivity(intent)
        }

        // Handle the delete button click (if present)
        holder.deleteButton?.setOnClickListener {
            deleteUserFromAPI(user.id, position)
        }
    }

    override fun getItemCount(): Int = userList.size

    // Function to delete a user via the API
    private fun deleteUserFromAPI(userId: Int, position: Int) {
        val client = OkHttpClient()
        val requestBody = FormBody.Builder()
            .add("id", userId.toString())
            .build()

        val request = Request.Builder()
            .url("http://10.0.2.2/api/deleteUser.php")  // Ensure the URL is correct for your API
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                (context as? AppCompatActivity)?.runOnUiThread {
                    Toast.makeText(context, "Erreur de connexion", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    (context as? AppCompatActivity)?.runOnUiThread {
                        Toast.makeText(context, "Utilisateur supprimé", Toast.LENGTH_SHORT).show()
                        // Remove the user from the list and notify the adapter
                        userList.removeAt(position)
                        notifyItemRemoved(position)
                    }
                } else {
                    (context as? AppCompatActivity)?.runOnUiThread {
                        Toast.makeText(context, "Échec de la suppression", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}
