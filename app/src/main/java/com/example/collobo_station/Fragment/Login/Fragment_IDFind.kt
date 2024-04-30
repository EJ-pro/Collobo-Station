package com.example.collobo_station.Fragment.Login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.collobo_station.R
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class Fragment_IDFind : Fragment() {

    private lateinit var verificationId: String
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_find_id, container, false)

        // Firebase 인증 객체 초기화
        firebaseAuth = FirebaseAuth.getInstance()

        val phoneNumberEditText = view.findViewById<EditText>(R.id.findid_ph)
        val sendVerificationButton = view.findViewById<Button>(R.id.findid_ph_ckbtn)
        val verifyCodeButton = view.findViewById<Button>(R.id.findid_code_ckbtn)

        // 전화번호 인증 요청 버튼 클릭 리스너
        sendVerificationButton.setOnClickListener {
            val phoneNumber = phoneNumberEditText.text.toString().trim()

            // 전화번호를 인증 요청하는 함수 호출
            startPhoneNumberVerification(phoneNumber)
        }

        // 인증 코드 확인 버튼 클릭 리스너
        verifyCodeButton.setOnClickListener {
            val code = "사용자가 입력한 인증 코드" // 사용자가 입력한 인증 코드를 가져오는 코드가 필요합니다.

            // 사용자가 입력한 인증 코드를 확인하는 함수 호출
            verifyPhoneNumberWithCode(code)
        }

        return view
    }

    // 전화번호로 인증 코드를 요청하는 함수
    private fun startPhoneNumberVerification(phoneNumber: String) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,
            60,
            TimeUnit.SECONDS,
            requireActivity(),
            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // 자동으로 인증이 완료된 경우 처리할 코드
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    // 인증 실패 시 처리할 코드
                    Log.e(TAG, "Verification failed: $e")
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    // 인증 코드가 성공적으로 전송된 경우 처리할 코드
                    this@Fragment_IDFind.verificationId = verificationId
                }
            })
    }

    // 사용자가 입력한 인증 코드를 확인하는 함수
    private fun verifyPhoneNumberWithCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        signInWithPhoneAuthCredential(credential)
    }

    // PhoneAuthCredential을 사용하여 사용자를 인증하는 함수
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // 인증이 성공한 경우 처리할 코드
                    val user = task.result?.user
                    Log.d(TAG, "signInWithCredential:success, user: $user")
                } else {
                    // 인증이 실패한 경우 처리할 코드
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // 인증 코드가 유효하지 않은 경우
                        // 사용자에게 메시지를 표시하거나 다른 조치를 취할 수 있습니다.
                    }
                }
            }
    }

    companion object {
        private const val TAG = "Fragment_IDFind"
    }
}
