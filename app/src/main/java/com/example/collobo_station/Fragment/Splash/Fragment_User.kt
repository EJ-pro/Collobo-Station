package com.example.collobo_station.Fragment.Splash

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
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.collobo_station.Login.LoginActivity
import com.example.collobo_station.R
import com.example.collobo_station.databinding.FragmentMyPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class Fragment_User : Fragment() {

    private var _binding: FragmentMyPageBinding? = null
    private val binding get() = _binding!!
    private val firestore = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()
    private var selectedImageUri: Uri? = null

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == android.app.Activity.RESULT_OK) {
                selectedImageUri = result.data?.data
                if (selectedImageUri != null) {
                    saveProfileImage()
                } else {
                    Toast.makeText(requireContext(), "이미지를 선택하지 않았습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyPageBinding.inflate(inflater, container, false)
        val view = binding.root

        // 메뉴 아이콘 클릭 리스너 설정
        binding.meunbar.setOnClickListener {
            showMenuDialog()
        }

        // 사용자 프로필 정보 가져오기
        setupFirestoreListener()


        // 프로필 이미지 클릭 리스너 추가
        binding.ivProfileImage.setOnClickListener {
            openGalleryForProfileImage() // 프로필 이미지를 변경하기 위해 갤러리 오픈
        }

        // 포트폴리오 이미지 변경 리스너
        binding.ivProfileCover.setOnClickListener {
            openGallery() // 포트폴리오 이미지를 변경하기 위해 갤러리 오픈
        }

        // 보러가기 버튼 클릭 리스너
        binding.btnOpenPortfolio.setOnClickListener {
            openPortfolioLink()
        }

        return view
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(intent)
    }

    private fun showUrlInputDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_url_input, null)
        val urlEditText = dialogView.findViewById<EditText>(R.id.et_url_input)

        AlertDialog.Builder(requireContext())
            .setTitle("포트폴리오 URL 입력")
            .setView(dialogView)
            .setPositiveButton("저장") { _, _ ->
                val url = urlEditText.text.toString().trim()
                if (url.isNotEmpty()) {
                    savePortfolioImageAndUrl(url)
                } else {
                    Toast.makeText(requireContext(), "URL을 입력해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun savePortfolioImageAndUrl(url: String) {
        val user = firebaseAuth.currentUser
        if (user == null || selectedImageUri == null) {
            Toast.makeText(requireContext(), "로그인이 필요하거나 이미지를 선택해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val userEmail = user.email ?: return
        val storageRef = FirebaseStorage.getInstance().reference.child("portfolio_images/${UUID.randomUUID()}.jpg")

        // Firebase Storage에 포트폴리오 이미지 업로드
        storageRef.putFile(selectedImageUri!!)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    updateFirestorePortfolioImage(userEmail, uri.toString(), url)
                }
            }
            .addOnFailureListener { e ->
                Log.e("Fragment_User", "포트폴리오 이미지 업로드 실패: ${e.message}")
                Toast.makeText(requireContext(), "포트폴리오 업로드 실패", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateFirestorePortfolioImage(email: String, imageUrl: String, portfolioUrl: String) {
        firestore.collection("Users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val documentId = documents.documents[0].id
                    firestore.collection("Users")
                        .document(documentId)
                        .update(
                            mapOf(
                                "profile_cover" to imageUrl,
                                "url" to portfolioUrl
                            )
                        )
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "포트폴리오 업데이트 완료!", Toast.LENGTH_SHORT).show()
                            Glide.with(this)
                                .load(imageUrl)
                                .into(binding.ivProfileCover)
                        }
                        .addOnFailureListener { e ->
                            Log.e("Fragment_User", "Firestore 업데이트 실패: ${e.message}")
                            Toast.makeText(requireContext(), "업데이트 실패", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(requireContext(), "사용자 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Log.e("Fragment_User", "사용자 정보 가져오기 실패: ${e.message}")
                Toast.makeText(requireContext(), "사용자 정보를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
    }
    private fun openGalleryForProfileImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(intent)
    }


    private fun saveProfileImage() {
        val user = firebaseAuth.currentUser ?: return
        val userEmail = user.email ?: return

        val storageRef = FirebaseStorage.getInstance().reference.child("profile_images/${UUID.randomUUID()}.jpg")
        storageRef.putFile(selectedImageUri!!)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    firestore.collection("Users")
                        .whereEqualTo("email", userEmail)
                        .get()
                        .addOnSuccessListener { documents ->
                            if (!documents.isEmpty) {
                                val documentId = documents.documents[0].id
                                firestore.collection("Users")
                                    .document(documentId)
                                    .update("profileUrl", uri.toString())
                                    .addOnSuccessListener {
                                        Toast.makeText(requireContext(), "프로필 이미지 업데이트 완료!", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("Fragment_User", "이미지 업로드 실패: ${e.message}")
            }
    }
    private fun setupFirestoreListener() {
        val user = firebaseAuth.currentUser
        if (user == null) return

        firestore.collection("Users")
            .whereEqualTo("email", user.email)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.e("Fragment_User", "Firestore 리스너 오류: ${e.message}")
                    return@addSnapshotListener
                }

                if (snapshots != null && !snapshots.isEmpty) { // 수정된 부분
                    val document = snapshots.documents[0]
                    val nickname = document.getString("nickname") ?: "닉네임 없음"
                    val profileImageUrl = document.getString("profile_cover") ?: ""
                    val profileAvatarUrl = document.getString("profile_avatar") ?: ""
                    val email = document.getString("email") ?: "이메일 없음"

                    // UI 업데이트
                    binding.tvProfileName.text = nickname
                    binding.tvProfileEmail.text = email

                    if (profileImageUrl.isNotEmpty()) {
                        Glide.with(this)
                            .load(profileImageUrl)
                            .placeholder(R.drawable.my_page_image)
                            .into(binding.ivProfileCover)
                    }

                    if (profileAvatarUrl.isNotEmpty()) {
                        Glide.with(this)
                            .load(profileAvatarUrl)
                            .placeholder(R.drawable.image_test)
                            .into(binding.ivProfileImage)
                    }
                }
            }
    }

    private fun updateFirestoreProfileImage(email: String, imageUrl: String) {
        firestore.collection("Users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val documentId = documents.documents[0].id
                    firestore.collection("Users")
                        .document(documentId)
                        .update("profile_avatar", imageUrl)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "프로필 이미지 업데이트 완료!", Toast.LENGTH_SHORT).show()
                            Glide.with(this)
                                .load(imageUrl)
                                .into(binding.ivProfileImage)
                        }
                        .addOnFailureListener { e ->
                            Log.e("Fragment_User", "Firestore 업데이트 실패: ${e.message}")
                            Toast.makeText(requireContext(), "업데이트 실패", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(requireContext(), "사용자 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Log.e("Fragment_User", "사용자 정보 가져오기 실패: ${e.message}")
                Toast.makeText(requireContext(), "사용자 정보를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
    }
    private fun fetchUserProfile() {
        val user = firebaseAuth.currentUser
        if (user == null) {
            Toast.makeText(requireContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val userEmail = user.email
        if (userEmail.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "이메일 정보를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        firestore.collection("Users")
            .whereEqualTo("email", userEmail)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.documents[0]
                    val nickname = document.getString("nickname") ?: "닉네임 없음"
                    val profileImageUrl = document.getString("profile_cover") // 프로필 커버 이미지
                    val profileAvatarUrl = document.getString("profile_avatar") // 프로필 아바타 이미지
                    val email = document.getString("email")

                    // UI 업데이트
                    binding.tvProfileName.text = nickname
                    binding.tvProfileEmail.text = email ?: "이메일 없음"

                    // 프로필 커버 이미지 설정
                    if (!profileImageUrl.isNullOrEmpty()) {
                        Glide.with(this)
                            .load(profileImageUrl)
                            .placeholder(R.drawable.my_page_image)
                            .error(R.drawable.my_page_image)
                            .into(binding.ivProfileCover)
                    } else {
                        binding.ivProfileCover.setImageResource(R.drawable.my_page_image)
                    }

                    // 프로필 아바타 이미지 설정
                    if (!profileAvatarUrl.isNullOrEmpty()) {
                        Glide.with(this)
                            .load(profileAvatarUrl)
                            .placeholder(R.drawable.image_test)
                            .error(R.drawable.image_test)
                            .into(binding.ivProfileImage)
                    } else {
                        binding.ivProfileImage.setImageResource(R.drawable.image_test)
                    }
                } else {
                    Toast.makeText(requireContext(), "사용자 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Log.e("Fragment_User", "사용자 정보 가져오기 실패: ${e.message}")
                Toast.makeText(requireContext(), "사용자 정보를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun openPortfolioLink() {
        firestore.collection("Users")
            .whereEqualTo("email", firebaseAuth.currentUser?.email)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    var url = documents.documents[0].getString("url")
                    if (url.isNullOrEmpty()) {
                        Toast.makeText(requireContext(), "유효하지 않은 링크입니다.", Toast.LENGTH_SHORT).show()
                    } else {
                        if (!url.startsWith("http://") && !url.startsWith("https://")) {
                            url = "https://$url"
                        }

                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            startActivity(intent)
                        } catch (e: Exception) {
                            Log.e("Fragment_User", "링크 열기 실패: ${e.message}")
                            Toast.makeText(requireContext(), "링크를 열 수 없습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "URL 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Log.e("Fragment_User", "링크 열기 실패: ${e.message}")
                Toast.makeText(requireContext(), "링크를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
    }
    private fun showMenuDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("메뉴")
        builder.setItems(R.array.menu_items) { _, which ->
            when (which) {
                0 -> {
                    // 로그아웃 처리
                    FirebaseAuth.getInstance().signOut()

                    // SharedPreferences에서 자동 로그인 정보 삭제
                    val sharedPreferences =
                        requireActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
                    with(sharedPreferences.edit()) {
                        clear() // 저장된 모든 데이터 삭제
                        apply()
                    }

                    // 로그인 화면으로 이동
                    val intent = Intent(activity, LoginActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }
                1 -> Toast.makeText(requireContext(), "다른 메뉴 클릭", Toast.LENGTH_SHORT).show()
            }
        }
        builder.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
