package com.example.collobo_station.Adapter.Login

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.collobo_station.Fragment.Login.Fragment_IDFind
import com.example.collobo_station.Fragment.Login.Fragment_PWFind

class IDPWFindAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return 2 // 아이디 찾기와 비밀번호 찾기 두 개의 Fragment
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> Fragment_IDFind()
            1 -> Fragment_PWFind()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}
