package com.example.myfirstkotlinapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.myfirstkotlinapp.ui.theme.MyFirstKotlinAppTheme
import androidx.navigation.compose.rememberNavController
import com.example.myfirstkotlinapp.ui.PoseCameraScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyFirstKotlinAppTheme {
                val navController = rememberNavController()
                NavHost(navController, startDestination = "pose_camera") {
                    composable("pose_camera") { PoseCameraScreen() }
                    // 이후 추가 예정: composable("result") { ExerciseResultScreen(...) }
                }
            }
        }
    }
}
