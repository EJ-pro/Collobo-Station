package com.example.collobo_station.Fragment.Tab

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.collobo_station.Adapter.TabFieldAdapter
import com.example.collobo_station.R

class Fragment_Tab_Field : Fragment() {
    private lateinit var recyclerView: RecyclerView
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tab_field, container, false)
        recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = TabFieldAdapter(getTabAllItems())
        // 리사이클러뷰 초기화 및 데이터 로드
        return view
    }
    fun adjustRecyclerViewSize() {
        recyclerView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        recyclerView.requestLayout()
    }
    private fun getTabAllItems(): List<String> {
        // 리사이클러뷰에 표시할 데이터를 생성하거나 가져옵니다.
        return listOf("Item 1", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6")
    }
}