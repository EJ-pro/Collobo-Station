package com.example.collobo_station.Global

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.collobo_station.Adapter.Global.Global_DesignerAdapter
import com.example.collobo_station.Main.MainActivity
import com.example.collobo_station.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

class Global_Team_Looking_Designer : AppCompatActivity() {

    private lateinit var fieldTextView: TextView
    private lateinit var contentTextView: TextView
    private lateinit var backbtn: ImageView
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: Global_DesignerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.global_designer_team_matching)

        fieldTextView = findViewById(R.id.field)
        contentTextView = findViewById(R.id.content)
        backbtn = findViewById(R.id.backbtn)
        fabAdd = findViewById(R.id.fabAdd)
        recyclerView = findViewById(R.id.recyclerView)

        adapter = Global_DesignerAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        // Intent로 전달받은 문서 ID 확인

        val text1 = intent.getStringExtra("text1") ?: ""
        Log.d("Text1", "Text1: $text1")

        // Firestore 인스턴스 초기화
        val db = FirebaseFirestore.getInstance()

        // Firestore에서 데이터 가져오기
        val docRef = db.collection("Global_Team_Looking").document(text1)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val text1 = document.getString("text1") ?: ""
                    val text3 = document.getString("text3") ?: ""

                    fieldTextView.text = text1
                    contentTextView.text = text3
                } else {
                    Log.d("TAG", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }

        fabAdd.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        backbtn.setOnClickListener {
            finish()
        }
    }
}
