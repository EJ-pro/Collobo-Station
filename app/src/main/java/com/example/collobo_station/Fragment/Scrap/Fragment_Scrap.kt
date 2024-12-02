package com.example.collobo_station.Fragment.Scrap

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import kotlin.math.abs
import kotlin.random.Random

class Fragment_Scrap : Fragment() {

    private lateinit var menu: ImageView
    private lateinit var viewPager: ViewPager2
    private lateinit var firestore: FirebaseFirestore
    private var dataList: MutableList<Model> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_scrap, container, false)
        viewPager = view.findViewById(R.id.Scrap_viewPager)
        menu = view.findViewById(R.id.meunbar)

        // Firestore 초기화
        firestore = FirebaseFirestore.getInstance()

        // 데이터 가져오기
        fetchContestData()

        // 메뉴 버튼 클릭 이벤트
        menu.setOnClickListener {
            showMenuDialog()
        }

        return view
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

    private fun fetchContestData() {
        val contestCollection = firestore.collection("Contest")

        contestCollection.get()
            .addOnSuccessListener { documents ->
                val dataList = mutableListOf<Model>()

                for (document in documents) {
                    val title = document.getString("대회명") ?: "Untitled"
                    val imageUrl = document.getString("이미지") ?: ""
                    val pageUrl = document.getString("홈페이지url") ?: ""
                    val randomColor = getRandomColor()
                    dataList.add(Model(title, imageUrl, pageUrl, title, randomColor))
                }

                if (dataList.isNotEmpty()) {
                    setupViewPager(dataList)
                } else {
                    Toast.makeText(requireContext(), "데이터가 없습니다.", Toast.LENGTH_SHORT).show()
                }

                viewPager.orientation = ViewPager2.ORIENTATION_VERTICAL
                viewPager.offscreenPageLimit = 4


                val startingPage = dataList.size - 1 // 시작 페이지의 인덱스 (0부터 시작)
                viewPager.setCurrentItem(startingPage, false) // 시작 페이지로 이동

                // VerticalTransformer 적용
                val verticalTransformer = VerticalTransformer(viewPager.offscreenPageLimit)
                viewPager.setPageTransformer(verticalTransformer)
            }

            .addOnFailureListener { exception ->
                exception.printStackTrace()
                Toast.makeText(requireContext(), "데이터를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupViewPager(dataList: List<Model>) {
        val adapter = ScrapPagerAdapter(dataList)
        viewPager.adapter = adapter
        viewPager.orientation = ViewPager2.ORIENTATION_VERTICAL
        viewPager.offscreenPageLimit = 4

        // 마지막 페이지로 이동
        val startingPage = dataList.size - 1
        viewPager.setCurrentItem(startingPage, false)

        // 페이지 전환 애니메이션 설정
        viewPager.setPageTransformer { page, position ->
            val scaleFactor = 0.85f.coerceAtLeast(1 - abs(position))
            page.scaleY = scaleFactor
            page.alpha = scaleFactor
        }
    }

    private fun getRandomColor(): Int {
        val r = Random.nextInt(256)
        val g = Random.nextInt(256)
        val b = Random.nextInt(256)
        return Color.rgb(r, g, b)
    }
}
