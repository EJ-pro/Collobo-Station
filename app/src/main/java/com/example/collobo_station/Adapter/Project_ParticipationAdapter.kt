package com.example.collobo_station.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.collobo_station.R

class Project_ParticipationAdapter (private val data: List<String>) :
    RecyclerView.Adapter<Project_ParticipationAdapter.ViewHolder>() {

    // ViewHolder 클래스 정의
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.Field)
    }

    // onCreateViewHolder: 뷰 홀더를 생성하고 레이아웃을 연결합니다.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_project_team, parent, false)
        return ViewHolder(view)
    }

    // onBindViewHolder: 뷰 홀더에 데이터를 바인딩합니다.
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = data[position]
    }

    // getItemCount: 데이터의 개수를 반환합니다.
    override fun getItemCount(): Int {
        return data.size
    }
}