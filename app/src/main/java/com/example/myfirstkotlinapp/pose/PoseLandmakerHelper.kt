package com.example.myfirstkotlinapp.pose


import android.content.Context
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.framework.image.MPImage

class PoseLandmarkerHelper(
    context: Context,
    modelName: String = "pose_landmarker_lite.task",
    val onResults: (PoseLandmarkerResult) -> Unit
) {
    private var poseLandmarker: PoseLandmarker

    init {
        val baseOptions = BaseOptions.builder()
            .setModelAssetPath(modelName)
            .build()

        val options = PoseLandmarker.PoseLandmarkerOptions.builder()
            .setBaseOptions(baseOptions)
            .setRunningMode(RunningMode.LIVE_STREAM)
            .setResultListener { result, _ ->
                onResults(result)
            }
            .build()

        poseLandmarker = PoseLandmarker.createFromOptions(context, options) // PoseLandmaker 객체 생성.
    }

    fun detectAsync(mpImage: MPImage, timestamp: Long) {
        poseLandmarker.detectAsync(mpImage, timestamp)
    }

    fun close() {
        poseLandmarker.close()
    }
}