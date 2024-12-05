package com.example.collobo_station.Fragment.Scrap

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.collobo_station.Adapter.Scrap.ScrapPagerAdapter
import com.example.collobo_station.Data.Model
import com.example.collobo_station.Login.LoginActivity
import com.example.collobo_station.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlin.math.abs
import kotlin.random.Random

class Fragment_Scrap : Fragment() {

    private lateinit var menu: ImageView
    private lateinit var viewPager: ViewPager2
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private var dataList: MutableList<Model> = mutableListOf()
    private lateinit var addButton: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_scrap, container, false)
        viewPager = view.findViewById(R.id.Scrap_viewPager)
        menu = view.findViewById(R.id.meunbar)
        addButton = view.findViewById(R.id.btn_add_contest)

        // Firestore 초기화
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        // 데이터 가져오기
        fetchScrapData()

        // 메뉴 버튼 클릭 이벤트
        menu.setOnClickListener {
            showMenuDialog()
        }

        addButton.setOnClickListener {
            showAddContestDialog() // + 버튼 클릭 시 Dialog 표시
        }
        return view
    }

    private fun showDeleteDialog(item: Model) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("삭제 확인")
        builder.setMessage("정말로 '${item.title}'을(를) 삭제하시겠습니까?")
        builder.setPositiveButton("삭제") { _, _ ->
            deleteScrapItem(item)
        }
        builder.setNegativeButton("취소", null)
        builder.show()
    }
    private fun deleteScrapItem(item: Model) {
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""
        val scrapCollection = firestore.collection("Scrap")

        Log.d("Scrap", "Current User Email: $currentUserEmail")
        Log.d("Scrap", "Deleting item with title: ${item.title}")

        // Firestore에서 nickname과 title이 일치하는 데이터 삭제
        scrapCollection
            .whereEqualTo("nickname", currentUserEmail)
            .whereEqualTo("title", item.title)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Toast.makeText(requireContext(), "삭제할 항목이 없습니다.", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                for (document in documents) {
                    scrapCollection.document(document.id)
                        .delete()
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "'${item.title}'이(가) 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                            // 로컬 리스트에서 항목 삭제 및 ViewPager 업데이트
                            dataList.remove(item)
                            setupViewPager(dataList)
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(requireContext(), "삭제에 실패했습니다: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "삭제 작업 중 오류 발생: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showAddContestDialog() {
        val contestCollection = firestore.collection("Contest")

        contestCollection.get()
            .addOnSuccessListener { documents ->
                val contestNames = mutableListOf<String>()

                for (document in documents) {
                    val title = document.getString("대회명") ?: "Untitled"
                    contestNames.add(title)
                }

                // Contest 목록 Dialog 표시
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("대회명 선택")
                builder.setItems(contestNames.toTypedArray()) { _, which ->
                    val selectedContest = contestNames[which]
                    addContestToScrap(selectedContest)
                }
                builder.show()

            }.addOnFailureListener { exception ->
                exception.printStackTrace()
                Toast.makeText(requireContext(), "대회 목록을 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addContestToScrap(selectedContest: String) {
        firestore.collection("Contest").whereEqualTo("대회명", selectedContest).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val title = document.getString("대회명") ?: "Untitled"
                    val imagePath = document.getString("이미지") ?: ""
                    val pageUrl = document.getString("홈페이지url") ?: ""
                    val randomColor = getRandomColor()

                    // 사용자 확인 다이얼로그
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle("대회 추가 확인")
                    builder.setMessage("대회를 추가하시겠습니까?\n\n대회명: $title")
                    builder.setPositiveButton("추가") { _, _ ->
                        // 중복 여부 확인
                        if (dataList.any { it.title == title }) {
                            Toast.makeText(requireContext(), "이미 추가된 대회입니다.", Toast.LENGTH_SHORT).show()
                            return@setPositiveButton
                        }

                        // Firestore에서 현재 최대 추가순서 값 가져오기
                        firestore.collection("Scrap")
                            .orderBy("추가순서", Query.Direction.DESCENDING)
                            .limit(1) // 가장 마지막 순서를 가져옴
                            .get()
                            .addOnSuccessListener { snapshot ->
                                val currentOrder = snapshot.documents.firstOrNull()?.getLong("추가순서") ?: 0L
                                val newOrder = currentOrder + 1

                                // Firebase Storage에서 이미지 다운로드 URL 생성
                                storage.reference.child(imagePath).downloadUrl
                                    .addOnSuccessListener { imageUrl ->
                                        val scrapData = hashMapOf(
                                            "title" to title,
                                            "imageUrl" to imagePath,
                                            "pageUrl" to pageUrl,
                                            "color" to randomColor,
                                            "nickname" to (FirebaseAuth.getInstance().currentUser?.email ?: ""),
                                            "추가순서" to newOrder // 새로운 추가순서 지정
                                        )

                                        firestore.collection("Scrap").add(scrapData)
                                            .addOnSuccessListener {
                                                dataList.add(Model(title, imageUrl.toString(), pageUrl, title, randomColor))
                                                setupViewPager(dataList)
                                                Toast.makeText(requireContext(), "대회가 추가되었습니다.", Toast.LENGTH_SHORT).show()
                                            }
                                            .addOnFailureListener { e ->
                                                Toast.makeText(requireContext(), "스크랩 추가에 실패했습니다: ${e.message}", Toast.LENGTH_SHORT).show()
                                            }
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(requireContext(), "이미지 URL을 가져오는 데 실패했습니다: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(requireContext(), "추가순서 가져오기에 실패했습니다: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                    builder.setNegativeButton("취소") { dialog, _ ->
                        dialog.dismiss()
                    }
                    builder.show()
                }
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
                Toast.makeText(requireContext(), "대회 데이터를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
    }



    private fun getUserNickname(onNicknameFetched: (String?) -> Unit) {
        val userEmail = FirebaseAuth.getInstance().currentUser?.email
        if (userEmail == null) {
            Toast.makeText(requireContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
            onNicknameFetched(null)
            return
        }

        firestore.collection("Users").document(userEmail).get()
            .addOnSuccessListener { document ->
                val nickname = document.getString("nickname")
                if (nickname != null) {
                    onNicknameFetched(nickname)
                } else {
                    Toast.makeText(requireContext(), "닉네임을 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                    onNicknameFetched(null)
                }
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
                Toast.makeText(requireContext(), "닉네임을 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                onNicknameFetched(null)
            }
    }


    private fun showMenuDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("메뉴")
        builder.setItems(R.array.menu_items) { _, which ->
            when (which) {
                0 -> {
                    FirebaseAuth.getInstance().signOut()

                    val sharedPreferences =
                        requireActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
                    with(sharedPreferences.edit()) {
                        putBoolean("isLoggedIn", false)
                        remove("username")
                        remove("password")
                        apply()
                    }

                    val intent = Intent(activity, LoginActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }
                1 -> Toast.makeText(requireContext(), "Action Two", Toast.LENGTH_SHORT).show()
            }
        }
        builder.show()
    }

    private fun fetchScrapData() {
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""
        val scrapCollection = firestore.collection("Scrap")

        scrapCollection
            .whereEqualTo("nickname", currentUserEmail)
            .orderBy("추가순서", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                dataList.clear() // 기존 데이터 초기화

                if (documents.isEmpty) {
                    setupViewPager(dataList) // 문서가 없는 경우에도 ViewPager 초기화
                    return@addOnSuccessListener
                }

                for (document in documents) {
                    val title = document.getString("title") ?: "Untitled"
                    val imagePath = document.getString("imageUrl") ?: ""
                    val pageUrl = document.getString("pageUrl") ?: ""
                    val color = document.getLong("color")?.toInt() ?: Color.WHITE

                    // Firebase Storage에서 다운로드 URL 생성
                    storage.reference.child(imagePath).downloadUrl
                        .addOnSuccessListener { imageUrl ->
                            dataList.add(Model(title, imageUrl.toString(), pageUrl, title, color))

                            // 데이터 로드 후 ViewPager 갱신
                            if (dataList.size == documents.size()) {
                                setupViewPager(dataList)
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("FirebaseStorage", "이미지 URL 생성 실패: ${e.message}")
                        }
                }

            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "데이터를 가져오는 데 실패했습니다: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("FirebaseStorage", "이미지 URL 생성 실패: ${e.message}")

            }
    }



    private fun setupViewPager(dataList: List<Model>) {
        val adapter = ScrapPagerAdapter(dataList) { item ->
            showDeleteDialog(item) // 삭제 다이얼로그 호출
        }
        viewPager.adapter = adapter
        viewPager.orientation = ViewPager2.ORIENTATION_VERTICAL
        viewPager.offscreenPageLimit = 3

        // 마지막 페이지로 이동
        if (dataList.isNotEmpty()) {
            val startingPage = dataList.size - 1
            viewPager.setCurrentItem(startingPage, false)
        }

        // 페이지 전환 애니메이션 설정 (VerticalTransformer 적용)
        val verticalTransformer = VerticalTransformer(viewPager.offscreenPageLimit)
        viewPager.setPageTransformer(verticalTransformer)
    }


    private fun getRandomColor(): Int {
        val r = (180..255).random()
        val g = (180..255).random()
        val b = (180..255).random()
        return Color.rgb(r, g, b)
    }

    private fun getUserId(): String? {
        val user = FirebaseAuth.getInstance().currentUser
        return user?.uid
    }

}
