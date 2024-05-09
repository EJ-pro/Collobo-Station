package com.example.collobo_station.Fragment.Bottom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.collobo_station.Adapter.Scrap.ScrapPagerAdapter
import com.example.collobo_station.Data.Model
import com.example.collobo_station.Fragment.Scrap.VerticalTransformer
import com.example.collobo_station.R

class Fragment_Chat  : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        return view
    }
}