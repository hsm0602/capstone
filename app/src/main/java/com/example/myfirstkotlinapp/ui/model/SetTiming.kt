package com.example.myfirstkotlinapp.ui.model

data class SetTiming(
    val recordId: Int,
    var workoutStart: Long = 0L,
    var workoutEnd: Long = 0L,
    var restStart: Long = 0L,
    var restEnd: Long = 0L
) {
    fun workoutDuration(): Int = ((workoutEnd - workoutStart) / 1000).toInt()
    fun restDuration(): Int = ((restEnd - restStart) / 1000).toInt()
}
