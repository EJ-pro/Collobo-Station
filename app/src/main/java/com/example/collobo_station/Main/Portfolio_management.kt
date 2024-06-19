package com.example.collobo_station.Main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.collobo_station.Adapter.Home.MemoAdapter
import com.example.collobo_station.Data.Memo
import com.example.collobo_station.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken

class Portfolio_management : AppCompatActivity() {
    private val memoList = mutableListOf<Memo>()
    private lateinit var memoAdapter: MemoAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAddMemo: FloatingActionButton

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var gson: Gson

    companion object {
        private const val REQUEST_ADD_MEMO = 1
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

        memoAdapter = MemoAdapter(memoList)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = memoAdapter

        convertOldData()  // 추가된 부분
        loadMemos()

        fabAddMemo.setOnClickListener {
            val intent = Intent(this, MemoComposeActivity::class.java)
            startActivityForResult(intent, REQUEST_ADD_MEMO)
        }
    }

    private fun convertOldData() {
        val memoJson = sharedPreferences.getString(MEMO_KEY, null) ?: return

        // 기존 데이터가 객체인지 배열인지 확인
        try {
            val memo = gson.fromJson(memoJson, Memo::class.java)
            if (memo != null) {
                // 객체 데이터를 배열로 변환
                val memoList = mutableListOf(memo)
                sharedPreferences.edit().putString(MEMO_KEY, gson.toJson(memoList)).apply()
            }
        } catch (e: JsonSyntaxException) {
            // 이미 배열 형식이면 변환 필요 없음
        }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ADD_MEMO && resultCode == Activity.RESULT_OK) {
            data?.getParcelableExtra<Memo>("memo")?.let {
                memoList.add(it)
                memoAdapter.notifyDataSetChanged()
            }
        }
    }
}
