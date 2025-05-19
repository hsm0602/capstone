package com.example.myfirstkotlinapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val label: String, val icon: ImageVector) {
    object Workout : BottomNavItem("운동", Icons.Filled.FitnessCenter)
    object Feedback : BottomNavItem("피드백", Icons.Filled.BarChart)
    object Profile : BottomNavItem("프로필", Icons.Filled.Person)
}