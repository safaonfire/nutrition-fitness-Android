package tn.rnu.isetr.nutritionfitness

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {
    private lateinit var nom: EditText; private lateinit var prenom: EditText
    private lateinit var age: EditText; private lateinit var weight: EditText; private lateinit var height: EditText
    private lateinit var sex: Spinner; private lateinit var goal: Spinner; private lateinit var status: TextView
    private lateinit var emailInfo: TextView
    // Only these three objective values exist in the supplied backend enum.
    private val goals = listOf("perdre_poids", "gagne_poids", "maintenir_masse", "ameliorer_condition")
    private val labels = listOf("Perdre du poids", "Prendre de la masse", "Maintenir le poids", "Améliorer ma condition")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); setContentView(R.layout.activity_profile)
        nom = findViewById(R.id.nomInput); prenom = findViewById(R.id.prenomInput)
        age = findViewById(R.id.ageInput); weight = findViewById(R.id.weightInput); height = findViewById(R.id.heightInput)
        sex = findViewById(R.id.sexSpinner); goal = findViewById(R.id.goalSpinner)
        status = findViewById(R.id.statusText); emailInfo = findViewById(R.id.emailInfo)
        sex.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listOf("HOMME", "FEMME"))
        goal.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, labels)
        nom.setText(intent.getStringExtra("nom")); prenom.setText(intent.getStringExtra("prenom"))
        age.setText(intent.getStringExtra("age")); weight.setText(intent.getStringExtra("poids")); height.setText(intent.getStringExtra("taille"))
        emailInfo.text = "Email: ${intent.getStringExtra("email").orEmpty()}"
        sex.setSelection(if (intent.getStringExtra("sexe") == "FEMME") 1 else 0)
        goal.setSelection(goals.indexOf(intent.getStringExtra("objectif")).coerceAtLeast(0))
        loadProfile()
        findViewById<Button>(R.id.saveButton).setOnClickListener { saveProfile() }
        findViewById<Button>(R.id.resetPasswordButton).setOnClickListener { startActivity(Intent(this, ForgotPasswordActivity::class.java)) }
        findViewById<Button>(R.id.logoutButton).setOnClickListener { logout() }
    }

    private fun loadProfile() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getProfile()
                if (response.isSuccessful) {
                    val data = response.body() ?: return@launch
                    nom.setText(data.stringValue("nom"))
                    prenom.setText(data.stringValue("prenom"))
                    age.setText(data.stringValue("age"))
                    weight.setText(data.stringValue("poids"))
                    height.setText(data.stringValue("taille"))
                    emailInfo.text = "Email: ${data.stringValue("email")}" 
                    sex.setSelection(if (data.stringValue("sexe") == "FEMME") 1 else 0)
                    goal.setSelection(goals.indexOf(data.stringValue("objectif")).coerceAtLeast(0))
                } else status.text = errorMessage(response.code(), response.errorBody()?.string())
            } catch (e: Exception) {
                status.text = "Impossible de charger le profil : ${e.message ?: "connexion indisponible"}"
            }
        }
    }

    private fun saveProfile() {
        val a = age.value().toIntOrNull(); val w = weight.value().toDoubleOrNull(); val h = height.value().toDoubleOrNull()
        if (nom.value().isBlank() || prenom.value().isBlank() || a == null || w == null || h == null) {
            status.text = "Enter your first name, last name, age, weight and height."
            return
        }
        if (a !in 18..100 || w !in 20.0..350.0 || h !in 0.80..2.50) {
            status.text = "Check the values: age 18–100, weight 20–350 kg, height 0.80–2.50 m."
            return
        }
        lifecycleScope.launch {
            try {
                val request = ProfileRequest(nom.value(), prenom.value(), a, w, h, sex.selectedItem.toString(), goals[goal.selectedItemPosition])
                val response = RetrofitClient.api.updateProfile(request)
                if (response.isSuccessful) {
                    status.text = "Profil enregistré avec succès."
                    loadProfile()
                }
                else {
                    val message = errorMessage(response.code(), response.errorBody()?.string())
                    status.text = if (message.contains("Objectif invalide", ignoreCase = true)) {
                        "Enregistrement impossible : incompatibilité des objectifs dans le backend. Prévenez la responsable Spring Boot."
                    } else message
                }
            } catch (e: Exception) { status.text = "Connection error: ${e.message ?: "Could not reach server"}." }
        }
    }

    private fun logout() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.logout()
                if (response.isSuccessful) { startActivity(Intent(this@ProfileActivity, MainActivity::class.java)); finish() }
                else status.text = errorMessage(response.code(), response.errorBody()?.string())
            } catch (e: Exception) { status.text = "Connection error: ${e.message ?: "Could not reach server"}." }
        }
    }
}
