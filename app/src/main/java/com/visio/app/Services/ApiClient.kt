package com.visio.app.Services

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiClient {
    private lateinit var apiService: ApiServices
    val BASE_URL="https://tap4trip.com/visio_app/public/api/"
    fun getApiService(): ApiServices {

        if (!::apiService.isInitialized) {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            apiService = retrofit.create(ApiServices::class.java)
        }

        return apiService
    }
}