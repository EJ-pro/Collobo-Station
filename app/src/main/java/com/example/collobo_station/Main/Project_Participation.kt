package com.example.collobo_station.Main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.collobo_station.Adapter.Project_ParticipationAdapter
import com.example.collobo_station.R
import com.google.firebase.firestore.FirebaseFirestore

class Project_Participation : AppCompatActivity()  {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: Project_ParticipationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.action_project_participation)

        // 리사이클러뷰를 XML에서 찾아옵니다.
        recyclerView = findViewById(R.id.project_recyclerview)

        // 가로로 스크롤되는 레이아웃 매니저를 설정합니다.
        val layoutManager = GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager

        // 어댑터를 생성하고 리사이클러뷰에 연결합니다.
        adapter = Project_ParticipationAdapter()
        recyclerView.adapter = adapter

        // Firestore에서 데이터 가져오기
        fetchDataFromFirestore()
    }

    private fun fetchDataFromFirestore() {
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
                adapter.setData(dataList)
            }
            .addOnFailureListener { exception ->
                // 실패 시 처리
            }
    }
}