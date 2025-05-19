package com.example.myfirstkotlinapp.ui.model

data class ExerciseRecordCreateDto(
    val date: String,       // ISO 날짜 형식: "2024-05-14"
    val sets: Int,
    val reps: Int,
    val weight: Float
)
