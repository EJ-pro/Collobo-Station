package com.example.collobo_station.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.collobo_station.R

class TabAllAdapter(private val items: List<String>) :
    RecyclerView.Adapter<TabAllAdapter.TabAllViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TabAllViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tab_all, parent, false)
        return TabAllViewHolder(view)
    }

    override fun onBindViewHolder(holder: TabAllViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class TabAllViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.text_view_item)

        fun bind(item: String) {
            textView.text = item
        }
    }
}
