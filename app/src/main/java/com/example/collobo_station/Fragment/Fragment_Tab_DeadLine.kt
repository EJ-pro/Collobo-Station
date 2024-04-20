package com.example.collobo_station.Fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.collobo_station.Adapter.TabAllAdapter
import com.example.collobo_station.Adapter.TabDeadLineAdapter
import com.example.collobo_station.R

class Fragment_Tab_DeadLine: Fragment() {
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_deadline, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = TabDeadLineAdapter(getTabAllItems())
        // 리사이클러뷰 초기화 및 데이터 로드
        return view
    }
    private fun getTabAllItems(): List<String> {
        // 리사이클러뷰에 표시할 데이터를 생성하거나 가져옵니다.
        return listOf("Item 1", "Item 2", "Item 3", "Item 4")
    }
}