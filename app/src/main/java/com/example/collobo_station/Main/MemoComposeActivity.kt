package com.example.collobo_station.Main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.collobo_station.Data.Memo
import com.example.collobo_station.R
import com.google.common.reflect.TypeToken
import com.google.gson.Gson

class MemoComposeActivity : AppCompatActivity() {
    private lateinit var btnSave: Button
    private lateinit var etTitle: EditText
    private lateinit var etContent: EditText

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var gson: Gson

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memo_compose)

        btnSave = findViewById(R.id.btnSave)
        etTitle = findViewById(R.id.etTitle)
        etContent = findViewById(R.id.etContent)
        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)
        gson = Gson() // Gson 객체 초기화
        btnSave.setOnClickListener {
            val title = etTitle.text.toString()
            val content = etContent.text.toString()

            if (title.isNotEmpty() && content.isNotEmpty()) {
                val memo = Memo(System.currentTimeMillis(), title, content)

                // Memo 객체를 SharedPreferences에 저장
                saveMemo(memo)

                // Intent에 Memo 객체를 담아서 이전 액티비티로 전달
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
        val jsonMemo = gson.toJson(memo)

        val prefsEditor = sharedPreferences.edit()
        prefsEditor.putString(MEMO_KEY, jsonMemo)
        prefsEditor.apply()
    }

    companion object {
        private const val PREFS_FILENAME = "com.example.collobo_station.memo"
        private const val MEMO_KEY = "memo_list"
    }
}
