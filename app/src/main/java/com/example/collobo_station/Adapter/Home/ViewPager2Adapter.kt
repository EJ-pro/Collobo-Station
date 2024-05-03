package com.example.collobo_station.Adapter.Home

import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.collobo_station.R
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class ViewPager2Adapter(private var imageUrls: List<String>, private val context: android.content.Context) : RecyclerView.Adapter<ViewPager2Adapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewpager2_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageUrl = imageUrls[position]
        holder.bind(imageUrl)
    }

    override fun getItemCount(): Int {
        return imageUrls.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.image)

        fun bind(imageUrl: String) {
            val storageReference = Firebase.storage.reference.child(imageUrl)

            storageReference.getBytes(Long.MAX_VALUE)
                .addOnSuccessListener { bytes ->
                    // 이미지 다운로드 성공 시 비트맵으로 변환하여 ImageView에 설정
                    val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    imageView.setImageBitmap(bmp)
                }
                .addOnFailureListener { exception ->
                    // 이미지 다운로드 실패 시 로그 기록
                    Log.e("ViewPager2Adapter", "Error downloading image", exception)
                }
        }
    }
}
