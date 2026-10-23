package tn.rnu.isetr.nutritionfitness

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var passwordInput: EditText
    private lateinit var confirmInput: EditText
    private lateinit var status: TextView
    private var token: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        passwordInput = findViewById(R.id.newPasswordInput)
        confirmInput  = findViewById(R.id.confirmPasswordInput)
        status        = findViewById(R.id.statusText)

        // Read token from deep link: nutritionapp://reset-password?token=...
        token = intent?.data?.getQueryParameter("token") ?: ""

        if (token.isBlank()) {
            status.text = "Lien invalide. Redemandez un e-mail de réinitialisation."
            findViewById<Button>(R.id.resetButton).isEnabled = false
        }

        findViewById<Button>(R.id.resetButton).setOnClickListener {
            val password = passwordInput.value()
            val confirm  = confirmInput.value()

            when {
                password.isBlank() -> {
                    status.text = "Veuillez saisir un nouveau mot de passe."
                }
                password != confirm -> {
                    status.text = "Les mots de passe ne correspondent pas."
                }
                password.length < 8 || password.length > 12 -> {
                    status.text = "Le mot de passe doit contenir entre 8 et 12 caractères."
                }
                !password.any { it.isUpperCase() } -> {
                    status.text = "Le mot de passe doit contenir au moins une majuscule."
                }
                !password.any { it.isLowerCase() } -> {
                    status.text = "Le mot de passe doit contenir au moins une minuscule."
                }
                !password.any { it.isDigit() } -> {
                    status.text = "Le mot de passe doit contenir au moins un chiffre."
                }
                !password.any { !it.isLetterOrDigit() } -> {
                    status.text = "Le mot de passe doit contenir au moins un symbole."
                }
                else -> submitReset(password)
            }
        }

        findViewById<Button>(R.id.backButton).setOnClickListener { finish() }
    }

    private fun submitReset(newPassword: String) {
        status.text = "Réinitialisation en cours…"
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.resetPassword(
                    ResetPasswordRequest(token, newPassword)
                )
                if (response.isSuccessful) {
                    status.text = "Mot de passe réinitialisé avec succès !"
                    // Return to login after a short moment
                    startActivity(
                        Intent(this@ResetPasswordActivity, MainActivity::class.java)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                    finish()
                } else {
                    val msg = errorMessage(response.code(), response.errorBody()?.string())
                    status.text = when {
                        response.code() == 404 || msg.contains("invalide", ignoreCase = true) ->
                            "Ce lien est invalide ou a déjà été utilisé."
                        response.code() == 410 || msg.contains("expiré", ignoreCase = true) ->
                            "Ce lien a expiré (15 min). Redemandez un e-mail."
                        else -> "Échec : $msg"
                    }
                }
            } catch (e: Exception) {
                status.text = "Erreur de connexion : ${e.message ?: "serveur injoignable"}."
            }
        }
    }
}
