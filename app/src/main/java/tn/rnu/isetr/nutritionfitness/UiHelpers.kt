package tn.rnu.isetr.nutritionfitness

import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import org.json.JSONObject
import com.google.gson.JsonObject

fun JsonObject.stringValue(key: String): String = try {
    get(key)?.takeUnless { it.isJsonNull }?.asString ?: ""
} catch (_: Exception) { "" }

fun errorMessage(code: Int, raw: String?): String {
    val message = try { JSONObject(raw ?: "{}").optString("message", "") } catch (_: Exception) { "" }
    return if (message.isNotBlank()) "HTTP $code: $message" else "Request failed (HTTP $code). Check the server and entered data."
}

fun EditText.value(): String = text.toString().trim()
