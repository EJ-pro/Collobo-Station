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
    private var originalInputPhoneNumber: String? = null // 사용자가 입력한 원본 전화번호 (하이픈 여부 상관없음)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_find_id, container, false)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val phoneNumberEditText = view.findViewById<EditText>(R.id.findid_ph)
        val sendVerificationButton = view.findViewById<Button>(R.id.findid_ph_ckbtn)
        val verifyCodeButton = view.findViewById<Button>(R.id.findid_code_ckbtn)
        val verificationCodeEditText = view.findViewById<EditText>(R.id.findid_code)

        // 전화번호 인증 요청
        sendVerificationButton.setOnClickListener {
            val phoneNumber = phoneNumberEditText.text.toString().trim()

            if (phoneNumber.isEmpty()) {
                Toast.makeText(requireContext(), "전화번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            originalInputPhoneNumber = phoneNumber
            val e164Phone = convertToE164Format(phoneNumber)
            if (e164Phone == null) {
                Toast.makeText(requireContext(), "전화번호 형식이 올바르지 않습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            startPhoneNumberVerification(e164Phone)
        }

        // 인증 코드 확인
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
        // phoneNumber는 E.164 형식
        PhoneAuthProvider.verifyPhoneNumber(
            PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneNumber)
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

                    override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
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
                    Log.d(TAG, "signInWithCredential:success")
                    // 인증 성공 -> Firestore 조회
                    originalInputPhoneNumber?.let { phoneNumber ->
                        val firestoreFormatNumber = convertToFirestoreFormat(phoneNumber)
                        fetchEmailFromPhoneNumber(firestoreFormatNumber)
                    }
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(requireContext(), "잘못된 인증 코드입니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun fetchEmailFromPhoneNumber(firestoreFormattedNumber: String) {
        // Firestore에 "010-3951-1401" 이런식으로 저장되어 있다고 가정
        firestore.collection("Users")
            .whereEqualTo("phone_number", firestoreFormattedNumber)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.documents[0]
                    val email = document.getString("email")
                    showEmailDialog(email)
                } else {
                    Log.e(TAG, "전화번호 검색 실패: $firestoreFormattedNumber")
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
        AlertDialog.Builder(requireContext())
            .setTitle("계정 정보")
            .setMessage("이메일: $message")
            .setPositiveButton("확인") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    /**
     * E.164 형식으로 변환
     * 사용자가 "010-3951-1401"이나 "01039511401"로 입력하면 "+821039511401" 반환
     */
    private fun convertToE164Format(inputNumber: String): String? {
        // 하이픈 제거
        val cleaned = inputNumber.replace("-", "")
        // 길이가 11자리이며 '010'으로 시작한다고 가정 (한국 전화번호)
        // 형식: 01039511401 -> +821039511401
        return if (cleaned.startsWith("0") && cleaned.length == 11) {
            "+82" + cleaned.removePrefix("0")
        } else {
            null // 형식이 맞지 않는 경우 null
        }
    }

    /**
     * Firestore 형식으로 변환
     * Firestore에는 "010-3951-1401" 형태로 저장되어 있다고 가정
     */
    private fun convertToFirestoreFormat(inputNumber: String): String {
        // 기존 데이터가 항상 "010-xxxx-xxxx" 형태라고 가정
        // 입력으로 들어온 번호에서 하이픈 제거 후 하이픈 위치 삽입
        val cleaned = inputNumber.replace("-", "")
        // 길이가 11자리라고 가정: 01039511401
        // -> 010-3951-1401
        return if (cleaned.length == 11 && cleaned.startsWith("010")) {
            cleaned.substring(0,3) + "-" + cleaned.substring(3,7) + "-" + cleaned.substring(7)
        } else {
            // 혹시 형식이 다르면 그대로 리턴하거나 에러 처리
            inputNumber
        }
    }

    companion object {
        private const val TAG = "Fragment_IDFind"
    }
}
