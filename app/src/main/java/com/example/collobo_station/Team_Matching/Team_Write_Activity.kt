package com.example.collobo_station.Team_Matching

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.collobo_station.Data.DataInfo
import com.example.collobo_station.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class Team_Write_Activity : AppCompatActivity() {
    private lateinit var titleEditText: EditText
    private lateinit var contentEditText: EditText
    private lateinit var submitButton: Button

    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_write)

        titleEditText = findViewById(R.id.editTextTitle)
        contentEditText = findViewById(R.id.editTextContent)
        submitButton = findViewById(R.id.buttonSubmit)

        submitButton.setOnClickListener {
            writeDataToFirestore()
            navigateToProjectParticipation()
        }
    }
    private fun navigateToProjectParticipation() {
        val intent = Intent(this, Project_Participation::class.java)
        startActivity(intent)
        finish() // 현재 액티비티 종료
    }
    private fun writeDataToFirestore() {
        val title = titleEditText.text.toString().trim()
        val content = contentEditText.text.toString().trim()
        val timestamp = Calendar.getInstance().timeInMillis
        // DataInfo에서 사용자 정보 가져오기
        val userInfo = DataInfo.getUserInfo()
        val nickname = userInfo?.nickname ?: ""
        // Firestore에 데이터 쓰기
        val documentName = "$nickname${System.currentTimeMillis()}"
        val data = hashMapOf(
            "title" to title,
            "content" to content,
            "userName" to nickname,
            "timestamp" to timestamp
        )
        firestore.collection("Team_Matching").document(documentName)
            .set(data)
            .addOnSuccessListener {
                // 성공적으로 저장된 경우
                // 여기에 성공 처리 코드 추가
            }
            .addOnFailureListener { e ->
                // 저장 실패한 경우
                // 여기에 실패 처리 코드 추가
            }
    }
}
