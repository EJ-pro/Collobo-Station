package com.example.collobo_station.Login

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.collobo_station.Adapter.IDPWFindAdapter
import com.example.collobo_station.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ID_PW_Find : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_id_pw)

        val viewPager: ViewPager2 = findViewById(R.id.id_pw_find_viewpage)
        val tabLayout: TabLayout = findViewById(R.id.tabLayout)

        val adapter = IDPWFindAdapter(supportFragmentManager, lifecycle)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "아이디 찾기"
                1 -> tab.text = "비밀번호 찾기"
            }
        }.attach()
        val margin = resources.getDimensionPixelSize(R.dimen.tab_layout_margin)
        // TabLayout에 각 탭의 마진 설정
        for (i in 0 until tabLayout.tabCount) {
            val tab = (tabLayout.getChildAt(0) as ViewGroup).getChildAt(i)
            val params = tab.layoutParams as ViewGroup.MarginLayoutParams
            params.marginEnd = margin
            params.marginStart = margin
            tab.layoutParams = params
        }
    }
}
