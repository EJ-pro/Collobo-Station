package com.example.collobo_station.Team_Matching

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.example.collobo_station.R

class AutoUploadActivity : AppCompatActivity() {

    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auto_upload)

        // 업로드할 데이터
        val userEmail = "hsshss2522@naver.com" // 이메일 (문서 ID로 사용)
        val userData = mapOf(
            "name" to "이재희",
            "email" to userEmail,
            "dob" to "2000-09-20",
            "phone" to "010-3951-1401",
            "address" to "경기도 이천시",
            "education" to "목원대학교 컴퓨터공학과 재학",
            "grade" to "4학년",
            "awards" to listOf(
                "제25회 입사서류 경진대회 - 장려상",
                "2024 진로 포트폴리오 경진대회 - 최우수상(1위)"
            ),
            "skills" to listOf(
                "Android", "Java", "Kotlin", "React", "Firebase"
            ),
            "profile_cover" to "이미지 URL"
        )

        // Firestore로 데이터 업로드
        uploadUserDataToFirestore(userEmail, userData)
    }

    private fun uploadUserDataToFirestore(documentId: String, data: Map<String, Any>) {
        val documentRef = firestore.collection("Users").document(documentId)

        documentRef.set(data)
            .addOnSuccessListener {
                Toast.makeText(this, "데이터 업로드 성공!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "데이터 업로드 실패: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
