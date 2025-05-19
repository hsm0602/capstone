package com.example.myfirstkotlinapp.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BodyPartSelectionScreen(
    onStartRoutine: (List<String>) -> Unit
) {
    val allParts = listOf("가슴", "등", "하체", "어깨", "팔", "복부")
    val selectedParts = remember { mutableStateListOf<String>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("오늘은 어떤 운동을 할까요?", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(allParts) { part ->
                val isSelected = part in selectedParts
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable {
                            if (isSelected) selectedParts.remove(part)
                            else selectedParts.add(part)
                        }
                        .background(if (isSelected) Color(0xFF3F51B5) else Color(0xFFF0F0F0)) // 파란색 (선택된 상태)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        part,
                        fontSize = 16.sp,
                        color = if (isSelected) Color.White else Color.Black // 파란색일 때는 글자색을 하얀색으로 변경
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { onStartRoutine(selectedParts.toList()) },
            enabled = selectedParts.isNotEmpty(),
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5)) // 파란색 버튼
        ) {
            Text("운동하기", color = Color.White)
        }
    }
}