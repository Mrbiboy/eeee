package com.example.e_banking

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class FinancialReportsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_financial_reports)

        // Activer le bouton "Retour" dans l'ActionBar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    // Gérer l'événement de clic sur le bouton "Retour"
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed() // Revenir à l'activité précédente
        return true
    }
}