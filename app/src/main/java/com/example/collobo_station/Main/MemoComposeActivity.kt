package com.example.collobo_station.Main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.collobo_station.Data.Memo
import com.example.collobo_station.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MemoComposeActivity : AppCompatActivity() {
    private lateinit var btnSave: Button
    private lateinit var etTitle: EditText
    private lateinit var etContent: EditText

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var gson: Gson

    companion object {
        private const val PREFS_FILENAME = "com.example.collobo_station.memo"
        private const val MEMO_KEY = "memo_list"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memo_compose)

        btnSave = findViewById(R.id.btnSave)
        etTitle = findViewById(R.id.etTitle)
        etContent = findViewById(R.id.etContent)

        sharedPreferences = getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)
        gson = Gson()

        btnSave.setOnClickListener {
            val title = etTitle.text.toString()
            val content = etContent.text.toString()

            if (title.isNotEmpty() && content.isNotEmpty()) {
                val memo = Memo(System.currentTimeMillis(), title, content)
                saveMemo(memo)

                val resultIntent = Intent()
                resultIntent.putExtra("memo", memo)
                setResult(Activity.RESULT_OK, resultIntent)
            } else {
                setResult(Activity.RESULT_CANCELED)
            }
            finish()
        }
    }

    private fun saveMemo(memo: Memo) {
        val memoListJson = sharedPreferences.getString(MEMO_KEY, null)
        val memoList: MutableList<Memo> = if (memoListJson != null) {
            val type = object : TypeToken<MutableList<Memo>>() {}.type
            gson.fromJson(memoListJson, type)
        } else {
            mutableListOf()
        }

        memoList.add(memo)
        val jsonString = gson.toJson(memoList)
        sharedPreferences.edit().putString(MEMO_KEY, jsonString).apply()
    }
}
