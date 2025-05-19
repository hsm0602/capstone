package com.example.myfirstkotlinapp.exercise

import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult

object ExerciseLogic {
    private var isDown = false
    private var lowFrameCount = 0
    private var highFrameCount = 0
    private var lastCountTime = 0L
    private const val frameThreshold = 3
    private const val squatCooldown = 500L // ms
    private var squatCount = 0


    fun countSquat(result: PoseLandmarkerResult): Boolean {
        val landmarks = result.landmarks().firstOrNull() ?: return false

        val leftHipY = landmarks[23].y()
        val rightHipY = landmarks[24].y()
        val hipY = (leftHipY + rightHipY) / 2

        val leftKneeY = landmarks[25].y()
        val rightKneeY = landmarks[26].y()
        val kneeY = (leftKneeY + rightKneeY) / 2

        val currentTime = System.currentTimeMillis()

        val hipBelowKnee = hipY > kneeY + 0.02f
        val hipAboveKnee = hipY < kneeY - 0.02f

        if (hipBelowKnee) {
            lowFrameCount++
            highFrameCount = 0
            if (lowFrameCount >= 3) {
                isDown = true
            }
        } else if (hipAboveKnee && isDown) {
            highFrameCount++
            if (highFrameCount >= 3 && currentTime - lastCountTime > squatCooldown) {
                lastCountTime = currentTime
                lowFrameCount = 0
                highFrameCount = 0
                isDown = false
                squatCount++
                return true
            }
        } else {
            lowFrameCount = 0
            highFrameCount = 0
        }

        return false
    }


    fun countPushup(result: PoseLandmarkerResult): Boolean {
        val landmarks = result.landmarks().firstOrNull() ?: return false
        // TODO: 푸시업 기준 위치 분석
        return false
    }
}