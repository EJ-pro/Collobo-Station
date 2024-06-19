package com.example.collobo_station.Adapter.Home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.collobo_station.Data.Memo
import com.example.collobo_station.R

class MemoAdapter(
    private val memoList: MutableList<Memo>,
    private val context: Context,
    private val deleteMemo: (Int) -> Unit // 삭제 함수
) : RecyclerView.Adapter<MemoAdapter.MemoViewHolder>() {

    inner class MemoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textTitle: TextView = view.findViewById(R.id.textTitle)
        val textContent: TextView = view.findViewById(R.id.textContent)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_memo, parent, false)
        return MemoViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemoViewHolder, position: Int) {
        val memo = memoList[position]
        holder.textTitle.text = memo.title
        holder.textContent.text = memo.content

        holder.btnDelete.setOnClickListener {
            deleteMemo(position)
        }
    }

    override fun getItemCount(): Int {
        return memoList.size
    }
}
