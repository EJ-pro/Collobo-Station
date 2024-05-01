package com.example.collobo_station.Fragment.Bottom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.collobo_station.Adapter.ScrapPagerAdapter
import com.example.collobo_station.Data.Model
import com.example.collobo_station.Fragment.Scrap.SliderTransformer
import com.example.collobo_station.R

class Fragment_Chat  : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        // ViewPager2 초기화
        val viewPager: ViewPager2 = view.findViewById(R.id.viewPager)

        // 데이터 준비
        val itemList = ArrayList<Model>()
        for (i in 0..7) {
            itemList.add(Model("Text $i"))
        }

        // Adapter 설정
        val adapter = ScrapPagerAdapter(itemList)
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 3
        viewPager.setPageTransformer(SliderTransformer(3))
        return view
    }
}