package com.example.collobo_station.Adapter.Scrap

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.collobo_station.Data.Model
import com.example.collobo_station.R

class ScrapPagerAdapter(private val dataList: List<Model>) :
    RecyclerView.Adapter<ScrapPagerAdapter.ScrapViewHolder>() {

    inner class ScrapViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // ViewHolder 내부에서 각 뷰를 참조할 수 있게 정의합니다.
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScrapViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_scrap_page, parent, false)
        return ScrapViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScrapViewHolder, position: Int) {
        val model = dataList[position]
        holder.itemView.findViewById<CardView>(R.id.itemCard).setCardBackgroundColor(model.color)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}
