package com.example.e_banking

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TransactionAdapter(
    private val context: Context,
    private val transactionList: List<Transaction>
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    inner class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val transactionId: TextView = view.findViewById(R.id.transaction_id)
        val userId: TextView = view.findViewById(R.id.user_id)
        val amount: TextView = view.findViewById(R.id.transaction_amount)
        val date: TextView = view.findViewById(R.id.transaction_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.transaction_list_item, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactionList[position]
        holder.transactionId.text = "Transaction ID: ${transaction.transactionId}"
        holder.userId.text = "User ID: ${transaction.userId}"
        holder.amount.text = "Amount: ${transaction.amount}"
        holder.date.text = "Date: ${transaction.transactionDate}"
    }

    override fun getItemCount(): Int {
        return transactionList.size
    }
}