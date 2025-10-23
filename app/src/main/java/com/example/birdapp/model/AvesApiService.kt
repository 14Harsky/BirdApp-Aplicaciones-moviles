package com.example.birdapp.model

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface AvesApiService {
    @GET("birds")
    suspend fun getAves(): List<Ave>

    companion object {
        private const val BASE_URL = "https://aves.ninjas.cl/api/"

        fun create(): AvesApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(AvesApiService::class.java)
        }
    }
}