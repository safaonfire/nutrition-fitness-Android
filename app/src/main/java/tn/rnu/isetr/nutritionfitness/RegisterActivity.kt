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

class RegisterActivity : AppCompatActivity() {
    private lateinit var status: TextView
    private lateinit var nom: EditText
    private lateinit var prenom: EditText
    private lateinit var email: EditText
    private lateinit var phone: EditText
    private lateinit var password: EditText
    private lateinit var age: EditText
    private lateinit var weight: EditText
    private lateinit var height: EditText
    private lateinit var sex: Spinner
    private lateinit var goal: Spinner
    // Only these three objective values exist in the supplied backend enum.
    private val goals = listOf("perdre_poids", "gagne_poids", "maintenir_masse")
    private val labels = listOf("Lose weight", "Gain mass", "Maintain weight")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        status = findViewById(R.id.statusText)
        nom = findViewById(R.id.nomInput); prenom = findViewById(R.id.prenomInput)
        email = findViewById(R.id.emailInput); phone = findViewById(R.id.phoneInput)
        password = findViewById(R.id.passwordInput); age = findViewById(R.id.ageInput)
        weight = findViewById(R.id.weightInput); height = findViewById(R.id.heightInput)
        sex = findViewById(R.id.sexSpinner); goal = findViewById(R.id.goalSpinner)
        sex.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listOf("HOMME", "FEMME"))
        goal.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, labels)
        findViewById<Button>(R.id.registerButton).setOnClickListener { register() }
        findViewById<Button>(R.id.backButton).setOnClickListener { finish() }
    }

    private fun register() {
        val ageValue = age.value().toIntOrNull()
        val weightValue = weight.value().toDoubleOrNull()
        val heightValue = height.value().toDoubleOrNull()
        if (listOf(nom.value(), prenom.value(), email.value(), phone.value(), password.text.toString()).any { it.isBlank() } || ageValue == null || weightValue == null || heightValue == null) {
            status.text = "Complete all fields with valid numbers."; return
        }
        status.text = "Creating account…"
        lifecycleScope.launch {
            try {
                val request = RegisterRequest(nom.value(), prenom.value(), email.value(), phone.value(), password.text.toString(), ageValue, sex.selectedItem.toString(), goals[goal.selectedItemPosition], weightValue, heightValue)
                val response = RetrofitClient.api.register(request)
                if (response.isSuccessful) {
                    status.text = "Registration successful. Please sign in."
                    startActivity(Intent(this@RegisterActivity, MainActivity::class.java)); finish()
                } else status.text = errorMessage(response.code(), response.errorBody()?.string())
            } catch (e: Exception) { status.text = "Connection error: ${e.message ?: "Could not reach server"}." }
        }
    }
}
