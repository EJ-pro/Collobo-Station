package com.example.collobo_station.Team_Matching

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.collobo_station.R
import com.google.firebase.firestore.FirebaseFirestore

class AutoUploadActivity : AppCompatActivity() {

    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auto_upload)

        // 업로드할 데이터 정의
        val memoData = listOf(
            mapOf(
                "title" to "API 통합 작업 리스트",
                "content" to """
                    사용자 로그인/회원가입 API 테스트 완료
                    데이터 저장/조회 API 연결 진행 중
                    오류: 500 Internal Server Error 발생 (백엔드 팀 확인 필요)
                """.trimIndent()
            ),
            mapOf(
                "title" to "프로젝트 일정 체크리스트",
                "content" to """
                    디자인 시안 검토 (12월 5일)
                    기능 요구사항 최종 확정 (12월 7일)
                    테스트 환경 구축 (12월 10일)
                """.trimIndent()
            ),
            mapOf(
                "title" to "코드 리뷰 피드백",
                "content" to """
                    함수 이름 명확히: fetchData() → fetchUserData()
                    주석 추가 필요: 데이터 파싱 부분
                    코드 중복 제거: saveToDatabase() 리팩토링
                """.trimIndent()
            ),
            mapOf(
                "title" to "다음 회의 안건",
                "content" to """
                    팀 구성원별 진행 상황 공유
                    UI/UX 시안 수정 논의
                    추가 기능 요청 검토
                """.trimIndent()
            ),
            mapOf(
                "title" to "배포 준비 사항",
                "content" to """
                    앱 아이콘/스플래시 스크린 적용
                    Google Play Store 설명 작성
                    테스트 환경 → 운영 환경 전환
                """.trimIndent()
            ),
            mapOf(
                "title" to "디버깅 이슈 기록",
                "content" to """
                    앱 크래시 발생: 로그인 화면
                    로그 메시지: NullPointerException
                    해결 방안: null 체크 추가
                """.trimIndent()
            ),
            mapOf(
                "title" to "공부한 내용 요약",
                "content" to """
                    Android Lifecycle 이해: onCreate, onDestroy 등
                    Kotlin의 Coroutine 기본 사용법
                    Clean Architecture의 3계층 구조
                """.trimIndent()
            )
        )

        // 업로드할 이메일(문서 ID), 필요 시 변경 가능
        val userEmail = "hsshss2522@naver.com"
        uploadMemoDataToFirestore(userEmail, memoData)
    }

    private fun uploadMemoDataToFirestore(documentId: String, memoData: List<Map<String, String>>) {
        val docRef = firestore.collection("Memo").document(documentId)

        // Firestore에 저장할 memo_list 만들기
        val memoList = memoData.map { data ->
            mapOf(
                "id" to System.currentTimeMillis(),
                "title" to data["title"].orEmpty(),
                "content" to data["content"].orEmpty()
            )
        }

        val newData = mapOf("memo_list" to memoList)

        docRef.set(newData)
            .addOnSuccessListener {
                Toast.makeText(this, "메모 업로드 성공!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "메모 업로드 실패: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
