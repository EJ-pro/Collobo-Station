package com.example.collobo_station.Main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.collobo_station.Adapter.Project_ParticipationAdapter
import com.example.collobo_station.R

class Project_Participation : AppCompatActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.action_project_participation)

        // 리사이클러뷰와 뷰페이저2를 XML에서 찾아옵니다.
        val recyclerView = findViewById<RecyclerView>(R.id.project_recyclerview)
        val viewPager2 = findViewById<ViewPager2>(R.id.project_viewpager2)


        // 가로로 스크롤되는 레이아웃 매니저를 설정합니다.
        val layoutManager = GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager
        // 어댑터를 생성하고 리사이클러뷰에 연결합니다.
        val adapter = Project_ParticipationAdapter(getDataList())
        recyclerView.adapter = adapter
    }
    private fun getDataList(): List<String> {
        return listOf("팀원 1", "팀원 2", "팀원 3", "팀원 4") // 원하는 데이터 리스트를 반환하도록 수정해야 합니다.
    }
}