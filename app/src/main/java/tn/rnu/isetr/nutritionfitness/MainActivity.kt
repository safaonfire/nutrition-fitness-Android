package tn.rnu.isetr.nutritionfitness

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        email = findViewById(R.id.emailInput)
        password = findViewById(R.id.passwordInput)
        status = findViewById(R.id.statusText)

        findViewById<Button>(R.id.loginButton).setOnClickListener {
            if (email.value().isBlank() || password.text.isNullOrBlank()) {
                status.text = "Enter your email and password."
                return@setOnClickListener
            }
            status.text = ""
            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.api.login(LoginRequest(email.value(), password.text.toString()))
                    if (response.isSuccessful) {
                        // Supplied backend has no GET /api/clients/profil endpoint.
                        // Use the subset returned by login; physical fields can be entered in the profile.
                        val body = response.body()
                        val intent = Intent(this@MainActivity, ProfileActivity::class.java)
                        intent.putExtra("email", body?.stringValue("email")?.ifBlank { email.value() } ?: email.value())
                        intent.putExtra("nom", body?.stringValue("nom").orEmpty())
                        intent.putExtra("prenom", body?.stringValue("prenom").orEmpty())
                        intent.putExtra("objectif", body?.stringValue("objectif").orEmpty())
                        startActivity(intent)
                        finish()
                    } else status.text = errorMessage(response.code(), response.errorBody()?.string())
                } catch (e: Exception) {
                    status.text = "Connection error: ${e.message ?: "Could not reach server"}. Check Spring Boot on port 8081."
                }
            }
        }
        findViewById<Button>(R.id.registerLink).setOnClickListener { startActivity(Intent(this, RegisterActivity::class.java)) }
        findViewById<Button>(R.id.forgotLink).setOnClickListener { startActivity(Intent(this, ForgotPasswordActivity::class.java)) }
    }
}
