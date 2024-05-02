package com.example.collobo_station.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.collobo_station.R

class Project_ParticipationAdapter_Developer :
    RecyclerView.Adapter<Project_ParticipationAdapter_Developer.ViewHolder>() {

    private var dataList = listOf<String>()
    private var itemClickListener: ((String) -> Unit)? = null

    // ViewHolder 클래스 정의
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView1: TextView = itemView.findViewById(R.id.text1)
        val textView2: TextView = itemView.findViewById(R.id.text2)
        val textView3: TextView = itemView.findViewById(R.id.text3)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = dataList[position]
                    // 클릭된 아이템의 데이터를 인터페이스를 통해 외부로 전달
                    itemClickListener?.invoke(item)
                }
            }
        }
    }

    // onCreateViewHolder: 뷰 홀더를 생성하고 레이아웃을 연결합니다.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_project_team, parent, false)
        return ViewHolder(view)
    }

    // onBindViewHolder: 뷰 홀더에 데이터를 바인딩합니다.
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position].split(", ")
        holder.textView1.text = data[0]
        holder.textView2.text = data[1]
        holder.textView3.text = data[2]
    }

    // getItemCount: 데이터의 개수를 반환합니다.
    override fun getItemCount(): Int {
        return dataList.size
    }

    // 데이터 설정 메소드
    fun setData(dataList: List<String>) {
        this.dataList = dataList
        notifyDataSetChanged()
    }
    // 클릭 리스너 설정 메소드
    fun setItemClickListener(listener: (String) -> Unit) {
        this.itemClickListener = listener
    }
}
