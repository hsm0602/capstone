package com.example.myfirstkotlinapp.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myfirstkotlinapp.ui.model.ExercisePlan
import com.example.myfirstkotlinapp.ui.model.ExerciseSet
import com.example.myfirstkotlinapp.ui.model.ExerciseDto
import com.example.myfirstkotlinapp.network.RetrofitClient
import com.example.myfirstkotlinapp.ui.model.ExerciseRecordCreateDto
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun RoutineEditScreen(
    selectedBodyParts: List<String>,
    onStartWorkout: (List<ExercisePlan>, List<Int>) -> Unit
) {
    var exercises by remember { mutableStateOf(listOf<ExercisePlan>()) }
    var showDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var availableExercises by remember { mutableStateOf<List<ExerciseDto>>(emptyList()) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                coroutineScope.launch {
                    try {
                        val response = RetrofitClient.exerciseApi.getExercises(selectedBodyParts)
                        availableExercises = response
                        showDialog = true
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }) {
                Text("+")
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("운동 계획을 설정하세요", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(exercises) { exercise ->
                    ExercisePlanItem(
                        plan = exercise,
                        onUpdate = { updated ->
                            exercises = exercises.map {
                                if (it.id == updated.id) updated else it
                            }
                        }
                    )
                }
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val createdRecordIds = mutableListOf<Int>()

                    coroutineScope.launch {
                        try {
                            exercises.forEach { plan ->
                                val matched = availableExercises.find { it.name == plan.name }
                                val exerciseId = matched?.id
                                val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                                if (exerciseId != null) {
                                    plan.sets.forEachIndexed { index, set ->
                                        val record = ExerciseRecordCreateDto(
                                            date = currentDate,
                                            sets = index + 1,  // 1번 세트, 2번 세트...
                                            reps = set.reps,
                                            weight = set.weight.toFloat()
                                        )

                                        val response = RetrofitClient.exerciseApi.postExerciseRecord(
                                            userId = 1,  // 실제 로그인 유저 ID로 대체
                                            exerciseId = exerciseId,
                                            record = record
                                        )
                                        createdRecordIds.add(response.recordId)
                                    }
                                }
                            }

                            onStartWorkout(exercises, createdRecordIds)

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            ) {
                Text("운동 시작")
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("운동을 선택하세요") },
            text = {
                if (availableExercises.isNotEmpty()) {
                    Column {
                        availableExercises.forEach { exercise ->
                            Text(
                                text = "${exercise.name} (${exercise.muscleGroup})",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        exercises = exercises + ExercisePlan(name = exercise.name)
                                        showDialog = false
                                    }
                                    .padding(12.dp)
                            )
                        }
                    }
                } else {
                    Text("선택된 부위에 해당하는 운동이 없습니다.")
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("닫기")
                }
            }
        )
    }
}


@Composable
fun ExercisePlanItem(plan: ExercisePlan, onUpdate: (ExercisePlan) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color(0xFFF0F0F0))
            .padding(12.dp)
    ) {
        Text(plan.name, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(8.dp))
        plan.sets.forEachIndexed { i, set ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("세트 ${i + 1}: ", modifier = Modifier.width(60.dp))

                OutlinedTextField(
                    value = set.weight.toString(),
                    onValueChange = {
                        val newWeight = it.toIntOrNull() ?: set.weight
                        onUpdate(plan.copy(sets = plan.sets.toMutableList().apply {
                            set(i, set.copy(weight = newWeight))
                        }))
                    },
                    label = { Text("무게(kg)") },
                    modifier = Modifier.width(120.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                OutlinedTextField(
                    value = set.reps.toString(),
                    onValueChange = {
                        val newReps = it.toIntOrNull() ?: set.reps
                        onUpdate(plan.copy(sets = plan.sets.toMutableList().apply {
                            set(i, set.copy(reps = newReps))
                        }))
                    },
                    label = { Text("횟수") },
                    modifier = Modifier.width(100.dp)
                )
            }
            Spacer(Modifier.height(8.dp))
        }

        Button(onClick = {
            onUpdate(plan.copy(sets = plan.sets + ExerciseSet()))
        }) {
            Text("+ 세트 추가")
        }
    }
}