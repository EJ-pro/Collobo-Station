package com.example.collobo_station.Fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.collobo_station.Adapter.ViewPager2Adater
import com.example.collobo_station.R

class Fragment_Home : Fragment() {

    var layoutOnBoardingIndicators: LinearLayout? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // ViewPager2 관련 코드 이동
        val list = ArrayList<Int>()
        list.add(Color.parseColor("#ffff00"))
        list.add(Color.parseColor("#bdbdbd"))
        list.add(Color.parseColor("#0f9231"))
        val adapter = ViewPager2Adater(list, requireContext())

        val viewPager = view.findViewById<ViewPager2>(R.id.viewpager2)
        viewPager.offscreenPageLimit = 3
        viewPager.getChildAt(0).overScrollMode = View.OVER_SCROLL_NEVER
        viewPager.adapter = adapter

        layoutOnBoardingIndicators = view.findViewById(R.id.indicators)
        setupOnBoardingIndicators()
        setCurrentOnboardingIndicator(0)

        val transform = CompositePageTransformer()
        transform.addTransformer(MarginPageTransformer(8))

        transform.addTransformer(ViewPager2.PageTransformer { view: View, fl: Float ->
            val v = 1 - Math.abs(fl)
            view.scaleY = 0.8f + v * 0.2f
        })

        viewPager.setPageTransformer(transform)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                setCurrentOnboardingIndicator(position)
            }
        })

        return view
    }

    private fun setupOnBoardingIndicators() {
        val indicators = arrayOfNulls<ImageView>(3)

        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )

        layoutParams.setMargins(8, 0, 8, 0)

        for (i in indicators.indices) {
            indicators[i] = ImageView(requireContext())
            indicators[i]?.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.viewpager2_background_color
                )
            )

            indicators[i]?.layoutParams = layoutParams

            layoutOnBoardingIndicators?.addView(indicators[i])
        }
    }

    private fun setCurrentOnboardingIndicator(index: Int) {
        val childCount = layoutOnBoardingIndicators?.childCount
        for (i in 0 until childCount!!) {
            val imageView = layoutOnBoardingIndicators?.getChildAt(i) as ImageView
            if (i == index) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.viewpager2_background_color
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.viewpager2_background_color
                    )
                )
            }
        }
    }
}
