package com.example.myfirstkotlinapp.ui

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.myfirstkotlinapp.exercise.ExerciseLogic
import com.example.myfirstkotlinapp.pose.PoseLandmarkerHelper
import com.example.myfirstkotlinapp.session.WorkoutSessionManager
import com.google.mediapipe.framework.image.BitmapImageBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import android.speech.tts.TextToSpeech
import java.util.Locale

@Composable
fun PoseCameraScreen(
    sessionManager: WorkoutSessionManager,
    onSetComplete: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val currentExercise = sessionManager.currentExercise
    val currentSet = sessionManager.currentSet
    val exerciseName = currentExercise?.name ?: ""
    val targetReps = currentSet?.reps ?: 0

    // TTS 초기화
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var isTtsReady by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.let { textToSpeech ->
                    val result = textToSpeech.setLanguage(Locale.KOREAN)
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        // 한국어가 지원되지 않으면 영어로 설정
                        textToSpeech.setLanguage(Locale.ENGLISH)
                    }
                    // TTS 속도 조절 (0.5 ~ 2.0, 기본값 1.0)
                    textToSpeech.setSpeechRate(1.0f)
                    isTtsReady = true
                }
            }
        }
    }

    // TTS 정리
    DisposableEffect(Unit) {
        onDispose {
            tts?.stop()
            tts?.shutdown()
        }
    }

    // 권한 요청
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(context, "카메라 권한이 필요합니다", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    var count by remember { mutableStateOf(0) }
    var showCount by remember { mutableStateOf(false) }

    // 음성 출력 함수
    fun speakCount(currentCount: Int, target: Int) {
        if (isTtsReady && tts != null) {
            val message = when {
                currentCount < target -> "$currentCount"
                currentCount >= target -> "목표 달성! $currentCount 개 완료"
                else -> "$currentCount"
            }
            tts?.speak(message, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    val poseHelper = remember {
        PoseLandmarkerHelper(context) { result ->
            val counted = when (exerciseName) {
                "스쿼트" -> ExerciseLogic.countSquat(result)
                "푸쉬업" -> ExerciseLogic.countPushup(result)
                "풀업" -> ExerciseLogic.countPullup(result)
                "숄더 프레스" -> ExerciseLogic.countShoulderPress(result)
                "레그 레이즈" -> ExerciseLogic.countLegRaise(result)
                else -> false
            }

            if (counted) {
                count += 1
                showCount = true

                speakCount(count, targetReps)

                CoroutineScope(Dispatchers.Main).launch {
                    delay(500)
                    showCount = false
                }

                if (count >= targetReps) {
                    onSetComplete()
                    ExerciseLogic.resetCount()
                }
            }
        }
    }

    val previewView = remember { PreviewView(context) }
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    DisposableEffect(Unit) {
        val cameraProvider = cameraProviderFuture.get()

        onDispose {
            cameraProvider.unbindAll()
            poseHelper.close()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())

        if (showCount) {
            Text(
                text = "$count",
                style = MaterialTheme.typography.displayLarge,
                color = Color.Yellow,
                modifier = Modifier
                    .align(Alignment.Center)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(16.dp)
            )
        }

        LaunchedEffect(Unit) {
            val cameraProvider = ProcessCameraProvider.getInstance(context).get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val analyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
                        val bitmap = imageProxyToBitmap(imageProxy)
                        if (bitmap != null) {
                            val mpImage = BitmapImageBuilder(bitmap).build()
                            poseHelper.detectAsync(mpImage, System.currentTimeMillis() * 1000)
                        }
                        imageProxy.close()
                    }
                }

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_FRONT_CAMERA,
                preview,
                analyzer
            )
        }
    }
}