package com.example.collobo_station.Fragment.Splash

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
import com.example.collobo_station.Login.LoginActivity
import com.example.collobo_station.R
import com.example.collobo_station.databinding.FragmentMyPageBinding
import com.google.firebase.auth.FirebaseAuth

class Fragment_User : Fragment() {

    private var _binding: FragmentMyPageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyPageBinding.inflate(inflater, container, false)
        val view = binding.root

        // 상단 메뉴 아이콘 설정
        val menu: ImageView = view.findViewById(R.id.meunbar)
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
                    // 로그아웃 처리
                    FirebaseAuth.getInstance().signOut()

                    // SharedPreferences에서 자동 로그인 정보 삭제
                    val sharedPreferences = requireActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
                    with(sharedPreferences.edit()) {
                        putBoolean("isLoggedIn", false)
                        remove("username")
                        remove("password")
                        apply()
                    }

                    // 로그인 화면으로 이동
                    val intent = Intent(activity, LoginActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }
                1 -> Toast.makeText(requireContext(), "다른 메뉴 클릭", Toast.LENGTH_SHORT).show()
            }
        }
        builder.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
