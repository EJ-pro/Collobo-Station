package com.example.collobo_station.Adapter.Home
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.collobo_station.Data.Memo
import com.example.collobo_station.R

class MemoAdapter(private val memos: List<Memo>) :
    RecyclerView.Adapter<MemoAdapter.MemoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_memo, parent, false)
        return MemoViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemoViewHolder, position: Int) {
        val memo = memos[position]
        holder.bind(memo)
    }

    override fun getItemCount(): Int {
        return memos.size
    }

    class MemoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.textTitle)
        private val contentTextView: TextView = itemView.findViewById(R.id.textContent)

        fun bind(memo: Memo) {
            titleTextView.text = memo.title
            contentTextView.text = memo.content
        }
    }
}