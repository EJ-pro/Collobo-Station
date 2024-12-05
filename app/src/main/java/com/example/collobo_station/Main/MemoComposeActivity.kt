package com.example.collobo_station.Main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.collobo_station.Data.Memo
import com.example.collobo_station.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MemoComposeActivity : AppCompatActivity() {
    private lateinit var btnSave: Button
    private lateinit var etTitle: EditText
    private lateinit var etContent: EditText

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memo_compose)

        btnSave = findViewById(R.id.btnSave)
        etTitle = findViewById(R.id.etTitle)
        etContent = findViewById(R.id.etContent)

        btnSave.setOnClickListener {
            val title = etTitle.text.toString()
            val content = etContent.text.toString()

            if (title.isNotEmpty() && content.isNotEmpty()) {
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    val userEmail = currentUser.email
                    if (userEmail != null) {
                        val memo = Memo(System.currentTimeMillis(), title, content)
                        saveMemoToFirestore(userEmail, memo) {
                            val resultIntent = Intent()
                            resultIntent.putExtra("memo", memo)
                            setResult(Activity.RESULT_OK, resultIntent)
                            finish()
                        }
                    } else {
                        setResult(Activity.RESULT_CANCELED)
                        finish()
                    }
                } else {
                    // 로그인 안된 경우
                    setResult(Activity.RESULT_CANCELED)
                    finish()
                }
            } else {
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
        }
    }

    private fun saveMemoToFirestore(userEmail: String, memo: Memo, onSuccess: () -> Unit) {
        val docRef = firestore.collection("Memo").document(userEmail)

        docRef.get().addOnSuccessListener { document ->
            val currentList = if (document.exists()) {
                // 기존 문서 존재 시 memo_list 필드를 가져옴
                document.get("memo_list") as? List<Map<String, Any>> ?: emptyList()
            } else {
                emptyList()
            }

            val updatedList = currentList.toMutableList()
            val newMemoMap = mapOf(
                "id" to memo.id,
                "title" to memo.title,
                "content" to memo.content
            )
            updatedList.add(newMemoMap)

            docRef.set(mapOf("memo_list" to updatedList))
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { e ->
                    // 에러 처리 필요시
                }
        }.addOnFailureListener { e ->
            // 에러 처리
        }
    }
}
