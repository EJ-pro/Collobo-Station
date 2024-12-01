package com.example.collobo_station.Team_Matching

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.collobo_station.Data.DataInfo
import com.example.collobo_station.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class Team_Write_Activity : AppCompatActivity() {
    private lateinit var titleEditText: EditText
    private lateinit var contentEditText: EditText
    private lateinit var urlEditText: EditText
    private lateinit var submitButton: Button
    private lateinit var designerButton: Button
    private lateinit var developerButton: Button
    private lateinit var uxUiButton: Button
    private lateinit var mediaButton: Button
    private lateinit var contestButton: Button
    private lateinit var illustrationButton: Button

    private var selectedField: String? = null
    private var selectedTopic: String? = null

    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_write)

        titleEditText = findViewById(R.id.editTextTitle)
        contentEditText = findViewById(R.id.editTextContent)
        urlEditText = findViewById(R.id.editURL)
        submitButton = findViewById(R.id.buttonSubmit)

        designerButton = findViewById(R.id.designerButton)
        developerButton = findViewById(R.id.developerButton)
        uxUiButton = findViewById(R.id.uxUiButton)
        mediaButton = findViewById(R.id.mediaButton)
        contestButton = findViewById(R.id.contestButton)
        illustrationButton = findViewById(R.id.illustrationButton)

        // 팀원 분야 버튼 그룹
        setupToggleButtonGroup(listOf(designerButton, developerButton), isDeveloperGroup = true)

        // 주제 버튼 그룹
        setupToggleButtonGroup(listOf(uxUiButton, mediaButton, contestButton, illustrationButton))

        submitButton.setOnClickListener {
            writeDataToFirestore()
            navigateToProjectParticipation()
        }
    }

    private fun setupToggleButtonGroup(buttons: List<Button>, isDeveloperGroup: Boolean = false) {
        buttons.forEach { button ->
            button.setOnClickListener {
                // 모든 버튼을 비활성화 색상으로 설정
                buttons.forEach {
                    it.setBackgroundResource(R.drawable.unselected_button_background)
                    it.setTextColor(ContextCompat.getColor(this, R.color.black)) // 비활성화 텍스트 색상
                }
                // 클릭된 버튼 활성화 색상으로 설정
                button.setBackgroundResource(R.drawable.selected_button_background)
                button.setTextColor(ContextCompat.getColor(this, R.color.white)) // 활성화 텍스트 색상

                if (isDeveloperGroup) {
                    selectedField = when (button.id) {
                        R.id.developerButton -> "개발자"
                        R.id.designerButton -> "디자이너"
                        else -> null
                    }
                } else {
                    selectedTopic = when (button.id) {
                        R.id.uxUiButton -> uxUiButton.text.toString()
                        R.id.mediaButton -> mediaButton.text.toString()
                        R.id.contestButton -> contestButton.text.toString()
                        R.id.illustrationButton -> illustrationButton.text.toString()
                        else -> null
                    }
                }
                // 개발자 버튼 그룹일 경우 주제 텍스트 변경
                if (isDeveloperGroup) {
                    when (button.id) {
                        R.id.developerButton -> {
                            updateTopicsForDeveloper() // 개발자 주제로 변경
                        }
                        R.id.designerButton -> {
                            updateTopicsForDesigner() // 디자이너 주제로 변경
                        }
                    }
                }
            }
        }
    }

    private fun updateTopicsForDeveloper() {
        uxUiButton.text = "Game Engine"
        mediaButton.text = "Web Front"
        contestButton.text = "Web Back"
        illustrationButton.text = "Android / IOS"
    }

    private fun updateTopicsForDesigner() {
        uxUiButton.text = "UXUI"
        mediaButton.text = "Graphic"
        contestButton.text = "3D Design"
        illustrationButton.text = "Illustration"
    }
    private fun navigateToProjectParticipation() {
        val intent = Intent(this, Project_Participation::class.java)
        startActivity(intent)
        finish() // 현재 액티비티 종료
    }
    private fun writeDataToFirestore() {
        val title = titleEditText.text.toString().trim()
        val content = contentEditText.text.toString().trim()
        val url = urlEditText.text.toString().trim()
        val timestamp = getCurrentDateTime()
        // 필드와 주제 선택 여부 확인
        if (selectedField == null || selectedTopic == null) {
            Toast.makeText(this, "분야와 주제를 선택하세요.", Toast.LENGTH_SHORT).show()
            return
        }

        // DataInfo에서 사용자 정보 가져오기
        val userInfo = DataInfo.getUserInfo()
        val nickname = userInfo?.nickname ?: ""
        // Firestore에 데이터 쓰기
        val documentName = "$nickname${System.currentTimeMillis()}"
        val data = hashMapOf(
            "title" to title,
            "content" to content,
            "nickname" to nickname,
            "timestamp" to timestamp,
            "url" to url,
            "field" to selectedField, // 선택된 분야
            "topic" to selectedTopic
        )
        firestore.collection("Team_Matching").document(documentName)
            .set(data)
            .addOnSuccessListener {
                Toast.makeText(this, "성공적으로 저장되었습니다.", Toast.LENGTH_SHORT).show()
                navigateToProjectParticipation()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "저장에 실패했습니다: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    private fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("yy-MM-dd HH:mm", Locale.getDefault())
        val date = Date()
        return dateFormat.format(date)
    }
}
