package com.example.collobo_station.Team_Matching

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.collobo_station.Adapter.Team.Project_ParticipationAdapter
import com.example.collobo_station.Adapter.Team.Project_ParticipationAdapter_Developer
import com.example.collobo_station.Adapter.Team.Project_ParticipationAdapter_Team
import com.example.collobo_station.Main.MainActivity
import com.example.collobo_station.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Locale

class Project_Participation : AppCompatActivity()  {
    private lateinit var Designer_recyclerView: RecyclerView
    private lateinit var Developer_recyclerView: RecyclerView
    private lateinit var Team_recyclerView: RecyclerView
    private lateinit var Designer_adapter: Project_ParticipationAdapter
    private lateinit var Developer_adapter: Project_ParticipationAdapter_Developer
    private lateinit var Team_adapter: Project_ParticipationAdapter_Team
    private lateinit var backbtn : ImageView
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.action_project_participation)

        // 리사이클러뷰를 XML에서 찾아옵니다.
        Designer_recyclerView = findViewById(R.id.Designer_recyclerview)
        Developer_recyclerView = findViewById(R.id.Developer_recyclerview)
        Team_recyclerView = findViewById(R.id.Team_recyclerview)
        backbtn = findViewById(R.id.backbtn)

        // 가로로 스크롤되는 레이아웃 매니저를 설정합니다.
        val Designer_layoutManager = GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false)
        Designer_recyclerView.layoutManager = Designer_layoutManager

        val Developer_layoutManager = GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false)
        Developer_recyclerView.layoutManager = Developer_layoutManager

        val Team_layoutManager = GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false)
        Team_recyclerView.layoutManager = Team_layoutManager

        // 어댑터를 생성하고 리사이클러뷰에 연결합니다.
        Designer_adapter = Project_ParticipationAdapter()
        Developer_adapter = Project_ParticipationAdapter_Developer()
        Team_adapter = Project_ParticipationAdapter_Team()

        Designer_recyclerView.adapter = Designer_adapter
        Developer_recyclerView.adapter = Developer_adapter
        Team_recyclerView.adapter = Team_adapter

        // Firestore에서 데이터 가져오기
        fetchDataFromFirestore_Designer()
        fetchDataFromFirestore_Developer()
        fetchDataFromFirestore_Team()

        // 리사이클러뷰 아이템 클릭 리스너 설정
        Designer_adapter.setItemClickListener { selectedText1 ->
            fetchFilteredTeamMatchingData(selectedText1)
        }

        Developer_adapter.setItemClickListener { selectedText1 ->
            fetchFilteredTeamMatchingData(selectedText1)
        }

        Team_adapter.setItemClickListener { item ->
            val title = item.split(",")[0].trim() // "title" 부분만 추출
            val db = FirebaseFirestore.getInstance()

            db.collection("Team_Matching")
                .whereEqualTo("title", title)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        Toast.makeText(this, "해당 데이터를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                    } else {
                        val document = documents.documents[0]
                        val url = document.getString("url")

                        if (!url.isNullOrEmpty()) {
                            val kakaoIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            kakaoIntent.setPackage("com.kakao.talk")
                            try {
                                startActivity(kakaoIntent)
                            } catch (e: Exception) {
                                // 카카오톡 앱이 없으면 기본 브라우저로 링크 열기
                                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                startActivity(browserIntent)
                            }
                        } else {
                            Toast.makeText(this, "URL이 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "데이터 로드 실패: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }



        // 플로팅 액션 버튼 클릭 리스너 설정
        val fabButton: FloatingActionButton = findViewById(R.id.team_plus_btn)
        fabButton.setOnClickListener {
            // Team_Write_Activity로 이동
            val intent = Intent(this, Team_Write_Activity::class.java)
            startActivity(intent)
            finish()
        }
        backbtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun fetchDataFromFirestore_Team() {
        val db = FirebaseFirestore.getInstance()
        val collectionRef = db.collection("Team_Matching")

        collectionRef.get()
            .addOnSuccessListener { documents ->
                val dataList = mutableListOf<Pair<String, String>>()
                for (document in documents) {
                    val text1 = document.getString("title") ?: ""
                    val text2 = document.getString("content") ?: ""
                    val text3 = document.getString("nickname") ?: ""
                    val text4 = document.getString("timestamp") ?: "" // 문자열 형태의 timestamp
                    val text5 = document.getString("url") ?: ""
                    val text6 = document.getString("field") ?: ""
                    val text7 = document.getString("topic") ?: ""
                    val combinedData = "$text1, $text2, $text3, $text4, $text5, $text6, $text7"

                    dataList.add(Pair(combinedData, text4))
                }

                // 정렬 후 데이터 설정
                val sortedDataList = sortDataByTimestamp(dataList)
                Team_adapter.setData(sortedDataList)
                Team_adapter.notifyDataSetChanged() // 데이터 변경 알림
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "데이터 가져오기에 실패했습니다.", Toast.LENGTH_SHORT).show()
                Log.e("FirestoreError", "Error: ${exception.message}")
            }
    }
    private fun sortDataByTimestamp(dataList: List<Pair<String, String>>): List<String> {
        return dataList.sortedByDescending {
            try {
                SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).parse(it.second)
            } catch (e: Exception) {
                null // 잘못된 날짜는 맨 뒤로
            }
        }.map { it.first }
    }
    private fun fetchDataFromFirestore_Designer() {
        val db = FirebaseFirestore.getInstance()
        val collectionRef = db.collection("Team_Looking")

        collectionRef.get()
            .addOnSuccessListener { documents ->
                val dataList = mutableListOf<String>()
                for (document in documents) {
                    val text1 = document.getString("text1") ?: ""
                    val text2 = document.getString("text2") ?: ""
                    val text3 = document.getString("text3") ?: ""
                    dataList.add("$text1, $text2, $text3")
                }
                // 어댑터에 데이터 설정
                Designer_adapter.setData(dataList)
            }
            .addOnFailureListener { exception ->
                // 실패 시 처리
            }
    }
    private fun fetchDataFromFirestore_Developer() {
        val db = FirebaseFirestore.getInstance()
        val collectionRef = db.collection("Team_Looking_Developer")

        collectionRef.get()
            .addOnSuccessListener { documents ->
                val dataList = mutableListOf<String>()
                for (document in documents) {
                    val text1 = document.getString("text1") ?: ""
                    val text2 = document.getString("text2") ?: ""
                    val text3 = document.getString("text3") ?: ""
                    dataList.add("$text1, $text2, $text3")
                }
                // 어댑터에 데이터 설정
                Developer_adapter.setData(dataList)
            }
            .addOnFailureListener { exception ->
                // 실패 시 처리
            }
    }
    private fun fetchFilteredTeamMatchingData(selectedText1: String) {
        val db = FirebaseFirestore.getInstance()
        val collectionRef = db.collection("Team_Matching")

        collectionRef.whereEqualTo("topic", selectedText1) // topic 조건
            .get()
            .addOnSuccessListener { documents ->
                val filteredDataList = mutableListOf<Pair<String, String>>() // 데이터와 timestamp를 함께 저장
                for (document in documents) {
                    val text1 = document.getString("title") ?: ""
                    val text2 = document.getString("content") ?: ""
                    val text3 = document.getString("nickname") ?: ""
                    val text4 = document.getString("timestamp") ?: "" // 문자열 형태의 timestamp
                    val text5 = document.getString("url") ?: ""
                    val text6 = document.getString("field") ?: ""
                    val text7 = document.getString("topic") ?: ""
                    val combinedData = "$text1, $text2, $text3, $text4, $text5, $text6, $text7"

                    filteredDataList.add(Pair(combinedData, text4))
                }

                // timestamp 기준으로 최신순 정렬 (내림차순)
                val sortedDataList = filteredDataList.sortedByDescending {
                    SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).parse(it.second)
                }.map { it.first }

                // 정렬된 데이터를 Team_adapter에 설정
                Team_adapter.setData(sortedDataList)
            }
            .addOnFailureListener { exception ->
                // 실패 시 처리
                Toast.makeText(this, "데이터 가져오기에 실패했습니다.", Toast.LENGTH_SHORT).show()
                exception.printStackTrace()
            }
    }



}