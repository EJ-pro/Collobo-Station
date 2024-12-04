package com.example.collobo_station.Fragment.Login

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.collobo_station.R
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.TimeUnit

class Fragment_IDFind : Fragment() {

    private var verificationId: String? = null
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var currentPhoneNumber: String? = null // phoneNumber 저장용 변수

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_find_id, container, false)

        // Firebase 인증 객체 초기화
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val phoneNumberEditText = view.findViewById<EditText>(R.id.findid_ph)
        val sendVerificationButton = view.findViewById<Button>(R.id.findid_ph_ckbtn)
        val verifyCodeButton = view.findViewById<Button>(R.id.findid_code_ckbtn)
        val verificationCodeEditText = view.findViewById<EditText>(R.id.findid_code)

        // 전화번호 인증 요청 버튼 클릭 리스너
        sendVerificationButton.setOnClickListener {
            val phoneNumber = phoneNumberEditText.text.toString().trim()

            if (phoneNumber.isEmpty()) {
                Toast.makeText(requireContext(), "전화번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 전화번호를 Firestore 형식에 맞게 변환
            val formattedPhoneNumber = if (phoneNumber.startsWith("+")) {
                phoneNumber // 이미 국제 형식
            } else {
                "+82" + phoneNumber.removePrefix("0") // 대한민국 국가 코드 추가
            }

            // 인증 요청 함수 호출 시 phoneNumber 전달
            startPhoneNumberVerification(formattedPhoneNumber)
        }

        // 인증 코드 확인 버튼 클릭 리스너
        verifyCodeButton.setOnClickListener {
            val code = verificationCodeEditText.text.toString().trim()

            if (verificationId.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "인증 코드가 전송되지 않았습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (code.isEmpty()) {
                Toast.makeText(requireContext(), "인증 코드를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            verifyPhoneNumberWithCode(code)
        }

        return view
    }

    private fun startPhoneNumberVerification(phoneNumber: String) {
        // 인증용 포맷
        val formattedPhoneNumberForAuth = formatPhoneNumberForAuth(phoneNumber)
        currentPhoneNumber = formatPhoneNumberForFirestore(phoneNumber) // Firestore 조회용 포맷 저장

        Log.d(TAG, "Formatted for Auth: $formattedPhoneNumberForAuth")
        Log.d(TAG, "Formatted for Firestore: $currentPhoneNumber")

        PhoneAuthProvider.verifyPhoneNumber(
            PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(formattedPhoneNumberForAuth) // 인증용 포맷 사용
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(requireActivity())
                .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                        signInWithPhoneAuthCredential(credential)
                    }

                    override fun onVerificationFailed(e: FirebaseException) {
                        Log.e(TAG, "인증 실패: ${e.message}")
                        Toast.makeText(requireContext(), "인증 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                    }

                    override fun onCodeSent(
                        verificationId: String,
                        token: PhoneAuthProvider.ForceResendingToken
                    ) {
                        this@Fragment_IDFind.verificationId = verificationId
                        Toast.makeText(requireContext(), "인증 코드가 전송되었습니다.", Toast.LENGTH_SHORT).show()
                    }
                })
                .build()
        )
    }



    private fun verifyPhoneNumberWithCode(code: String) {
        try {
            val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
            signInWithPhoneAuthCredential(credential)
        } catch (e: Exception) {
            Log.e(TAG, "잘못된 인증 코드: ${e.message}")
            Toast.makeText(requireContext(), "잘못된 인증 코드입니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    Log.d(TAG, "signInWithCredential:success, user: $user")

                    // 현재 저장된 전화번호로 이메일 가져오기
                    currentPhoneNumber?.let { phoneNumber ->
                        fetchEmailFromPhoneNumber(phoneNumber)
                    }
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(requireContext(), "잘못된 인증 코드입니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun fetchEmailFromPhoneNumber(phoneNumber: String) {

        val formattedPhoneNumber = formatPhoneNumberForFirestore(phoneNumber)
        firestore.collection("Users")
            .whereEqualTo("phone_number", formattedPhoneNumber)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.documents[0]
                    val email = document.getString("email")

                    // 이메일을 다이얼로그로 표시
                    showEmailDialog(email)
                } else {
                    Log.e(TAG, "formattedPhoneNumber 검색 실패: ${formattedPhoneNumber}")
                    Toast.makeText(requireContext(), "해당 전화번호로 등록된 계정을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Firestore 검색 실패: ${e.message}")
                Toast.makeText(requireContext(), "계정 정보를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showEmailDialog(email: String?) {
        val message = email ?: "등록된 이메일이 없습니다."
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle("계정 정보")
            .setMessage("이메일: $message")
            .setPositiveButton("확인") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun formatPhoneNumberForAuth(phoneNumber: String): String {
        // Firebase 인증용 국제 번호 형식으로 변환
        return if (!phoneNumber.startsWith("+82")) {
            "+82" + phoneNumber.removePrefix("0") // "01012345678" → "+821012345678"
        } else {
            phoneNumber // 이미 국제 형식인 경우 그대로 반환
        }
    }

    private fun formatPhoneNumberForFirestore(phoneNumber: String): String {
        // Firestore 조회용 하이픈 포함 형식으로 변환
        return if (!phoneNumber.contains("-")) {
            phoneNumber.replaceFirst("(\\d{3})(\\d{4})(\\d{4})".toRegex(), "$1-$2-$3")
        } else {
            phoneNumber // 이미 하이픈이 포함된 경우 그대로 반환
        }
    }

    companion object {
        private const val TAG = "Fragment_IDFind"
    }
}
