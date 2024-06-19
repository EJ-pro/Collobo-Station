package com.example.collobo_station.Adapter.Global

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.collobo_station.R

class Global_DesignerAdapter : RecyclerView.Adapter<Global_DesignerAdapter.ViewHolder>() {

    private var dataList: List<String> = emptyList()

    fun setData(data: List<String>) {
        this.dataList = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_global_designer, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val text = dataList[position]
        holder.textView.text = text
    }

    override fun getItemCount() = dataList.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.textView)
    }
}
