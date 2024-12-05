package com.example.collobo_station.Fragment.Home

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.collobo_station.Adapter.Home.ViewPager2Adapter
import com.example.collobo_station.Adapter.Tab.TabAdapter
import com.example.collobo_station.Login.LoginActivity
import com.example.collobo_station.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Fragment_Home : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: ViewPager2Adapter
    private lateinit var layoutOnBoardingIndicators: LinearLayout
    private lateinit var textViewContestName: Button
    private lateinit var textViewUserName: TextView
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPagerTabs: ViewPager2
    private lateinit var menu: ImageView
    private lateinit var mContext: Context
    private lateinit var imagePickerLauncher: androidx.activity.result.ActivityResultLauncher<Intent>

    private val firestore = FirebaseFirestore.getInstance()
    private var selectedImageUri: Uri? = null

    private val imageUrls = ArrayList<String>()
    private val homepageUrls = ArrayList<String>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        imagePickerLauncher = registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                selectedImageUri = data?.data
                if (selectedImageUri == null) {
                    Toast.makeText(requireContext(), "이미지를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        initView(view)
        fetchUserInfo()
        loadRecommendationContests()
        setupTabLayout(view)
        return view
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(intent)
    }

    private fun initView(view: View) {
        viewPager = view.findViewById(R.id.viewpager2)
        layoutOnBoardingIndicators = view.findViewById(R.id.indicators)
        textViewContestName = view.findViewById(R.id.textViewContestName)
        textViewUserName = view.findViewById(R.id.textViewUserName)
        menu = view.findViewById(R.id.meunbar)

        menu.setOnClickListener {
            showMenuDialog()
        }
    }

    private fun showMenuDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("메뉴")
            .setItems(R.array.menu_items) { _, which ->
                when (which) {
                    0 -> {
                        FirebaseAuth.getInstance().signOut()
                        val sharedPreferences = requireActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
                        with(sharedPreferences.edit()) {
                            putBoolean("isLoggedIn", false)
                            remove("username")
                            remove("password")
                            apply()
                        }
                        val intent = Intent(activity, LoginActivity::class.java)
                        startActivity(intent)
                        activity?.finish()
                    }
                    1 -> Toast.makeText(requireContext(), "Action Two", Toast.LENGTH_SHORT).show()
                }
            }.show()
    }

    private fun fetchUserInfo() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            textViewUserName.text = "로그인 필요"
            return
        }

        firestore.collection("Users").whereEqualTo("email", user.email)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.documents[0]
                    val nickname = document.getString("nickname") ?: "default_user"
                    textViewUserName.text = nickname
                } else {
                    textViewUserName.text = "사용자"
                }
            }.addOnFailureListener {
                textViewUserName.text = "로그인 필요"
            }
    }

    private fun loadRecommendationContests() {
        firestore.collection("Recommendation Contest")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val imageUrl = document.getString("이미지")
                    val homeUrl = document.getString("홈페이지url")
                    if (!imageUrl.isNullOrEmpty()) imageUrls.add(imageUrl)
                    if (!homeUrl.isNullOrEmpty()) homepageUrls.add(homeUrl) else homepageUrls.add("")
                }
                setupViewPager()
            }
    }

    private fun setupViewPager() {
        adapter = ViewPager2Adapter(imageUrls, requireContext())
        viewPager.adapter = adapter
        setupOnBoardingIndicators()
        setCurrentOnboardingIndicator(0)

        val transform = CompositePageTransformer()
        val pageMargin = resources.getDimensionPixelOffset(R.dimen.pageMargin)
        transform.addTransformer(MarginPageTransformer(pageMargin))
        transform.addTransformer { view, fl ->
            val v = 1 - kotlin.math.abs(fl)
            view.scaleY = 0.8f + v * 0.2f
        }

        viewPager.offscreenPageLimit = 1
        viewPager.setPageTransformer(transform)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                setCurrentOnboardingIndicator(position)
                setContestLink(position)
            }
        })

        setContestLink(0)
    }

    private fun setupOnBoardingIndicators() {
        layoutOnBoardingIndicators.removeAllViews()
        val indicators = Array(imageUrls.size) { ImageView(requireContext()) }
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply { setMargins(8, 0, 8, 0) }

        indicators.forEach {
            it.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.viewpager2_background_color))
            it.layoutParams = layoutParams
            layoutOnBoardingIndicators.addView(it)
        }
    }

    private fun setCurrentOnboardingIndicator(index: Int) {
        for (i in 0 until layoutOnBoardingIndicators.childCount) {
            val imageView = layoutOnBoardingIndicators.getChildAt(i) as ImageView
            imageView.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.viewpager2_background_color
                )
            )
        }
    }

    private fun setContestLink(position: Int) {
        if (position in homepageUrls.indices) {
            val url = homepageUrls[position]
            textViewContestName.setOnClickListener {
                if (url.isNotEmpty()) {
                    try {
                        val uri = Uri.parse(if (!url.startsWith("http")) "https://$url" else url)
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "URL 이동에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "유효하지 않은 URL입니다.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            textViewContestName.setOnClickListener {
                Toast.makeText(requireContext(), "데이터를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupTabLayout(view: View) {
        tabLayout = view.findViewById(R.id.tabLayout)
        viewPagerTabs = view.findViewById(R.id.view_pager)

        val tabAdapter = TabAdapter(childFragmentManager, lifecycle)
        tabAdapter.addFragment(Fragment_Tab_All(), "전체보기")
        tabAdapter.addFragment(Fragment_Tab_DeadLine(), "마감순")

        viewPagerTabs.adapter = tabAdapter
        viewPagerTabs.offscreenPageLimit = 1

        TabLayoutMediator(tabLayout, viewPagerTabs) { tab, position ->
            tab.text = tabAdapter.getPageTitle(position)
        }.attach()
    }
}
