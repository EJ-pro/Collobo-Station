package com.example.collobo_station.Fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.collobo_station.Adapter.ViewPager2Adapter
import com.example.collobo_station.R
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.Toast

class Fragment_Home : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: ViewPager2Adapter
    private lateinit var layoutOnBoardingIndicators: LinearLayout
    private lateinit var textViewContestName: TextView
    private lateinit var textViewField: TextView
    private lateinit var textViewTargetAudience: TextView
    private lateinit var textViewReceptionPeriod: TextView
    private lateinit var textViewTotalPrize: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        viewPager = view.findViewById(R.id.viewpager2)
        layoutOnBoardingIndicators = view.findViewById(R.id.indicators)
        textViewContestName = view.findViewById(R.id.textViewContestName)
        textViewField = view.findViewById(R.id.textViewField)
        textViewTargetAudience = view.findViewById(R.id.textViewTargetAudience)
        textViewReceptionPeriod = view.findViewById(R.id.textViewReceptionPeriod)
        textViewTotalPrize = view.findViewById(R.id.textViewTotalPrize)

        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("Recommendation Contest")
            .get()
            .addOnSuccessListener { documents ->
                val imageUrls = ArrayList<String>()
                for (document in documents) {
                    val imageUrl = document.getString("이미지")
                    imageUrl?.let {
                        imageUrls.add(it)
                    }
                }
                adapter = ViewPager2Adapter(imageUrls, requireContext())
                viewPager.adapter = adapter
                setupOnBoardingIndicators()
                setCurrentOnboardingIndicator(1)
            }
            .addOnFailureListener { exception ->
                // 처리
            }

        val transform = CompositePageTransformer()
        val pageMargin = resources.getDimensionPixelOffset(R.dimen.pageMargin)
        transform.addTransformer(MarginPageTransformer(pageMargin))
        transform.addTransformer(ViewPager2.PageTransformer { view: View, fl: Float ->
            val v = 1 - Math.abs(fl)
            view.scaleY = 0.8f + v * 0.2f
        })
        viewPager.offscreenPageLimit = 1
        viewPager.setPageTransformer(transform)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                fetchContestInfo(position)
                setCurrentOnboardingIndicator(position)
            }
        })

        return view
    }

    private fun setupOnBoardingIndicators() {
        val indicators = arrayOfNulls<ImageView>(adapter.itemCount)
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
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
            layoutOnBoardingIndicators.addView(indicators[i])
        }
    }

    private fun setCurrentOnboardingIndicator(index: Int) {
        for (i in 0 until layoutOnBoardingIndicators.childCount) {
            val imageView = layoutOnBoardingIndicators.getChildAt(i) as ImageView
            imageView.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    if (i == index) R.drawable.viewpager2_background_color else R.drawable.viewpager2_background_color
                )
            )
        }
    }

    private fun fetchContestInfo(position: Int) {
        val currentPosition = viewPager.currentItem
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("Recommendation Contest")
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty && currentPosition < documents.size()) {
                    val document = documents.elementAtOrNull(currentPosition)
                    document?.let {
                        val contestName = it.getString("대회명") ?: ""
                        val field = it.getString("분야") ?: ""
                        val targetAudience = it.getString("응모대상") ?: ""
                        val receptionPeriod = it.getString("접수기간") ?: ""
                        val totalPrize = it.getString("총상금") ?: ""

                        textViewContestName.text = contestName
                        textViewField.text = field
                        textViewTargetAudience.text = targetAudience
                        textViewReceptionPeriod.text = receptionPeriod
                        textViewTotalPrize.text = totalPrize
                    }
                }
            }
            .addOnFailureListener { exception ->
                // 처리
            }
    }

}
