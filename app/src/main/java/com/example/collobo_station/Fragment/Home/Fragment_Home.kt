package com.example.collobo_station.Fragment.Home

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
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
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.collobo_station.Adapter.Home.ViewPager2Adapter
import com.example.collobo_station.R
import com.google.firebase.firestore.FirebaseFirestore
import com.example.collobo_station.Adapter.Tab.TabAdapter
import com.example.collobo_station.Data.DataInfo
import com.example.collobo_station.Data.UserInfo
import com.example.collobo_station.Login.LoginActivity
import com.example.collobo_station.Team_Matching.Team_Write_Activity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import android.content.Context
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage

class Fragment_Home : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: ViewPager2Adapter
    private lateinit var layoutOnBoardingIndicators: LinearLayout
    private lateinit var textViewContestName: Button
    private lateinit var textViewUserName: TextView
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPagerTabs: ViewPager2
    private lateinit var tabadapter: ViewPager2Adapter
    private lateinit var menu : ImageView
    private lateinit var mContext: Context
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
//    private lateinit var profileImage: de.hdodenhof.circleimageview.CircleImageView
    private var selectedImageUri: Uri? = null
    private lateinit var profileImageView: de.hdodenhof.circleimageview.CircleImageView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onDetach() {
        super.onDetach()
        // Context를 해제하거나 필요에 따라 초기화
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 이미지 선택 ActivityResultLauncher 등록
        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                selectedImageUri = data?.data // 선택한 이미지의 URI 가져오기
                if (selectedImageUri != null) {
                    updateProfileImage(selectedImageUri!!)
                } else {
                    Toast.makeText(requireContext(), "이미지를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.e("ImagePicker", "이미지 선택 취소")
            }
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        // Fragment_Home 클래스의 onCreateView 메서드에서 뷰를 초기화하고 사용자 정보를 가져오며 ViewPager 및 TabLayout을 설정합니다.
        // initView(view) - 뷰를 초기화하는 메서드입니다.
        // fetchUserInfo() - 사용자 정보를 가져오는 메서드입니다.
        // setupViewPager() - ViewPager를 설정하는 메서드입니다. Recommendation Contest 문서에서 이미지 URL을 가져와 ViewPager에 표시합니다.
        // setupTabLayout(view) - TabLayout을 설정하는 메서드입니다. ViewPagerTabs 어댑터를 생성하고 각 탭에 해당하는 Fragment를 추가합니다.
        initView(view)
        setupViewPager()
        setupTabLayout(view)
        fetchUserInfo()
        return view
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(intent)
    }

    private fun updateProfileImage(uri: Uri) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Toast.makeText(requireContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val db = FirebaseFirestore.getInstance()
        val storageRef = FirebaseStorage.getInstance().reference

        // Firestore에서 닉네임 가져오기
        db.collection("Users").whereEqualTo("email", user.email)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val nickname = documents.documents[0].getString("nickname") ?: "default_user"

                    // 닉네임 기반으로 이미지 저장
//                    val imageRef = storageRef.child("profile_images/$nickname.jpg")
//                    imageRef.putFile(uri)
//                        .addOnSuccessListener {
//                            Toast.makeText(requireContext(), "프로필 이미지가 저장되었습니다.", Toast.LENGTH_SHORT).show()
//
//                            // 저장된 이미지를 다시 로드
//                            imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
//                                Glide.with(this)
//                                    .load(downloadUri)
//                                    .into(profileImage)
//                            }
//                        }
//                        .addOnFailureListener { e ->
//                            Toast.makeText(requireContext(), "이미지 업로드에 실패했습니다: ${e.message}", Toast.LENGTH_SHORT).show()
//                        }
                } else {
                    Toast.makeText(requireContext(), "닉네임을 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "닉네임 조회에 실패했습니다: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadProfileImage(nickname: String) {
        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("profile_images/$nickname.jpg")

        imageRef.downloadUrl
            .addOnSuccessListener { uri ->
                // Glide로 이미지 로드 (캐시를 적극 활용)
                Glide.with(this)
                    .load(uri)
                    .placeholder(R.drawable.image_test) // 로딩 중 표시할 기본 이미지
                    .error(R.drawable.image_test) // 에러 시 표시할 기본 이미지
                    .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.ALL) // 캐싱 전략 설정
//                    .into(profileImage)
            }
            .addOnFailureListener {
                // 이미지 로드 실패 시 기본 이미지 설정
//                profileImage.setImageResource(R.drawable.image_test)
            }
    }


    private fun initView(view: View) {
        viewPager = view.findViewById(R.id.viewpager2)
        layoutOnBoardingIndicators = view.findViewById(R.id.indicators)
        textViewContestName = view.findViewById(R.id.textViewContestName)
        textViewUserName = view.findViewById(R.id.textViewUserName)
        menu = view.findViewById(R.id.meunbar)  // Assuming you have an ImageView with this ID

        // Set an OnClickListener for the menu ImageView
        menu.setOnClickListener {
            showMenuDialog()
        }


    }
    private fun showMenuDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("메뉴")
        builder.setItems(R.array.menu_items) { dialog, which ->
            when (which) {
                0 -> {
                    FirebaseAuth.getInstance().signOut()

                    // SharedPreferences에서 자동 로그인 정보 삭제
                    val sharedPreferences = requireActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
                    // SharedPreferences 수정
                    with(sharedPreferences.edit()) {
                        putBoolean("isLoggedIn", false) // 로그인 상태를 false로 설정
                        remove("username") // username 키의 값을 제거
                        remove("password") // password 키의 값을 제거
                        apply() // 변경사항을 저장
                    }

                    // 로그인 화면으로 이동
                    val intent = Intent(activity, LoginActivity::class.java)
                    startActivity(intent)
                    activity?.finish() // 현재 화면 종료
                }
                1 -> Toast.makeText(requireContext(), "Action Two", Toast.LENGTH_SHORT).show()
            }
        }
        builder.show()
    }

    private fun fetchUserInfo() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            textViewUserName.text = "로그인 필요"
//            profileImage.setImageResource(R.drawable.image_test)
            return
        }

        val db = FirebaseFirestore.getInstance()
        db.collection("Users").whereEqualTo("email", user.email)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.documents[0]
                    val nickname = document.getString("nickname") ?: "default_user"
                    val profileUrl = document.getString("profileUrl") // Firestore에 저장된 프로필 이미지 URL

                    // 닉네임 설정
                    textViewUserName.text = nickname

                    // Firestore에 저장된 URL로 프로필 이미지 로드
                    if (profileUrl != null) {
                        Glide.with(this)
                            .load(profileUrl)
                            .placeholder(R.drawable.image_test)
                            .error(R.drawable.image_test)
                            .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.ALL)
