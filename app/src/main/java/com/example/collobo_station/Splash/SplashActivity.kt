package com.example.collobo_station.Splash

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.collobo_station.Login.LoginActivity
import com.example.collobo_station.Main.MainActivity
import com.example.collobo_station.R

class SplashActivity : AppCompatActivity() {

    private var isExpanded = false
    private var doubleBackToExitPressedOnce = false

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()

        android.os.Handler().postDelayed({
            doubleBackToExitPressedOnce = false
        }, 2000) // 2초간 두 번째 뒤로가기 버튼이 클릭되지 않으면 리셋
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // 일정 시간 지연 이후 실행하기 위한 코드
        Handler(Looper.getMainLooper()).postDelayed({

            // SharedPreferences를 사용하여 자동 로그인 확인
            val sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
            val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

            // 자동 로그인이 활성화된 경우 MainActivity로 이동
            if (isLoggedIn) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                // 자동 로그인이 비활성화된 경우 LoginActivity로 이동
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }

            // 이전 키를 눌렀을 때 스플래스 스크린 화면으로 이동을 방지하기 위해
            // 이동한 다음 사용안함으로 finish 처리
            finish()

        }, 2000) // 시간 2초 이후 실행
    }
}
