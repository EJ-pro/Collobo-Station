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

class TabFieldAdapter(private val items: MutableList<DocumentSnapshot>, private val itemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<TabFieldAdapter.TabFieldViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TabFieldViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home_contest, parent, false)
        return TabFieldViewHolder(view)
    }

    override fun onBindViewHolder(holder: TabFieldViewHolder, position: Int) {
        holder.bind(items[position])
        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(position)
        }
    }

    fun setItems(items: List<DocumentSnapshot>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class TabFieldViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val contestImage: ImageView = itemView.findViewById(R.id.contest_image)
        private val contestName: TextView = itemView.findViewById(R.id.Contest_name)
        private val contestDayLast: TextView = itemView.findViewById(R.id.Contest_Day_Last)
        private val contestDayStart: TextView = itemView.findViewById(R.id.Contest_Day_Start)
        private val contestField: TextView = itemView.findViewById(R.id.Contest_Feild)
        private val contestCount: TextView = itemView.findViewById(R.id.Contest_Count)

        fun bind(item: DocumentSnapshot) {
            val contestData = item.data
            contestData?.let {
                // 텍스트 뷰에 데이터 설정
                contestName.text = it["대회명"] as? String ?: "데이터가 없습니다."
                contestDayStart.text = it["접수시작"] as? String ?: "데이터가 없습니다."
                contestDayLast.text = it["접수마감"] as? String ?: "데이터가 없습니다."
                contestField.text = it["분야"] as? String ?: "데이터가 없습니다."
                contestCount.text = it["D-day"] as? String ?: "데이터가 없습니다."

                val imageUrl = it["이미지"] as? String
                val storageReference = imageUrl?.let { url -> Firebase.storage.reference.child(url) }

                // 이미지 파일 다운로드 및 처리
                storageReference?.getBytes(Long.MAX_VALUE)
                    ?.addOnSuccessListener { bytes ->
                        // 다운로드 성공 시 바이트 데이터를 비트맵으로 변환하여 ImageView에 설정
                        val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        contestImage.setImageBitmap(bmp)
                    }
                    ?.addOnFailureListener { exception ->
                        // 다운로드 실패 시 에러 로그 출력
                        Log.e("Fragment_Tab_Field", "Error downloading image", exception)
                    }
            }
        }
    }
}