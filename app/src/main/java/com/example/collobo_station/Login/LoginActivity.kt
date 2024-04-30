package com.example.collobo_station.Login

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.collobo_station.Main.MainActivity
import com.example.collobo_station.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPreferences: SharedPreferences

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
        binding = ActivityLoginBinding.inflate(layoutInflater) // 수정된 부분
        setContentView(binding.root)

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE)

        // Firebase 초기화
        auth = Firebase.auth
        //push test
        // View 요소 가져오기
        val id: EditText = binding.userId
        val pw: EditText = binding.userPw
        val loginButton: Button = binding.loginBtn
        val ID_PWButton : Button = binding.idPwBtn
        val createBtn3 : Button = binding.createBtn3


        // 로그인 버튼 클릭 리스너 설정
        loginButton.setOnClickListener {
            val inputId = id.text.toString()
            val inputPw = pw.text.toString()

            if (inputId.isEmpty() || inputPw.isEmpty()) {
                Toast.makeText(this, "빈 값을 입력하셨습니다.", Toast.LENGTH_SHORT).show()
            } else {
                login(inputId, inputPw)
            }
        }

        // 비밀번호 찾기 버튼 클릭 시
        ID_PWButton.setOnClickListener {
            val intent = Intent(this, ID_PW_Find::class.java)
            startActivity(intent)
        }

        // 회원가입 버튼 클릭 시
        createBtn3.setOnClickListener {
            val intent = Intent(this, CreateActivity::class.java)
            startActivity(intent)
        }
    }

    private fun login(id: String, pw: String) {
        auth.signInWithEmailAndPassword(id, pw)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // 로그인 성공 시 SharedPreferences에 저장
                    with(sharedPreferences.edit()) {
                        putString("username", id)
                        putString("password", pw)
                        putBoolean("isLoggedIn", true)
                        apply()
                    }

                    Toast.makeText(this, "로그인에 성공했습니다!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "아이디와 비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
