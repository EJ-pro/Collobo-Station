package com.example.collobo_station.Adapter.Team

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.collobo_station.R

class Project_ParticipationAdapter :
    RecyclerView.Adapter<Project_ParticipationAdapter.ViewHolder>() {

    private var dataList = listOf<String>() // 데이터를 저장하는 리스트
    private var itemClickListener: ((String) -> Unit)? = null // 클릭 리스너

    // ViewHolder 클래스 정의
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView1: TextView = itemView.findViewById(R.id.text1)
        val textView2: TextView = itemView.findViewById(R.id.text2)
        val textView3: TextView = itemView.findViewById(R.id.text3)
        init {
            // 아이템 클릭 이벤트 설정
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = dataList[position]
                    val selectedText1 = item.split(", ").getOrNull(0) ?: "N/A"
                    itemClickListener?.invoke(selectedText1)
                }
            }
        }
    }

    // onCreateViewHolder: ViewHolder를 생성하고 레이아웃을 연결
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_project_team, parent, false)
        return ViewHolder(view)
    }

    // onBindViewHolder: ViewHolder에 데이터를 바인딩
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position].split(", ") // 데이터를 ","로 구분하여 나눔
        holder.textView1.text = data.getOrNull(0) ?: "N/A" // text1
        holder.textView2.text = data.getOrNull(1) ?: "N/A" // text2
        holder.textView3.text = data.getOrNull(2) ?: "N/A" // text3
    }

    // getItemCount: 데이터의 개수를 반환
    override fun getItemCount(): Int {
        return dataList.size
    }

    // 데이터 설정 메서드
    fun setData(dataList: List<String>) {
        this.dataList = dataList
        notifyDataSetChanged() // 데이터 변경 후 갱신
    }

    // 클릭 리스너 설정 메서드
    fun setItemClickListener(listener: (String) -> Unit) {
        this.itemClickListener = listener
    }
}
