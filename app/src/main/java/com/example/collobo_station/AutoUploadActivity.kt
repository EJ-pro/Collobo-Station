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

        // 테스트 계정 0~9 생성
        for (i in 0..5) {
            val userEmail = "test$i@mokwon.ac.kr" // 이메일 (문서 ID로 사용)
            val userData = mapOf(
                "nickname" to "test$i",
                "name" to "Test User $i",
                "email" to userEmail,
                "dob" to "200$i-01-01", // 테스트용 생년월일
                "phone" to "010-1234-000$i",
                "address" to "테스트 주소 $i",
                "education" to "목원 대학교 $i",
                "grade" to "4학년",
                "awards" to listOf(
                    "테스트 상 $i"
                ),
                "skills" to listOf(
                    "Android", "Java", "Kotlin"
                ),
                "profile_cover" to "테스트 이미지 URL $i"
            )

            // Firestore로 데이터 업로드
            uploadUserDataToFirestore(userEmail, userData)
        }
    }

    private fun uploadUserDataToFirestore(documentId: String, data: Map<String, Any>) {
        val documentRef = firestore.collection("Users").document(documentId)

        documentRef.set(data)
            .addOnSuccessListener {
                Toast.makeText(this, "계정 $documentId 업로드 성공!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "계정 $documentId 업로드 실패: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
