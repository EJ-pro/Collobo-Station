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
import kotlin.math.abs
import kotlin.random.Random

class Fragment_Scrap : Fragment() {

    private lateinit var menu : ImageView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_scrap, container, false)
        val viewPager: ViewPager2 = view.findViewById(R.id.Scrap_viewPager)

        // 어댑터 생성 및 설정
        val adapter = ScrapPagerAdapter(getDummyData()) // getDummyData()는 가상의 데이터를 반환하는 메서드입니다.
        viewPager.adapter = adapter

        viewPager.orientation = ViewPager2.ORIENTATION_VERTICAL
        viewPager.offscreenPageLimit = 4

        val startingPage = 9 // 시작 페이지의 인덱스 (0부터 시작)
        viewPager.setCurrentItem(startingPage, false) // 시작 페이지로 이동

        // VerticalTransformer 적용
        val verticalTransformer = VerticalTransformer(viewPager.offscreenPageLimit)
        viewPager.setPageTransformer(verticalTransformer)

        menu = view.findViewById(R.id.meunbar)  // Assuming you have an ImageView with this ID

        // Set an OnClickListener for the menu ImageView
        menu.setOnClickListener {
            showMenuDialog()
        }

        return view
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
    // 가상의 데이터를 반환하는 메서드
    private fun getDummyData(): List<Model> {
        val dataList = mutableListOf<Model>()
        for (i in 1..10) {
            dataList.add(Model("Item $i", getRandomColor()))
        }
        return dataList
    }

    private fun getRandomColor(): Int {
        val r = Random.nextInt(256)
        val g = Random.nextInt(256)
        val b = Random.nextInt(256)
        return Color.rgb(r, g, b)
    }
}
