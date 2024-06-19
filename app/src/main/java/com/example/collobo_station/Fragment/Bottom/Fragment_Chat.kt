package com.example.collobo_station.Fragment.Bottom

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.collobo_station.Adapter.Team.Project_ParticipationAdapter
import com.example.collobo_station.Adapter.Team.Project_ParticipationAdapter_Developer
import com.example.collobo_station.Global.Global_Team_Looking_Developer
import com.example.collobo_station.Login.LoginActivity
import com.example.collobo_station.R
import com.example.collobo_station.Global.Global_Team_Looking_Designer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Fragment_Chat  : Fragment() {
    private lateinit var menu : ImageView
    private lateinit var Designer_recyclerView: RecyclerView
    private lateinit var Developer_recyclerView: RecyclerView
    private lateinit var Designer_adapter: Project_ParticipationAdapter
    private lateinit var Developer_adapter: Project_ParticipationAdapter_Developer

    private fun sanitizeDocumentId(documentId: String): String {
        return documentId.replace(" ", "_").replace(",", "")
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        menu = view.findViewById(R.id.meunbar)  // Assuming you have an ImageView with this ID

        // Set an OnClickListener for the menu ImageView
        menu.setOnClickListener {
            showMenuDialog()
        }

        Designer_recyclerView = view.findViewById(R.id.Designer_recyclerview)
        Developer_recyclerView = view.findViewById(R.id.Developer_recyclerview)
        // Set LayoutManagers for RecyclerViews
        val Designer_layoutManager = GridLayoutManager(requireContext(), 1, GridLayoutManager.HORIZONTAL, false)
        Designer_recyclerView.layoutManager = Designer_layoutManager

        val Developer_layoutManager = GridLayoutManager(requireContext(), 1, GridLayoutManager.HORIZONTAL, false)
        Developer_recyclerView.layoutManager = Developer_layoutManager

        // Initialize Adapters
        Designer_adapter = Project_ParticipationAdapter()
        Developer_adapter = Project_ParticipationAdapter_Developer()

        // Set Adapters to RecyclerViews
        Designer_recyclerView.adapter = Designer_adapter
        Developer_recyclerView.adapter = Developer_adapter

        Designer_adapter.setItemClickListener { item ->
            val text1 = item.split(",")[0].trim() // 예시: "text1, text2, text3"에서 첫 번째 값 가져오기
            val intent = Intent(requireContext(), Global_Team_Looking_Designer::class.java)
            intent.putExtra("text1", text1)
            startActivity(intent)
        }


        Developer_adapter.setItemClickListener { item ->
            val intent = Intent(requireContext(), Global_Team_Looking_Developer::class.java)
            intent.putExtra("documentid", item)
            startActivity(intent)
        }

        // Fetch data from Firestore
        fetchDataFromFirestore_Designer()
        fetchDataFromFirestore_Developer()
        return view
    }
    // Firestore data fetching methods
    private fun fetchDataFromFirestore_Designer() {
        val db = FirebaseFirestore.getInstance()
        val collectionRef = db.collection("Global_Team_Looking")

        collectionRef.get()
            .addOnSuccessListener { documents ->
                val dataList = mutableListOf<String>()
                for (document in documents) {
                    val text1 = document.getString("text1") ?: ""
                    val text2 = document.getString("text2") ?: ""
                    val text3 = document.getString("text3") ?: ""
                    dataList.add("$text1, $text2, $text3")
                }
                Designer_adapter.setData(dataList)
            }
            .addOnFailureListener { exception ->
                // Handle failure
            }
    }

    private fun fetchDataFromFirestore_Developer() {
        val db = FirebaseFirestore.getInstance()
        val collectionRef = db.collection("Team_Looking_Developer")

        collectionRef.get()
            .addOnSuccessListener { documents ->
                val dataList = mutableListOf<String>()
                for (document in documents) {
                    val text1 = document.getString("text1") ?: ""
                    val text2 = document.getString("text2") ?: ""
                    val text3 = document.getString("text3") ?: ""
                    dataList.add("$text1, $text2, $text3")
                }
                Developer_adapter.setData(dataList)
            }
            .addOnFailureListener { exception ->
                // Handle failure
            }
    }
    private fun showMenuDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("메뉴")
        builder.setItems(R.array.menu_items) { dialog, which ->
            when (which) {
                0 -> {
                    FirebaseAuth.getInstance().signOut()

                    // SharedPreferences에서 자동 로그인 정보 삭제
                    val sharedPreferences = requireActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
                    // SharedPreferences 수정
                    with(sharedPreferences.edit()) {
                        putBoolean("isLoggedIn", false) // 로그인 상태를 false로 설정
                        remove("username") // username 키의 값을 제거
                        remove("password") // password 키의 값을 제거
                        apply() // 변경사항을 저장
                    }

                    // 로그인 화면으로 이동
                    val intent = Intent(activity, LoginActivity::class.java)
                    startActivity(intent)
                    activity?.finish() // 현재 화면 종료
                }
                1 -> Toast.makeText(requireContext(), "Action Two", Toast.LENGTH_SHORT).show()
            }
        }
        builder.show()
    }

}