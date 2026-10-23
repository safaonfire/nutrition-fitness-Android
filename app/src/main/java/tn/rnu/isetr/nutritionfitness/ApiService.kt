package tn.rnu.isetr.nutritionfitness

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

data class RegisterRequest(
    val nom: String,
    val prenom: String,
    val email: String,
    val numTel: String,
    val motDePasse: String,
    val age: Int,
    val sexe: String,
    val objectif: String,
    val poids: Double,
    val taille: Double
)

data class LoginRequest(val email: String, val motDePasse: String)
data class ForgotPasswordRequest(val email: String)
data class ResetPasswordRequest(val token: String, val nouveauMotDePasse: String)
data class ProfileRequest(
    val nom: String,
    val prenom: String,
    val age: Int,
    val poids: Double,
    val taille: Double,
    val sexe: String,
    val objectif: String
)

interface ApiService {
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<JsonObject>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<JsonObject>

    @POST("api/auth/logout")
    suspend fun logout(): Response<JsonObject>

    @GET("api/clients/profil")
    suspend fun getProfile(): Response<JsonObject>

    @PUT("api/clients/profil")
    suspend fun updateProfile(@Body request: ProfileRequest): Response<JsonObject>

    @POST("api/auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<JsonObject>

    @POST("api/auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<JsonObject>
}
