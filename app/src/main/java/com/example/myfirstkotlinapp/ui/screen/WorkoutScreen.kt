package com.example.myfirstkotlinapp.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.myfirstkotlinapp.session.WorkoutSessionManager
import com.example.myfirstkotlinapp.ui.PoseCameraScreen
import com.example.myfirstkotlinapp.ui.model.ExercisePlan

@Composable
fun WorkoutScreen() {
    var isEditingRoutine by remember { mutableStateOf(false) }  // 운동 계획을 설정할지 말지
    var isDoingWorkout by remember { mutableStateOf(false) }     // 운동을 진행할지 말지
    var isResting by remember { mutableStateOf(false) }
    var isWorkoutComplete by remember { mutableStateOf(false) }
    var selectedParts by remember { mutableStateOf<List<String>>(emptyList()) } // 선택된 부위
    var selectedPlans by remember { mutableStateOf<List<ExercisePlan>>(emptyList()) } // 운동 계획 리스트
    var recordIds by remember { mutableStateOf<List<Int>>(emptyList()) }

    val sessionManager = remember(selectedPlans, recordIds) {
        if (selectedPlans.isNotEmpty() && recordIds.isNotEmpty()) {
            WorkoutSessionManager(selectedPlans, recordIds)
        } else null
    }

    // 첫 운동 시작 시 startWorkout 호출 보장
    LaunchedEffect(isDoingWorkout, sessionManager) {
        if (isDoingWorkout && sessionManager != null && !isResting && !isWorkoutComplete) {
            sessionManager.startWorkout()
        }
    }

    when {
        isWorkoutComplete && sessionManager != null -> {
            WorkoutCompleteScreen(
                sessionManager = sessionManager,
                onFinish = {
                    isEditingRoutine = false
                    isDoingWorkout = false
                    isResting = false
                    isWorkoutComplete = false
                    selectedPlans = emptyList()
                    selectedParts = emptyList()
                    recordIds = emptyList()
                }
            )
        }

        isResting && sessionManager != null -> {
            IntermediateScreen(
                sessionManager = sessionManager,
                restDurationSec = 30,
                onRestComplete = {
                    sessionManager.endRest()
                    sessionManager.advanceToNextSet()

                    // 다음 세트가 있는지 확인하고 운동 시작
                    if (!sessionManager.isFinished) {
                        isResting = false
                        isDoingWorkout = true  // 다음 세트 수행
                        // 이미 markSetAsCompleted에서 다음 세트 타이밍이 초기화되었기 때문에
                        // 여기서는 startWorkout을 호출하지 않음
                    } else {
                        // 모든 운동이 끝난 경우
                        isResting = false
                        isWorkoutComplete = true
                    }
                }
            )
        }

        isDoingWorkout && sessionManager != null -> { // 운동 중일 때
            PoseCameraScreen(
                sessionManager = sessionManager,
                onSetComplete = {
                    sessionManager.endWorkout()
                    sessionManager.markSetAsCompleted()

                    if (sessionManager.isFinished) {
                        isDoingWorkout = false
                        isWorkoutComplete = true
                    } else {
                        sessionManager.startRest()
                        isDoingWorkout = false
                        isResting = true
                        // → 휴식 화면으로 이동
                    }
                }
            )
        }
        isEditingRoutine -> { // 운동 계획 설정 중일 때
            RoutineEditScreen(
                selectedBodyParts = selectedParts,
                onStartWorkout = { plans, ids ->
                    // 운동 계획이 설정되면 운동 시작
                    selectedPlans = plans
                    recordIds = ids
                    isDoingWorkout = true
                }
            )
        }

        else -> { // 운동 부위 선택 화면
            BodyPartSelectionScreen(
                onStartRoutine = { selected ->
                    selectedParts = selected // 부위 선택 저장
                    isEditingRoutine = true   // 운동 계획 설정 화면으로 이동
                }
            )
        }
    }
}