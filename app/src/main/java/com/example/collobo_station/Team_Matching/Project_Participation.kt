package com.example.collobo_station.Team_Matching

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
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
            // 클릭된 아이템의 데이터를 가지고 다음 페이지로 이동
            val intent = Intent(this, Team_Looking_Write::class.java)
            startActivity(intent)
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
                val dataList = mutableListOf<String>()
                for (document in documents) {
                    val text1 = document.getString("title") ?: ""
                    val text2 = document.getString("content") ?: ""
                    val text3 = document.getString("nickname") ?: ""
                    val text4 = document.getString("timestamp") ?: ""
                    val text5 = document.getString("url") ?: ""
                    val text6 = document.getString("field") ?: ""
                    val text7 = document.getString("topic") ?: ""
                    dataList.add("$text1, $text2, $text3, $text4, $text5, $text6, $text7")
                }
                // 어댑터에 데이터 설정
                Team_adapter.setData(dataList)
            }
            .addOnFailureListener { exception ->
                // 실패 시 처리
            }
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

        collectionRef.whereEqualTo("topic", selectedText1) // topic과 선택된 text4 비교
            .get()
            .addOnSuccessListener { documents ->
                val filteredDataList = mutableListOf<String>()
                for (document in documents) {
                    val text1 = document.getString("title") ?: ""
                    val text2 = document.getString("content") ?: ""
                    val text3 = document.getString("nickname") ?: ""
                    val text4 = document.getString("timestamp") ?: ""
                    val text5 = document.getString("url") ?: ""
                    val text6 = document.getString("field") ?: ""
                    val text7 = document.getString("topic") ?: ""
                    filteredDataList.add("$text1, $text2, $text3, $text4, $text5, $text6, $text7")
                }
                // 필터링된 데이터를 Team_adapter에 설정
                Team_adapter.setData(filteredDataList)
            }
            .addOnFailureListener { exception ->
                // 실패 시 처리
                exception.printStackTrace()
            }
    }

}