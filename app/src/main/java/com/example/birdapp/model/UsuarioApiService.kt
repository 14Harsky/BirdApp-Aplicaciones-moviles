package com.example.birdapp.model

import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

// Modelo para Register
data class Usuario(
    val id: Int? = null,
    val nombre: String,
    val correo: String,
    @SerializedName("password")
    val clave: String? = null
)

// Modelo para Login (Request)
data class LoginRequest(
    val correo: String,
    @SerializedName("password")
    val clave: String
)

// Modelo para Login (Response)
data class AuthResponse(
    val token: String,
    val id: Int,
    val nombre: String,
    val correo: String
)

// ¡NUEVO! Modelo para Update Profile (Request)
data class UpdateProfileRequest(
    val nombre: String,
    val correo: String
)

// Modelo para ChangePassword (Request)
data class ChangePasswordRequest(
    val claveActual: String,
    val claveNueva: String
)

interface UsuarioApiService {

    @POST("Api/usuario/register")
    suspend fun register(@Body usuario: Usuario): Usuario

    @POST("Api/usuario/login")
    suspend fun login(@Body loginRequest: LoginRequest): AuthResponse

    @PUT("Api/usuario/editar/{id}")
    suspend fun updateUser(
        @Header("Authorization") token: String,
        @Path("id") userId: Int,
        @Body updateRequest: UpdateProfileRequest // <-- Usamos el nuevo modelo
    ): Usuario // La respuesta del backend sí es un objeto Usuario completo

    @DELETE("Api/usuario/eliminar/{id}")
    suspend fun deleteUser(
        @Header("Authorization") token: String,
        @Path("id") userId: Int
    )

    @PUT("Api/usuario/editar/{id}/password")
    suspend fun changePassword(
        @Header("Authorization") token: String,
        @Path("id") userId: Int,
        @Body changePasswordRequest: ChangePasswordRequest
    )

    companion object {
        private const val BASE_URL = "http://10.0.2.2:8080/"

        fun create(): UsuarioApiService {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(UsuarioApiService::class.java)
        }
    }
}