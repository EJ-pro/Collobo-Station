package com.example.collobo_station.Adapter.Scrap

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.collobo_station.Adapter.ViewHolder
import com.example.collobo_station.Data.Model
import com.example.collobo_station.databinding.ItemTextBinding

class ScrapPagerAdapter(private val itemList: ArrayList<Model>) : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemTextBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

}