package com.example.collobo_station.Main

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.collobo_station.Adapter.Home.MemoAdapter
import com.example.collobo_station.Data.Memo
import com.example.collobo_station.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Portfolio_management : AppCompatActivity() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val memoList = mutableListOf<Memo>()
    private lateinit var adapter: MemoAdapter

    // 뷰 변수 선언
    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAddMemo: FloatingActionButton

    companion object {
        const val REQUEST_CODE_ADD_MEMO = 1001
        const val REQUEST_CODE_EDIT_MEMO = 1002
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 여기서 레이아웃 파일명을 실제 존재하는 것으로 변경해주세요.
        setContentView(R.layout.action_protfolio_management)

        // findViewById로 뷰 참조
        recyclerView = findViewById(R.id.recyclerView)
        fabAddMemo = findViewById(R.id.fabAddMemo)

        adapter = MemoAdapter(memoList, this,
            deleteMemo = { position -> confirmDelete(position) },
            editMemo = { position -> showEditMemoDialog(position) }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        fabAddMemo.setOnClickListener {
            showAddMemoDialog()
        }

        loadMemosFromFirestore()
    }

    private fun loadMemosFromFirestore() {
        val userEmail = auth.currentUser?.email
        if (userEmail != null) {
            firestore.collection("Memo").document(userEmail)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val memoData = document.get("memo_list") as? List<Map<String, Any>> ?: emptyList()
                        memoList.clear()

                        // 역순으로 순회 (memoData.reversed())
                        for (data in memoData.reversed()) {
                            val id = data["id"] as? Number ?: 0L
                            val title = data["title"] as? String ?: ""
                            val content = data["content"] as? String ?: ""
                            memoList.add(Memo(id.toLong(), title, content))
                        }

                        adapter.notifyDataSetChanged()
                    } else {
                        memoList.clear()
                        adapter.notifyDataSetChanged()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "메모 불러오기 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "로그인 필요", Toast.LENGTH_SHORT).show()
        }
    }

    fun confirmDelete(position: Int) {
        deleteMemoFromFirestore(position)
    }

    private fun deleteMemoFromFirestore(position: Int) {
        val userEmail = auth.currentUser?.email ?: return
        firestore.collection("Memo").document(userEmail)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val memoData = document.get("memo_list") as? List<Map<String, Any>> ?: emptyList()
                    val mutableListData = memoData.toMutableList()
                    mutableListData.removeAt(position)

                    firestore.collection("Memo").document(userEmail)
                        .update("memo_list", mutableListData)
                        .addOnSuccessListener {
                            memoList.removeAt(position)
                            adapter.notifyItemRemoved(position)
                            Toast.makeText(this, "메모 삭제됨", Toast.LENGTH_SHORT).show()
                        }
                }
            }
    }

    private fun showAddMemoDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_memo, null)
        val editTitle = dialogView.findViewById<EditText>(R.id.addMemoTitle)
        val editContent = dialogView.findViewById<EditText>(R.id.addMemoContent)

        AlertDialog.Builder(this)
            .setTitle("메모 추가")
            .setView(dialogView)
            .setPositiveButton("추가") { _, _ ->
                val title = editTitle.text.toString().trim()
                val content = editContent.text.toString().trim()

                if (title.isNotEmpty() && content.isNotEmpty()) {
                    val memo = Memo(System.currentTimeMillis(), title, content)
                    addMemoToFirestore(memo)
                } else {
                    Toast.makeText(this, "제목과 내용을 모두 입력해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun addMemoToFirestore(memo: Memo) {
        val userEmail = auth.currentUser?.email
        if (userEmail == null) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val docRef = firestore.collection("Memo").document(userEmail)

        docRef.get().addOnSuccessListener { document ->
            val currentList = if (document.exists()) {
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
                .addOnSuccessListener {
                    memoList.add(memo)
                    adapter.notifyDataSetChanged()
                    Toast.makeText(this, "메모가 추가되었습니다.", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "메모 추가 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Firestore 접근 실패: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showEditMemoDialog(position: Int) {
        val memo = memoList[position]

        // 다이얼로그 뷰 인플레이션
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_memo, null)
        val editTitle = dialogView.findViewById<EditText>(R.id.editMemoTitle)
        val editContent = dialogView.findViewById<EditText>(R.id.editMemoContent)

        // 기존 메모 내용 설정
        editTitle.setText(memo.title)
        editContent.setText(memo.content)

        AlertDialog.Builder(this)
            .setTitle("메모 수정")
            .setView(dialogView)
            .setPositiveButton("저장") { _, _ ->
                val updatedTitle = editTitle.text.toString().trim()
                val updatedContent = editContent.text.toString().trim()

                if (updatedTitle.isNotEmpty() && updatedContent.isNotEmpty()) {
                    val updatedMemo = Memo(memo.id, updatedTitle, updatedContent)
                    updateMemoInFirestore(updatedMemo, position)
                } else {
                    Toast.makeText(this, "제목과 내용을 모두 입력해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ADD_MEMO && resultCode == RESULT_OK) {
            loadMemosFromFirestore()
        } else if (requestCode == REQUEST_CODE_EDIT_MEMO && resultCode == RESULT_OK) {
            val updatedMemo = data?.getParcelableExtra<Memo>("updated_memo") ?: return
            val position = data.getIntExtra("position", -1)
            if (position != -1) {
                updateMemoInFirestore(updatedMemo, position)
            }
        }
    }

    private fun updateMemoInFirestore(updatedMemo: Memo, position: Int) {
        val userEmail = auth.currentUser?.email ?: return
        firestore.collection("Memo").document(userEmail)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val memoData = document.get("memo_list") as? List<Map<String, Any>> ?: emptyList()
                    val mutableListData = memoData.toMutableList()

                    val newMemoMap = mapOf(
                        "id" to updatedMemo.id,
                        "title" to updatedMemo.title,
                        "content" to updatedMemo.content
                    )
                    mutableListData[position] = newMemoMap

                    firestore.collection("Memo").document(userEmail)
                        .update("memo_list", mutableListData)
                        .addOnSuccessListener {
                            memoList[position] = updatedMemo
                            adapter.notifyItemChanged(position)
                            Toast.makeText(this, "메모 수정됨", Toast.LENGTH_SHORT).show()
                        }
                }
            }
    }
}
