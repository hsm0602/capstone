package com.example.myfirstkotlinapp.network

import com.example.myfirstkotlinapp.ui.model.ExerciseDto
import com.example.myfirstkotlinapp.ui.model.ExerciseRecordCreateDto
import com.example.myfirstkotlinapp.ui.model.ExerciseRecordUpdateDto
import com.example.myfirstkotlinapp.ui.model.PostRecordResponse
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ExerciseApi {
    // 운동 목록 get
    @GET("exercises")
    suspend fun getExercises(
        @Query("muscle_group") muscleGroups: List<String>
    ): List<ExerciseDto>

    // 최초 운동 플랜 post
    @POST("exercise_records/{user_id}/{exercise_id}")
    suspend fun postExerciseRecord(
        @Path("user_id") userId: Int,
        @Path("exercise_id") exerciseId: Int,
        @Body record: ExerciseRecordCreateDto
    ): PostRecordResponse

    @PATCH("exercise_records/{record_id}")
    suspend fun patchExerciseRecord(
        @Path("record_id") recordId: Int,
        @Body data: ExerciseRecordUpdateDto
    )
}
