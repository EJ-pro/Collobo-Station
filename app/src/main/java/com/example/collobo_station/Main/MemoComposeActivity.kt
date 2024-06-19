package com.example.collobo_station.Main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.collobo_station.Data.Memo
import com.example.collobo_station.R

class MemoComposeActivity : AppCompatActivity() {
    private lateinit var btnSave: Button
    private lateinit var etTitle: EditText
    private lateinit var etContent: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memo_compose)

        btnSave = findViewById(R.id.btnSave)

        btnSave.setOnClickListener {
            // 입력된 제목과 내용 가져오기
            val title = etTitle.text.toString()
            val content = etContent.text.toString()

            // 제목이나 내용이 비어있는 경우, 저장하지 않고 액티비티 종료
            if (title.isEmpty() || content.isEmpty()) {
                setResult(Activity.RESULT_CANCELED)
                finish()
            } else {
                // 메모를 생성하여 이전 액티비티로 전달
                val memo = Memo(System.currentTimeMillis(), title, content)
                val resultIntent = Intent()
                resultIntent.putExtra("memo", memo)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }
    }
}
