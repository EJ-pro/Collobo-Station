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
        viewPager.setPageTransformer { page, position ->
            val verticalMarginPx = resources.getDimensionPixelOffset(R.dimen.page_vertical_margin)
            val offsetPx = resources.getDimensionPixelOffset(R.dimen.page_offset)

            val offset = position * -(2 * offsetPx + verticalMarginPx)
            page.translationY = offset

            // 페이지의 크기를 조정하여 겹치는 효과를 줍니다.
            val scaleFactor = 1 - (0.1f * abs(position))
            page.scaleY = scaleFactor
        }


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
