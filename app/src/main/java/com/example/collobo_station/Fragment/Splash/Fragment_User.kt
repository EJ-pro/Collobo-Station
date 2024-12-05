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
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
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
        binding.btnEditProfile.setOnClickListener {
            showEditProfileDialog()
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
            .document(userEmail)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // Firestore에서 데이터 가져오기
                    val nickname = document.getString("nickname") ?: "닉네임 없음"
                    val name = document.getString("name") ?: "이름 없음"
                    val dob = document.getString("dob") ?: "생년월일 없음"
                    val phone = document.getString("phone") ?: "연락처 없음"
                    val address = document.getString("address") ?: "주소 없음"
                    val education = document.getString("education") ?: "학력 없음"
                    val grade = document.getString("grade") ?: "학년 없음"
                    val awards = document.get("awards") as? List<String> ?: emptyList()
                    val skills = document.get("skills") as? List<String> ?: emptyList()

                    // 추가: profileUrl 가져오기
                    val profileUrl = document.getString("profileUrl") ?: ""
                    val profileCoverUrl = document.getString("profile_cover") ?: ""
                    val existingUrl = document.getString("url") ?: ""
                    portfolioUrl = existingUrl

                    // UI 업데이트
                    binding.tvEducation.text = education
                    binding.tvGrade.text = grade
                    binding.tvProfileName.text = nickname
                    binding.tvProfileEmail.text = userEmail
                    binding.tvdob.text = dob
                    binding.tvphone.text = phone
                    binding.tvaddress.text = address
                    binding.tvemail.text = userEmail
                    binding.tvname.text = name
                    binding.tveducationAbout.text = education

                    binding.tvAwards.text = awards.joinToString(separator = "\n\n")
                    binding.tvSkills.text = skills.joinToString(separator = " / ")

                    // 프로필 이미지 업데이트
                    if (profileUrl.isNotEmpty()) {
                        Glide.with(this)
                            .load(profileUrl)
                            .placeholder(R.drawable.image_test)
                            .error(R.drawable.image_test)
                            .into(binding.ivProfileImage)
                    } else {
                        // URL이 없을 경우 기본 이미지 로딩
                        binding.ivProfileImage.setImageResource(R.drawable.image_test)
                    }

                    // 포트폴리오 커버 이미지 업데이트
                    if (profileCoverUrl.isNotEmpty()) {
                        Glide.with(this)
                            .load(profileCoverUrl)
                            .placeholder(R.drawable.my_page_image)
                            .error(R.drawable.my_page_image)
                            .into(binding.ivProfileCover)
                    } else {
                        binding.ivProfileCover.setImageResource(R.drawable.my_page_image)
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

    private fun showEditProfileDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_profile, null)

        val editNickname = dialogView.findViewById<EditText>(R.id.et_edit_nickname)
        val editName = dialogView.findViewById<EditText>(R.id.et_edit_name)
        val editDob = dialogView.findViewById<EditText>(R.id.et_edit_dob)
        val editPhone = dialogView.findViewById<EditText>(R.id.et_edit_phone)
        val editAddress = dialogView.findViewById<EditText>(R.id.et_edit_address)
        val editEducation = dialogView.findViewById<EditText>(R.id.et_edit_education)
        val editGrade = dialogView.findViewById<EditText>(R.id.et_edit_grade)

        val user = firebaseAuth.currentUser ?: return
        val userEmail = user.email ?: return

        firestore.collection("Users")
            .document(userEmail)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // 현재 사용자 정보 다이얼로그에 설정
                    editNickname.setText(document.getString("nickname"))
                    editName.setText(document.getString("name"))
                    editDob.setText(document.getString("dob"))
                    editPhone.setText(document.getString("phone"))
                    editAddress.setText(document.getString("address"))
                    editEducation.setText(document.getString("education"))
                    editGrade.setText(document.getString("grade"))

                    val awards = document.get("awards") as? List<String> ?: emptyList()
                    val skills = document.get("skills") as? List<String> ?: emptyList()

                    setupAwardsAndSkillsDialog(dialogView, awards, skills)

                    // 다이얼로그 생성
                    AlertDialog.Builder(requireContext())
                        .setTitle("내 정보 수정")
                        .setView(dialogView)
                        .setPositiveButton("저장") { _, _ ->
                            val newNickname = editNickname.text.toString().trim()
                            val newName = editName.text.toString().trim()
                            val newDob = editDob.text.toString().trim()
                            val newPhone = editPhone.text.toString().trim()
                            val newAddress = editAddress.text.toString().trim()
                            val newEducation = editEducation.text.toString().trim()
                            val newGrade = editGrade.text.toString().trim()

                            // Awards, Skills 업데이트
                            saveUpdatedAwardsAndSkillsToFirebase(dialogView)

                            // 닉네임 중복 체크 로직 추가
                            checkNicknameDuplicateAndUpdate(userEmail, newNickname, newName, newDob, newPhone, newAddress, newEducation, newGrade)
                        }
                        .setNegativeButton("취소") { dialog, _ -> dialog.dismiss() }
                        .create()
                        .show()

                } else {
                    Toast.makeText(requireContext(), "사용자 데이터를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Log.e("Fragment_User", "사용자 데이터 로드 실패: ${e.message}")
                Toast.makeText(requireContext(), "데이터를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkNicknameDuplicateAndUpdate(
        userEmail: String,
        newNickname: String,
        newName: String,
        newDob: String,
        newPhone: String,
        newAddress: String,
        newEducation: String,
        newGrade: String
    ) {
        // 닉네임이 비어있으면 그냥 저장
        if (newNickname.isEmpty()) {
            updateUserProfileData(userEmail, newNickname, newName, newDob, newPhone, newAddress, newEducation, newGrade)
            return
        }

        // 현재 사용자 documentId 찾기
        firestore.collection("Users")
            .document(userEmail)
            .get()
            .addOnSuccessListener { currentUserDoc ->
                val currentDocumentId = currentUserDoc.id

                // 같은 닉네임을 가진 다른 사용자 존재 여부 확인
                firestore.collection("Users")
                    .whereEqualTo("nickname", newNickname)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        // 같은 닉네임을 가진 문서가 존재
                        val duplicateDocs = querySnapshot.documents
                        if (duplicateDocs.isNotEmpty()) {
                            // 같은 닉네임을 사용하는데, 그 문서가 자기 자신이 아닌 경우
                            val isUsedByOther = duplicateDocs.any { it.id != currentDocumentId }
                            if (isUsedByOther) {
                                Toast.makeText(requireContext(), "이미 사용중인 닉네임입니다.", Toast.LENGTH_SHORT).show()
                                return@addOnSuccessListener
                            }
                        }
                        // 중복되지 않거나 자기 자신이 사용하는 닉네임이면 업데이트 가능
                        updateUserProfileData(userEmail, newNickname, newName, newDob, newPhone, newAddress, newEducation, newGrade)
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "닉네임 중복 확인 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "사용자 정보 확인 실패: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateUserProfileData(
        userEmail: String,
        newNickname: String,
        newName: String,
        newDob: String,
        newPhone: String,
        newAddress: String,
        newEducation: String,
        newGrade: String
    ) {
        val updatedData = mapOf(
            "nickname" to newNickname,
            "name" to newName,
            "dob" to newDob,
            "phone" to newPhone,
            "address" to newAddress,
            "education" to newEducation,
            "grade" to newGrade
        )

        firestore.collection("Users")
            .document(userEmail)
            .update(updatedData)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "정보가 성공적으로 수정되었습니다.", Toast.LENGTH_SHORT).show()
                // UI 업데이트
                fetchUserProfile()
            }
            .addOnFailureListener { e ->
                Log.e("Fragment_User", "사용자 정보 업데이트 실패: ${e.message}")
                Toast.makeText(requireContext(), "정보를 수정하는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupAwardsAndSkillsDialog(view: View, awards: List<String>, skills: List<String>) {
        val awardsContainer = view.findViewById<LinearLayout>(R.id.awards_container)
        val skillsContainer = view.findViewById<LinearLayout>(R.id.skills_container)
        val addAwardButton = view.findViewById<Button>(R.id.btn_add_award)
        val addSkillButton = view.findViewById<Button>(R.id.btn_add_skill)

        // 초기 데이터 추가
        fun addAwardField(value: String = "") {
            val editText = EditText(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setText(value)
                hint = "수상 경력 입력"
            }
            awardsContainer.addView(editText)
        }

        fun addSkillField(value: String = "") {
            val editText = EditText(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setText(value)
                hint = "기술 스택 입력"
            }
            skillsContainer.addView(editText)
        }

        // Awards 초기화
        awards.forEach { addAwardField(it) }
        addAwardButton.setOnClickListener { addAwardField() }

        // Skills 초기화
        skills.forEach { addSkillField(it) }
        addSkillButton.setOnClickListener { addSkillField() }
    }
    private fun saveUpdatedAwardsAndSkillsToFirebase(view: View) {
        val awardsContainer = view.findViewById<LinearLayout>(R.id.awards_container)
        val skillsContainer = view.findViewById<LinearLayout>(R.id.skills_container)

        val updatedAwards = mutableListOf<String>()
        val updatedSkills = mutableListOf<String>()

        // Awards 데이터 수집
        for (i in 0 until awardsContainer.childCount) {
            val editText = awardsContainer.getChildAt(i) as EditText
            val text = editText.text.toString().trim()
            if (text.isNotEmpty()) updatedAwards.add(text)
        }

        // Skills 데이터 수집
        for (i in 0 until skillsContainer.childCount) {
            val editText = skillsContainer.getChildAt(i) as EditText
            val text = editText.text.toString().trim()
            if (text.isNotEmpty()) updatedSkills.add(text)
        }

        val userEmail = firebaseAuth.currentUser?.email ?: return
        val updates = mapOf(
            "awards" to updatedAwards,
            "skills" to updatedSkills
        )

        firestore.collection("Users").document(userEmail)
            .update(updates)
            .addOnSuccessListener {
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "저장 실패: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
