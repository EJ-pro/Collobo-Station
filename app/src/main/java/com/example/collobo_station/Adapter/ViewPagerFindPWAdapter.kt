package com.example.collobo_station.Adapter
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.fragment.app.Fragment

class ViewPagerFindPWAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    private val fragments: MutableList<Fragment> = ArrayList()

    // Fragment 추가 메서드
    fun addFragment(fragment: Fragment) {
        fragments.add(fragment)
    }

    // Fragment 가져오는 메서드
    override fun getItemCount(): Int {
        return fragments.size
    }

    // 해당 위치(position)의 Fragment 반환
    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}
