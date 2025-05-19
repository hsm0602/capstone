package com.example.myfirstkotlinapp.ui.model

import com.google.gson.annotations.SerializedName

data class ExerciseDto(
    val id: Int,
    val name: String,
    @SerializedName("muscle_group") val muscleGroup: String,
    val description: String? // nullable
)
