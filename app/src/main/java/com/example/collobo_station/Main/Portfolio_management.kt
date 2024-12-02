package com.example.collobo_station.Main

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.collobo_station.Adapter.Home.MemoAdapter
import com.example.collobo_station.Data.Memo
import com.example.collobo_station.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Portfolio_management : AppCompatActivity() {
    private val memoList = mutableListOf<Memo>()
    private lateinit var memoAdapter: MemoAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAddMemo: FloatingActionButton

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var gson: Gson

    companion object {
        private const val PREFS_FILENAME = "com.example.collobo_station.memo"
        private const val MEMO_KEY = "memo_list"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.action_protfolio_management)

        recyclerView = findViewById(R.id.recyclerView)
        fabAddMemo = findViewById(R.id.fabAddMemo)

        sharedPreferences = getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)
        gson = Gson()

        memoAdapter = MemoAdapter(memoList, this, { position ->
            deleteMemo(position)
        }, { position ->
            editMemo(position)
        })

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = memoAdapter

        loadMemos()

        fabAddMemo.setOnClickListener {
            showAddMemoDialog()
        }
    }

    fun confirmDelete(position: Int) {
        val memo = memoList[position]

        val builder = AlertDialog.Builder(this)
        builder.setTitle("메모 삭제")
        builder.setMessage("정말 '${memo.title}' 메모를 삭제하시겠습니까?")
        builder.setPositiveButton("삭제") { _, _ ->
            deleteMemo(position) // 확인하면 삭제 수행
        }
        builder.setNegativeButton("취소") { dialog, _ ->
            dialog.dismiss() // 취소하면 다이얼로그 닫기
        }
        builder.show()
    }

    private fun showAddMemoDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_memo, null)
        val editTitle = dialogView.findViewById<EditText>(R.id.editMemoTitle)
        val editContent = dialogView.findViewById<EditText>(R.id.editMemoContent)

        AlertDialog.Builder(this)
            .setTitle("메모 작성")
            .setView(dialogView)
            .setPositiveButton("추가") { _, _ ->
                val title = editTitle.text.toString().trim()
                val content = editContent.text.toString().trim()

                if (title.isNotEmpty() && content.isNotEmpty()) {
                    val newMemo = Memo(System.currentTimeMillis(), title, content)
                    memoList.add(0, newMemo) // 리스트의 맨 앞에 추가
                    memoAdapter.notifyDataSetChanged() // 데이터 변경 알림
                    saveMemos()
                }
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun editMemo(position: Int) {
        val memo = memoList[position]

        val dialogView = layoutInflater.inflate(R.layout.dialog_add_memo, null)
        val editTitle = dialogView.findViewById<EditText>(R.id.editMemoTitle)
        val editContent = dialogView.findViewById<EditText>(R.id.editMemoContent)

        editTitle.setText(memo.title)
        editContent.setText(memo.content)

        AlertDialog.Builder(this)
            .setTitle("메모 수정")
            .setView(dialogView)
            .setPositiveButton("수정") { _, _ ->
                val newTitle = editTitle.text.toString()
                val newContent = editContent.text.toString()

                memo.title = newTitle
                memo.content = newContent
                memoList.removeAt(position)
                memoList.add(0, memo) // 수정된 메모를 맨 앞에 추가
                memoAdapter.notifyDataSetChanged() // 변경 사항 반영

                saveMemos()
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun deleteMemo(position: Int) {
        if (position >= 0 && position < memoList.size) {
            memoList.removeAt(position)
            memoAdapter.notifyItemRemoved(position)
            saveMemos()
        }
    }

    private fun saveMemos() {
        val jsonString = gson.toJson(memoList)
        sharedPreferences.edit().putString(MEMO_KEY, jsonString).apply()
    }

    private fun loadMemos() {
        val memoListJson = sharedPreferences.getString(MEMO_KEY, null)
        if (memoListJson != null) {
            val type = object : TypeToken<MutableList<Memo>>() {}.type
            val savedMemos: MutableList<Memo> = gson.fromJson(memoListJson, type)
            memoList.addAll(savedMemos)
            memoAdapter.notifyDataSetChanged()
        }
    }
}
