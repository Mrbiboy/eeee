package com.example.e_banking

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TransactionUserAdapter(
    private val context: Context,
    private val userList: MutableList<User>,
    private val onUserSelected: (User) -> Unit // Lambda pour la sélection de l'utilisateur pour la transaction
) : RecyclerView.Adapter<TransactionUserAdapter.UserViewHolder>() {

    inner class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userName: TextView = view.findViewById(R.id.user_name)
        val selectButton: Button = view.findViewById(R.id.select_button) // Bouton pour sélectionner l'utilisateur
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_list_transaction, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.userName.text = "${user.nom} ${user.prenom}"

        // Gestion du bouton de sélection
        holder.selectButton.setOnClickListener {
            onUserSelected(user) // Appel de la fonction lambda pour sélectionner l'utilisateur
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }
}