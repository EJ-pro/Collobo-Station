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

class Portfolio_management : AppCompatActivity()  {
    private val memoList = mutableListOf<Memo>()
    private lateinit var memoAdapter: MemoAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAddMemo : FloatingActionButton

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

        // XML에서 RecyclerView와 FloatingActionButton 초기화
        recyclerView = findViewById(R.id.recyclerView)
        fabAddMemo = findViewById(R.id.fabAddMemo)
        gson = Gson() // Gson 객체 초기화
        // MemoAdapter 초기화
        memoAdapter = MemoAdapter(memoList)

        // RecyclerView 설정
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = memoAdapter

        sharedPreferences = getSharedPreferences(Companion.PREFS_FILENAME, Context.MODE_PRIVATE)

        // 이전에 저장된 Memo 데이터 불러오기
        loadMemo()

        fabAddMemo.setOnClickListener {
            // MemoComposeActivity를 시작하여 새 메모 추가
            val intent = Intent(this, MemoComposeActivity::class.java)
            startActivityForResult(intent, REQUEST_ADD_MEMO)
        }
    }
    private fun loadMemo() {
        val gson = Gson()
        val jsonMemo = sharedPreferences.getString(Companion.MEMO_KEY, null)
        if (jsonMemo != null) {
            val memo = gson.fromJson(jsonMemo, Memo::class.java)
            memoList.add(memo)
            memoAdapter.notifyDataSetChanged()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_ADD_MEMO && resultCode == Activity.RESULT_OK) {
            // MemoComposeActivity에서 전달받은 Memo 객체를 리스트에 추가
            val memo = data?.getParcelableExtra<Memo>(/* name = */ "memo")
            if (memo != null) {
                memoList.add(memo)
                memoAdapter.notifyDataSetChanged()
            }
        }
    }
}
