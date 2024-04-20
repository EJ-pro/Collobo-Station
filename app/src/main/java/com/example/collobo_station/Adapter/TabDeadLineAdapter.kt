package com.example.collobo_station.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.collobo_station.R

class TabDeadLineAdapter (private val items: List<String>) :
    RecyclerView.Adapter<TabDeadLineAdapter.TabDeadLineViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TabDeadLineViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tab_all, parent, false)
        return TabDeadLineViewHolder(view)
    }

    override fun onBindViewHolder(holder: TabDeadLineViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class TabDeadLineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.text_view_item)

        fun bind(item: String) {
            textView.text = item
        }
    }
}