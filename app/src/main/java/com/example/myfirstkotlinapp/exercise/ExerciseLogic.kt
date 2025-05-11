package com.example.myfirstkotlinapp.exercise

import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult

object ExerciseLogic {
    private var isDown = false

    fun countSquat(result: PoseLandmarkerResult): Boolean {
        val landmarks = result.landmarks().firstOrNull() ?: return false
        val leftHip = landmarks[23] // 좌측 엉덩이
        val leftKnee = landmarks[25] // 좌측 무릎

        return if (!isDown && leftHip.y() > leftKnee.y() + 0.05f) {
            isDown = true
            false
        } else if (isDown && leftHip.y() < leftKnee.y()) {
            isDown = false
            true // 올라올 때 카운트
        } else {
            false
        }
    }

    fun countPushup(result: PoseLandmarkerResult): Boolean {
        val landmarks = result.landmarks().firstOrNull() ?: return false
        // TODO: 푸시업 기준 위치 분석
        return false
    }
}