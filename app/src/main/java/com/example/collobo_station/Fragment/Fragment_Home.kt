package com.example.collobo_station.Fragment

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
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
import androidx.viewpager.widget.ViewPager
import com.example.collobo_station.Adapter.TabAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class Fragment_Home : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: ViewPager2Adapter
    private lateinit var layoutOnBoardingIndicators: LinearLayout
    private lateinit var textViewContestName: Button
    private lateinit var textViewUserName:TextView
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPagerTabs: ViewPager2
    private lateinit var tabadapter: ViewPager2Adapter

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
        textViewUserName = view.findViewById(R.id.textViewUserName)

        val user = Firebase.auth.currentUser
        if (user != null) {
            val userEmail = user.email
            if (userEmail != null) {
                val db = Firebase.firestore
                // Users 컬렉션에서 현재 사용자의 이메일과 일치하는 문서를 찾습니다.
                db.collection("Users")
                    .whereEqualTo("email", userEmail)
                    .get()
                    .addOnSuccessListener { documents ->
                        if (!documents.isEmpty) {
                            val document = documents.documents[0]
                            val nickname = document.getString("nickname")
                            if (nickname != null) {
                                // 닉네임이 존재하는 경우에만 표시합니다.
                                textViewUserName.text = nickname
                                Toast.makeText(requireContext(), "안녕하세요, $nickname 님!", Toast.LENGTH_SHORT).show()
                            } else {
                                // 닉네임이 없는 경우 "사용자"로 표시합니다.
                                textViewUserName.text = "사용자"
                            }
                        } else {
                            // 일치하는 문서가 없는 경우 "사용자"로 표시합니다.
                            textViewUserName.text = "사용자"
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error getting document", e)
                        // 오류 발생 시 "로그인 필요"로 표시합니다.
                        textViewUserName.text = "로그인 필요"
                    }
            } else {
                // 사용자 이메일이 없는 경우 "로그인 필요"로 표시합니다.
                textViewUserName.text = "로그인 필요"
            }
        } else {
            // 사용자가 로그인하지 않은 경우 "로그인 필요"로 표시합니다.
            textViewUserName.text = "로그인 필요"
        }

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

        tabLayout = view.findViewById(R.id.tabLayout)
        viewPagerTabs = view.findViewById(R.id.view_pager)

        var tabAdapter = TabAdapter(childFragmentManager, lifecycle)

        tabAdapter.addFragment(Fragment_Tab_All(), "전체보기")
        tabAdapter.addFragment(Fragment_Tab_Recent(), "최근등록순")
        tabAdapter.addFragment(Fragment_Tab_DeadLine(),"마감순")

        viewPagerTabs.adapter = tabAdapter

        TabLayoutMediator(tabLayout, viewPagerTabs) { tab, position ->
            tab.text = tabAdapter.getPageTitle(position)
        }.attach()

        viewPagerTabs.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val fragment = getChildFragmentManager().fragments[position]
                if (fragment is Fragment_Tab_All) {
                    fragment.adjustRecyclerViewSize()
                } else if (fragment is Fragment_Tab_Recent) {
                    fragment.adjustRecyclerViewSize()
                } else if (fragment is Fragment_Tab_DeadLine) {
                    fragment.adjustRecyclerViewSize()
                }
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
                        val url = it.getString("url") ?:""

                        textViewContestName.setOnClickListener {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            startActivity(intent)
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                // 처리
            }
    }

}