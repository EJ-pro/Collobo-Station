package com.example.collobo_station.Fragment.Bottom

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.collobo_station.databinding.FragmentPortfolioBinding
import androidx.recyclerview.widget.RecyclerView
import com.example.collobo_station.Login.LoginActivity
import com.example.collobo_station.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Fragment_Portfolio : Fragment() {
    private var _binding: FragmentPortfolioBinding? = null
    private val binding get() = _binding!!

    private lateinit var menu : ImageView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPortfolioBinding.inflate(inflater, container, false)
        val view = binding.root
        val edittext_university :EditText = binding.portfolioUniversityEdit
        val edittext_websiteLink :EditText = binding.portfolioLinkEdit
        val edittext_selfIntroduction :EditText = binding.portfolioIntroductionEdit
        val edittext_profilePictureUrl :EditText = binding.portfolioImageEdit
        val editText_profileNowCareer :EditText = binding.portfolioNowCareerEdit

        menu = view.findViewById(R.id.meunbar)  // Assuming you have an ImageView with this ID

        // Set an OnClickListener for the menu ImageView
        menu.setOnClickListener {
            showMenuDialog()
        }
        // Firestore 부분
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("portfolio").document("test")
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val university = document.getString("대학")
                    val experienceList = document.get("경력") as? List<String> ?: listOf()
                    val websiteLink = document.getString("링크")
                    val selfIntroduction = document.getString("자기소개")
                    val profilePictureUrl = document.getString("프로필사진")
                    val currentStatus = document.getString("현재경력")

                    edittext_university.setText(university);
                    edittext_websiteLink.setText(websiteLink);
                    edittext_selfIntroduction.setText(selfIntroduction);
                    edittext_profilePictureUrl.setText(profilePictureUrl);
                    edittext_university.setText(university);
                    editText_profileNowCareer.setText(currentStatus);

                    // 첫 번째 RecyclerView 설정
                    val recyclerView1: RecyclerView = binding.portfolioRecyclerView
                    recyclerView1.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                    val adapter1 = CareerAdapter(experienceList.map { CareerItem(isChecked = false, career = it) })
                    recyclerView1.adapter = adapter1

                    //경력 리사이클뷰 추가
                    //experienceList
                    // 예시로, 자기소개 텍스트를 설정합니다. 실제로는 레이아웃 파일에 맞는 ID를 사용해야 합니다.
                } else {
                    // 문서가 존재하지 않는 경우의 처리
                }
            }
            .addOnFailureListener { exception ->
                // Firestore에서 데이터를 가져오는 데 실패한 경우 처리
                // 예를 들어, Log.e()를 사용하여 오류를 기록하거나 사용자에게 알림을 표시할 수 있습니다.
            }

        return view
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
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }
    data class CareerItem(val isChecked: Boolean, val career: String)

    // CareerViewHolder 클래스를 Fragment_Portfolio 내부에 정의
    inner class CareerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val button: Button = itemView.findViewById(R.id.portfolio_current_career_btn)
        private val editText: EditText = itemView.findViewById(R.id.portfolio_current_career_edit)

        fun bind(item: CareerItem) {
            editText.setText(item.career)
        }
    }

    inner class CareerAdapter(private val careerItems: List<CareerItem>) : RecyclerView.Adapter<CareerViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CareerViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_portfolio_career, parent, false)
            return CareerViewHolder(view)
        }

        override fun onBindViewHolder(holder: CareerViewHolder, position: Int) {
            val item = careerItems[position]
            holder.bind(item)
        }

        override fun getItemCount(): Int {
            return careerItems.size
        }
    }
}


