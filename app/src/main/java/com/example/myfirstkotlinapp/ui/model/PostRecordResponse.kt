package com.example.myfirstkotlinapp.ui.model

import com.google.gson.annotations.SerializedName

data class PostRecordResponse(
    val message: String,
    @SerializedName("record_id") val recordId: Int
)
