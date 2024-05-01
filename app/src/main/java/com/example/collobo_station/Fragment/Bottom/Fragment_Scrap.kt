package com.example.collobo_station.Fragment.Bottom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.PageTransformer
import com.example.collobo_station.Adapter.ScrapPagerAdapter
import com.example.collobo_station.R

class Fragment_Scrap : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_scrap, container, false)

        // ViewPager2 초기화
        val viewPager: ViewPager2 = view.findViewById(R.id.scrap_viewpager2)
        return view
    }
}
