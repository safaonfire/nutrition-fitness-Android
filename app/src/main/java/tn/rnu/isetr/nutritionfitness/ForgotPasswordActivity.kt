package tn.rnu.isetr.nutritionfitness

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var email: EditText
    private lateinit var status: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        email = findViewById(R.id.emailInput); status = findViewById(R.id.statusText)
        findViewById<Button>(R.id.sendButton).setOnClickListener {
            if (email.value().isBlank()) {
                status.text = "Veuillez saisir votre adresse e-mail."
                return@setOnClickListener
            }
            val address = email.value()
            status.text = "Envoi en cours…"
            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.api.forgotPassword(ForgotPasswordRequest(address))
                    if (response.isSuccessful) {
                        // US6 : envoi d'un lien de réinitialisation par le backend.
                        status.text = "Un lien de réinitialisation a été envoyé à $address."
                        email.text.clear()
                    } else {
                        // US6 : message si l'e-mail n'existe pas.
                        val msg = errorMessage(response.code(), response.errorBody()?.string())
                        status.text = if (
                            response.code() == 404 ||
                            msg.contains("n'existe pas", ignoreCase = true) ||
                            msg.contains("non existant", ignoreCase = true) ||
                            msg.contains("not found", ignoreCase = true)
                        ) {
                            "Cet e-mail n'existe pas."
                        } else {
                            "Échec de l'envoi : $msg"
                        }
                    }
                } catch (e: Exception) {
                    status.text = "Erreur de connexion : ${e.message ?: "serveur injoignable"}."
                }
            }
        }
        findViewById<Button>(R.id.backButton).setOnClickListener { finish() }
    }
}