//                            .into(profileImage)
                    } else {
                        // URL이 없을 경우 기본 이미지 로드
//                        loadProfileImage(nickname)
                    }
                } else {
                    textViewUserName.text = "사용자"
//                    profileImage.setImageResource(R.drawable.image_test)
                }
            }
            .addOnFailureListener { e ->
                textViewUserName.text = "로그인 필요"
//                profileImage.setImageResource(R.drawable.image_test)
                Log.e(TAG, "Firestore 프로필 불러오기 실패: ${e.message}")
            }
    }

    private fun setupViewPager() {
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
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("Recommendation Contest")
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty && position < documents.size()) {
                    val document = documents.documents[position]
                    val url = document.getString("홈페이지url")

                    if (!url.isNullOrEmpty()) {
                        textViewContestName.setOnClickListener {
                            try {
                                val uri = Uri.parse(if (!url.startsWith("http")) "https://$url" else url)
                                val intent = Intent(Intent.ACTION_VIEW, uri)
                                startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(requireContext(), "URL 이동에 실패했습니다: ${e.message}", Toast.LENGTH_SHORT).show()
                                Log.e(TAG, "URL 이동 실패: ${e.message}")
                            }
                        }
                    } else {
                        textViewContestName.setOnClickListener {
                            Toast.makeText(requireContext(), "유효하지 않은 URL입니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    textViewContestName.setOnClickListener {
                        Toast.makeText(requireContext(), "데이터를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .addOnFailureListener { exception ->
                textViewContestName.setOnClickListener {
                    Toast.makeText(requireContext(), "Firestore 데이터를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "Firestore 데이터 불러오기 실패: ${exception.message}")
                }
            }
    }


    private fun setupTabLayout(view: View) {
        tabLayout = view.findViewById(R.id.tabLayout)
        viewPagerTabs = view.findViewById(R.id.view_pager)

        var tabAdapter = TabAdapter(childFragmentManager, lifecycle)

        tabAdapter.addFragment(Fragment_Tab_All(), "전체보기")
        tabAdapter.addFragment(Fragment_Tab_DeadLine(), "마감순")

        viewPagerTabs.adapter = tabAdapter
        viewPagerTabs.offscreenPageLimit = 1

        TabLayoutMediator(tabLayout, viewPagerTabs) { tab, position ->
            tab.text = tabAdapter.getPageTitle(position)
        }.attach()

    }
}