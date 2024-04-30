package com.example.collobo_station.Adapter

import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.collobo_station.R
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.storage.storage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TabAllAdapter(private val items: MutableList<DocumentSnapshot>) :
    RecyclerView.Adapter<TabAllAdapter.TabAllViewHolder>() {
    private var itemList = mutableListOf<DocumentSnapshot>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TabAllViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home_contest, parent, false)
        
        return TabAllViewHolder(view)
    }

    private fun calculateDDay(eventDate: Date): String {
        val currentDate = Calendar.getInstance().time
        val diff = eventDate.time - currentDate.time
        val days = diff / (1000 * 60 * 60 * 24)
        return if (days >= 0) {
            "D-${days + 1}"
        } else {
            "D+${-days}"
        }
    }
    override fun onBindViewHolder(holder: TabAllViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun setItems(items: List<DocumentSnapshot>) {
        itemList.clear()
        itemList.addAll(items)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    // 아이템 클릭 리스너 인터페이스 정의
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    // 아이템 클릭 리스너 멤버 변수
    private var itemClickListener: OnItemClickListener? = null

    // 아이템 클릭 리스너 설정 메서드
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.itemClickListener = listener
    }

    inner class TabAllViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val contestImage: ImageView = itemView.findViewById(R.id.contest_image)
        private val contestName: TextView = itemView.findViewById(R.id.Contest_name)
        private val contestDayStart: TextView = itemView.findViewById(R.id.Contest_Day_Start)
        private val contestDayLast: TextView = itemView.findViewById(R.id.Contest_Day_Last)
        private val contestField: TextView = itemView.findViewById(R.id.Contest_Feild)
        private val contestCountText: TextView = itemView.findViewById(R.id.Contest_Count)

        init {
            // 아이템 뷰에 클릭 리스너 설정
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener?.onItemClick(position)
                }
            }
        }

        fun bind(item: DocumentSnapshot) {
            val contestData = item.data
            contestData?.let {
                // 텍스트 뷰에 데이터 설정
                contestName.text = it["대회명"] as? String ?: "데이터가 없습니다."
                // 접수 기간을 Timestamp에서 Date로 변환
                val timestamp = item.getTimestamp("접수마감")
                val date = timestamp?.toDate()

                contestDayStart.text = it["접수시작"] as? String ?: "데이터가 없습니다."
                // Date를 문자열로 변환하여 TextView에 설정
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val dateString = date?.let { dateFormat.format(it) } ?: "데이터가 없습니다."
                contestDayLast.text = dateString

                // D-day 계산
                val dDay = date?.let { it1 -> calculateDDay(it1) }
                contestCountText.text = dDay.toString()  // D-day 값을 TextView에 설정

                contestField.text = it["분야"] as? String ?: "데이터가 없습니다."

                val imageUrl = it["이미지"] as? String
                val storageReference =
                    imageUrl?.let { url -> Firebase.storage.reference.child(url) }

                // 이미지 파일 다운로드 및 처리
                storageReference?.getBytes(Long.MAX_VALUE)
                    ?.addOnSuccessListener { bytes ->
                        // 다운로드 성공 시 바이트 데이터를 비트맵으로 변환하여 ImageView에 설정
                        val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        contestImage.setImageBitmap(bmp)
                    }
                    ?.addOnFailureListener { exception ->
                        // 다운로드 실패 시

                    }
            }
        }
    }
}
