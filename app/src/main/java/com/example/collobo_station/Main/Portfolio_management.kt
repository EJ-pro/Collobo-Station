package com.example.collobo_station.Main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.collobo_station.Adapter.Home.MemoAdapter
import com.example.collobo_station.Data.Memo
import com.example.collobo_station.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class Portfolio_management : AppCompatActivity()  {
    private val memoList = mutableListOf<Memo>()
    private lateinit var memoAdapter: MemoAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAddMemo : FloatingActionButton
    companion object {
        private const val REQUEST_ADD_MEMO = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.action_protfolio_management)

        // XML에서 RecyclerView와 FloatingActionButton 초기화
        recyclerView = findViewById(R.id.recyclerView)
        fabAddMemo = findViewById(R.id.fabAddMemo)

        // MemoAdapter 초기화
        memoAdapter = MemoAdapter(memoList)

        // RecyclerView 설정
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = memoAdapter

        fabAddMemo.setOnClickListener {
            // MemoComposeActivity를 시작하여 새 메모 추가
            val intent = Intent(this, MemoComposeActivity::class.java)
            startActivityForResult(intent, REQUEST_ADD_MEMO)
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
