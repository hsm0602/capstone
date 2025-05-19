package com.example.myfirstkotlinapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.myfirstkotlinapp.ui.navigation.BottomNavItem

@Composable
fun MainScreen() {
    var selectedTab by remember { mutableStateOf<BottomNavItem>(BottomNavItem.Workout) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                listOf(
                    BottomNavItem.Workout,
                    BottomNavItem.Feedback,
                    BottomNavItem.Profile
                ).forEach { item ->
                    NavigationBarItem(
                        selected = selectedTab == item,
                        onClick = { selectedTab = item },
                        icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (selectedTab) {
                is BottomNavItem.Workout -> WorkoutScreen()
                is BottomNavItem.Feedback -> FeedbackScreen()
                is BottomNavItem.Profile -> ProfileScreen()
            }
        }
    }
}