package com.example.myfirstkotlinapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.myfirstkotlinapp.ui.theme.MyFirstKotlinAppTheme

class SignupActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyFirstKotlinAppTheme {
                SignupScreen {
                    finish() // 회원가입 성공 후 현재 액티비티 종료 (필요시 로그인 화면으로 이동)
                }
            }
        }
    }
}
