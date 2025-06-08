package com.example.myfirstkotlinapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myfirstkotlinapp.MainActivity
import com.example.myfirstkotlinapp.network.RetrofitClient
import com.example.myfirstkotlinapp.ui.theme.MyFirstKotlinAppTheme
import kotlinx.coroutines.launch
import android.widget.Toast
import androidx.core.content.edit


class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyFirstKotlinAppTheme {
                LoginScreen(
                    onLoginSuccess = {
                        //startActivity(Intent(this, MainActivity::class.java))
                        startActivity(Intent(this, WelcomeActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var userId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "어서와요\n같이 운동해요!",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = userId,
            onValueChange = { userId = it },
            label = { Text("ID") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("PW") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )

        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()
        Button(
            onClick = {
                coroutineScope.launch {
                    try {
                        val response = RetrofitClient.authApi.login(userId, password)
                        if (response.isSuccessful) {
                            val token = response.body()?.accessToken
                            if (token != null) {
                                // SharedPreferences에 저장
                                val sharedPref = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
                                sharedPref.edit {
                                    putString("access_token", token)
                                }
                                println("Access Token 저장 완료: $token")
                                onLoginSuccess()
                            } else {
                                Toast.makeText(context, "토큰이 비어 있습니다", Toast.LENGTH_SHORT).show()
                            }
                            onLoginSuccess()
                        } else {
                            Toast.makeText(context, "로그인 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "네트워크 오류: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5BC3EB))
        ) {
            Text("로그인", color = Color.White)
        }

        Text(
            text = "or",
            color = Color.Gray,
            modifier = Modifier.padding(top = 16.dp)
        )

        Text(
            text = "회원가입",
            color = Color(0xFF5BC3EB),
            fontSize = 14.sp,
            modifier = Modifier
                .padding(top = 8.dp)
                .clickable { /* 추후 회원가입 연결 */ }
        )
    }
}