package com.example.collobo_station.Fragment.Scrap

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.collobo_station.Adapter.Scrap.ScrapPagerAdapter
import com.example.collobo_station.Data.Model
import com.example.collobo_station.R
import kotlin.math.abs
import kotlin.random.Random

class Fragment_Scrap : Fragment() {
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
        return view
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
