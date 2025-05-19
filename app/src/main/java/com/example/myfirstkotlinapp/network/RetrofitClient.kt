package com.example.myfirstkotlinapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    val exerciseApi: ExerciseApi by lazy {
        Retrofit.Builder()
            .baseUrl("http://129.154.56.222:8000/")  // 슬래시 반드시 포함
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ExerciseApi::class.java)
    }
}
