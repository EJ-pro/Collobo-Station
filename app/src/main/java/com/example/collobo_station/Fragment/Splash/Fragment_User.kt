package com.example.collobo_station.Fragment.Splash

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.collobo_station.Login.LoginActivity
import com.example.collobo_station.databinding.FragmentMyPageBinding
import com.google.firebase.auth.FirebaseAuth

class Fragment_User: Fragment() {
    private var _binding: FragmentMyPageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyPageBinding.inflate(inflater, container, false)
        val view = binding.root

        // 로그아웃 버튼 클릭 리스너 설정
        binding.logoutBtn.setOnClickListener {
            // Firebase에서 로그아웃
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
            activity?.finish() // 현재 화면 종료
        }
        binding.push.setOnClickListener {

        }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}