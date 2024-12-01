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

//        val dataList = listOf(
//            mapOf(
//                "title" to "Kotlin으로 간단한 안드로이드 앱 제작하실 분 구합니다!",
//                "nickname" to "김영훈",
//                "content" to "로그인 화면 구현 예정",
//                "topic" to "Android / IOS",
//                "timestamp" to "2024-03-22 11:10"
//            )
//        )
//
//
//
//        uploadDataToFirestore(dataList)
    }

    private fun uploadDataToFirestore(dataList: List<Map<String, Any>>) {
        val collectionRef = firestore.collection("Team_Matching")

        for (data in dataList) {
            collectionRef.add(data)
                .addOnSuccessListener {
                    Toast.makeText(this, "데이터 업로드 성공!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "데이터 업로드 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
