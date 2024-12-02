package com.example.collobo_station.Adapter.Scrap

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.collobo_station.Data.Model
import com.example.collobo_station.R

class ScrapPagerAdapter(private val dataList: List<Model>) :
    RecyclerView.Adapter<ScrapPagerAdapter.ScrapViewHolder>() {

    inner class ScrapViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.scrap_name)
        val image: ImageView = itemView.findViewById(R.id.scrap_image)
        val url: TextView = itemView.findViewById(R.id.scrap_description)
        val itemCard: CardView = itemView.findViewById(R.id.itemCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScrapViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_scrap_page, parent, false)
        return ScrapViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScrapViewHolder, position: Int) {
        val item = dataList[position]
        holder.title.text = item.title

        // 이미지 로드 (Glide 사용)
        Glide.with(holder.itemView.context)
            .load(item.imageUrl)
            .into(holder.image)

        // CardView 배경 색상 설정
        holder.itemCard.setCardBackgroundColor(item.color)

        // 클릭 시 URL로 이동
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val pageUrl = item.pageUrl

            if (pageUrl.isNotBlank() && pageUrl.startsWith("http")) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(pageUrl))
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "유효하지 않은 URL입니다.", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}
