package com.example.collobo_station.Fragment.Splash

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.renderscript.Allocation
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.collobo_station.R
import com.example.collobo_station.databinding.FragmentMyPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import jp.wasabeef.blurry.Blurry
import java.util.*

class Fragment_User : Fragment() {

    private var _binding: FragmentMyPageBinding? = null
    private val binding get() = _binding!!
    private val firestore = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()
    private var selectedImageUri: Uri? = null
    private var portfolioUrl: String? = null

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == android.app.Activity.RESULT_OK) {
                selectedImageUri = result.data?.data
                if (selectedImageUri != null) {
                    // 다이얼로그 띄우기
                    showUrlInputDialog()
                } else {
                    Toast.makeText(requireContext(), "이미지를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.e("ImagePicker", "이미지 선택 취소")
            }
        }


    private fun showUrlInputDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_url_input, null)
        val urlEditText = dialogView.findViewById<EditText>(R.id.et_url_input)

        AlertDialog.Builder(requireContext())
            .setTitle("포트폴리오 URL 입력")
            .setView(dialogView)
            .setPositiveButton("저장") { _, _ ->
                var url = urlEditText.text.toString().trim()

                if (url.isNotEmpty()) {
                    // URL이 "http://" 또는 "https://"로 시작하지 않으면 "https://" 추가
                    if (!url.startsWith("http://") && !url.startsWith("https://")) {
                        url = "https://$url"
                    }

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


    private val profileImagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == android.app.Activity.RESULT_OK) {
                selectedImageUri = result.data?.data
                if (selectedImageUri != null) {
                    updateProfileImage(selectedImageUri!!)
                } else {
                    Toast.makeText(requireContext(), "이미지를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.e("ProfileImagePicker", "이미지 선택 취소")
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyPageBinding.inflate(inflater, container, false)
        val view = binding.root

        // 프로필 정보 불러오기
        fetchUserProfile()

        // 프로필 사진 클릭 리스너
        binding.ivProfileImage.setOnClickListener {
            openGalleryForProfileImage()
        }

        // 포트폴리오 이미지 변경 리스너
        binding.ivProfileCover.setOnClickListener {
            openGalleryForPortfolioImage()
        }

        // 보러가기 버튼 클릭 리스너
        binding.btnOpenPortfolio.setOnClickListener {
            openPortfolioUrl()
        }

        return view
    }

    private fun openGalleryForPortfolioImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(intent)
    }

    private fun openGalleryForProfileImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        profileImagePickerLauncher.launch(intent)
    }

    private fun updateProfileImage(uri: Uri) {
        val user = firebaseAuth.currentUser ?: return
        val userEmail = user.email ?: return
        val storageRef = FirebaseStorage.getInstance().reference.child("profile_images/${UUID.randomUUID()}.jpg")

        storageRef.putFile(uri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    firestore.collection("Users")
                        .whereEqualTo("email", userEmail)
                        .get()
                        .addOnSuccessListener { documents ->
                            if (!documents.isEmpty) {
                                val documentId = documents.documents[0].id
                                firestore.collection("Users")
                                    .document(documentId)
                                    .update("profileUrl", downloadUri.toString())
                                    .addOnSuccessListener {
                                        Toast.makeText(requireContext(), "프로필 이미지가 업데이트되었습니다!", Toast.LENGTH_SHORT).show()
                                        Glide.with(this)
                                            .load(downloadUri)
                                            .placeholder(R.drawable.image_test)
                                            .error(R.drawable.image_test)
                                            .into(binding.ivProfileImage)
                                    }
                            }
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("Fragment_User", "프로필 이미지 업로드 실패: ${e.message}")
                Toast.makeText(requireContext(), "프로필 이미지 업로드에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun openPortfolioUrl() {
        if (!portfolioUrl.isNullOrEmpty()) {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(portfolioUrl))
                startActivity(intent)
            } catch (e: Exception) {
                Log.e("Fragment_User", "URL 열기 실패: ${e.message}")
                Toast.makeText(requireContext(), "유효하지 않은 URL입니다.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "포트폴리오 URL이 없습니다.", Toast.LENGTH_SHORT).show()
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
                    val profileImageUrl = document.getString("profileUrl") ?: ""
                    val profileCoverUrl = document.getString("profile_cover") ?: ""
                    portfolioUrl = document.getString("url") ?: ""

                    // UI 업데이트
                    binding.tvProfileName.text = nickname
                    binding.tvProfileEmail.text = userEmail

                    if (profileImageUrl.isNotEmpty()) {
                        Glide.with(this)
                            .load(profileImageUrl)
                            .placeholder(R.drawable.image_test)
                            .error(R.drawable.image_test)
                            .into(binding.ivProfileImage)
                    }

                    if (profileCoverUrl.isNotEmpty()) {
                        Glide.with(this)
                            .load(profileCoverUrl)
                            .placeholder(R.drawable.my_page_image)
                            .error(R.drawable.my_page_image)
                            .into(binding.ivProfileCover)
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

    private fun savePortfolioImageAndUrl(url: String) {
        val user = firebaseAuth.currentUser
        if (user == null || selectedImageUri == null) {
            Toast.makeText(requireContext(), "로그인이 필요하거나 이미지를 선택해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val userEmail = user.email ?: return
        val storageRef = FirebaseStorage.getInstance().reference.child("portfolio_images/${UUID.randomUUID()}.jpg")

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
                                    .update(
                                        mapOf(
                                            "profile_cover" to uri.toString(),
                                            "url" to url
                                        )
                                    )
                                    .addOnSuccessListener {
                                        portfolioUrl = url
                                        Toast.makeText(requireContext(), "포트폴리오가 업데이트되었습니다!", Toast.LENGTH_SHORT).show()
                                        Glide.with(this)
                                            .load(uri)
                                            .placeholder(R.drawable.my_page_image)
                                            .error(R.drawable.my_page_image)
                                            .into(binding.ivProfileCover)
                                    }
                            }
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("Fragment_User", "포트폴리오 이미지 업로드 실패: ${e.message}")
                Toast.makeText(requireContext(), "포트폴리오 업로드에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
