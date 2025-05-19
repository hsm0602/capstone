package com.example.myfirstkotlinapp.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myfirstkotlinapp.session.WorkoutSessionManager
import kotlinx.coroutines.delay

@Composable
fun IntermediateScreen(
    sessionManager: WorkoutSessionManager,
    restDurationSec: Int = 30,
    onRestComplete: () -> Unit
) {
    var secondsRemaining by remember { mutableStateOf(restDurationSec) }

    LaunchedEffect(Unit) {
        while (secondsRemaining > 0) {
            delay(1000)
            secondsRemaining--
        }
        onRestComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "지금까지의 운동 진행 현황",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Progress Summary
            Column(modifier = Modifier.weight(1f)) {
                sessionManager.completedSets.groupBy { it.first }.forEach { (exerciseIndex, setList) ->
                    val exercise = sessionManager.plans.getOrNull(exerciseIndex) ?: return@forEach
                    Text(
                        text = exercise.name,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        exercise.sets.forEachIndexed { index, _ ->
                            val completed = setList.any { it.second == index }
                            Box(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .size(24.dp)
                                    .background(if (completed) Color.Gray else Color.LightGray),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "${index + 1}", fontSize = 12.sp)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Countdown Timer (작게 표시)
            Text(
                text = "휴식: $secondsRemaining 초",
                fontSize = 18.sp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 16.dp)
            )
        }
    }
}
