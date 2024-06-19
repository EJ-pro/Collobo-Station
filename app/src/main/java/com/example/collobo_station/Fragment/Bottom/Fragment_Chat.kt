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
import androidx.viewpager2.widget.ViewPager2
import com.example.collobo_station.Adapter.Scrap.ScrapPagerAdapter
import com.example.collobo_station.Data.Model
import com.example.collobo_station.Fragment.Scrap.VerticalTransformer
import com.example.collobo_station.Login.LoginActivity
import com.example.collobo_station.R
import com.google.firebase.auth.FirebaseAuth

class Fragment_Chat  : Fragment() {
    private lateinit var menu : ImageView
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
}