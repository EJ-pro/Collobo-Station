package com.example.collobo_station.Fragment.Home

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.collobo_station.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import kotlin.random.Random

private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

class ContestDetailActivity : AppCompatActivity(), ColorPickerDialogListener {
    private lateinit var contestImageView: ImageView
    private lateinit var contestNameTextView: TextView
    private lateinit var contestFieldTextView: TextView
    private lateinit var contestPeriodStartTextView: TextView
    private lateinit var contestPeriodTextView: TextView
    private lateinit var contestCountTextView: TextView
    private lateinit var contestRegionTextView: TextView
    private lateinit var contestAwardTextView: TextView
    private lateinit var contestUrlTextView: TextView
    private lateinit var contestOrganizerTextView: TextView
    private lateinit var contestHostTextView: TextView
    private lateinit var contestEligibilityTextView: TextView
    private lateinit var contestHomepageUrlTextView: TextView
    private lateinit var contestprecautionsTextView: TextView

    // 선택된 색상을 전달할 콜백 람다
    private var colorSelectedCallback: ((Int) -> Unit)? = null
    private val COLOR_DIALOG_ID = 0

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_contest_detail)

        // Intent에서 데이터 가져오기
        val contestName = intent.getStringExtra("contestName")
        val contestField = intent.getStringExtra("contestField")
        val contestPeriodStart = intent.getStringExtra("contestPeriodStart")
        val contestPeriod = intent.getStringExtra("contestPeriod")
        val contestCount = intent.getStringExtra("contestCount")
        val contestImage = intent.getStringExtra("contestImage")
        val contestRegion = intent.getStringExtra("contestRegion")
        val contestAward = intent.getStringExtra("contestAward")
        val contestUrl = intent.getStringExtra("contestUrl")
        val contestOrganizer = intent.getStringExtra("contestOrganizer")
        val contestHost = intent.getStringExtra("contestHost")
        val contestEligibility = intent.getStringExtra("contestEligibility")
        val contestHomepageUrl = intent.getStringExtra("contestHomepageUrl")
        val contestprecautions = intent.getStringExtra("contestprecautions")

        // 레이아웃 요소 찾기
        contestImageView = findViewById(R.id.contest_image)
        contestNameTextView = findViewById(R.id.contestNameTextView)
        contestFieldTextView = findViewById(R.id.contestField)
        contestPeriodStartTextView = findViewById(R.id.contestPeriodStartTextView)
        contestPeriodTextView = findViewById(R.id.contestPeriodTextView)
        contestCountTextView = findViewById(R.id.contestCountTextView)
        contestRegionTextView = findViewById(R.id.contestRegion)
        contestAwardTextView = findViewById(R.id.contestAward)
        contestUrlTextView = findViewById(R.id.contestUrl)
        contestOrganizerTextView = findViewById(R.id.contestOrganizer)
        contestHostTextView = findViewById(R.id.contestHost)
        contestEligibilityTextView = findViewById(R.id.contestEligibility)
        contestHomepageUrlTextView = findViewById(R.id.contestHomepageUrl)
        contestprecautionsTextView = findViewById(R.id.contestprecautions)

        // 이미지 로드
        val storageReference = contestImage?.let { Firebase.storage.reference.child(it) }
        storageReference?.getBytes(Long.MAX_VALUE)
            ?.addOnSuccessListener { bytes ->
                val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                contestImageView.setImageBitmap(bmp)
            }
            ?.addOnFailureListener {
                // 실패 시 무시
            }

        // URL 버튼 클릭
        val urlButton: Button = findViewById(R.id.url_button)
        urlButton.setOnClickListener {
            val url = contestHomepageUrl
            if (url != null) {
                openWebPage(url)
            }
        }

        // 텍스트뷰에 데이터 설정
        contestNameTextView.text = contestName
        contestFieldTextView.text = contestField
        contestPeriodTextView.text = contestPeriod
        contestPeriodStartTextView.text = contestPeriodStart
        contestCountTextView.text = contestCount
        contestRegionTextView.text = contestRegion
        contestAwardTextView.text = contestAward
        contestUrlTextView.text = contestUrl
        contestOrganizerTextView.text = contestOrganizer
        contestHostTextView.text = contestHost
        contestEligibilityTextView.text = contestEligibility
        contestHomepageUrlTextView.text = contestHomepageUrl
        contestprecautionsTextView.text = contestprecautions

        // 공유 버튼
        val shareBtn: ImageButton = findViewById(R.id.shareBtn)
        shareBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
            intent.type = "text/plain"
            val blogUrl = contestHomepageUrl
            val content = "친구가 링크를 공유했어요!\n어떤 링크인지 들어가서 확인해볼까요?"
            intent.putExtra(Intent.EXTRA_TEXT,"$content\n\n$blogUrl")

            val chooserTitle = "친구에게 공유하기"
            startActivity(Intent.createChooser(intent, chooserTitle))
        }

        // 스크랩 버튼
        val scrapButton: ImageButton = findViewById(R.id.scrap_add)
        scrapButton.setOnClickListener {
            // 색상 선택 방법 다이얼로그 표시
            showColorChoiceDialog(contestName ?: "Untitled", contestImage ?: "", contestHomepageUrl ?: "")
        }
    }

    // 색상 선택 방법 다이얼로그 (랜덤 vs 직접선택)
    private fun showColorChoiceDialog(title: String, imageUrl: String, pageUrl: String) {
        val options = arrayOf("랜덤 색상 선택", "색상 직접 선택")
        AlertDialog.Builder(this)
            .setTitle("색상을 선택하는 방법을 고르세요")
            .setItems(options) { dialog, which ->
                when(which) {
                    0 -> { // 랜덤 색상
                        val randomColor = getRandomColor()
                        addToScrapCollection(title, imageUrl, pageUrl, randomColor)
                    }
                    1 -> { // 직접 색상 선택
                        showColorPickerDialog { selectedColor ->
                            addToScrapCollection(title, imageUrl, pageUrl, selectedColor)
                        }
                    }
                }
                dialog.dismiss()
            }
            .show()
    }

    private fun showColorPickerDialog(onColorSelected: (Int) -> Unit) {
        // 콜백 저장
        colorSelectedCallback = onColorSelected
        // ColorPicker 다이얼로그 표시
        ColorPickerDialog
            .newBuilder()
            .setColor(Color.RED) // 초기 컬러
            .setShowAlphaSlider(true) // 투명도 조절 가능
            .setDialogType(ColorPickerDialog.TYPE_CUSTOM)
            .setAllowPresets(true)
            .setPresets(intArrayOf(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.CYAN, Color.MAGENTA))
            .setDialogId(COLOR_DIALOG_ID)
            .show(this)
    }

    // ColorPickerDialogListener 구현 메서드
    override fun onColorSelected(dialogId: Int, color: Int) {
        if (dialogId == COLOR_DIALOG_ID) {
            // 사용자가 색상 선택 완료 시 콜백 호출
            colorSelectedCallback?.invoke(color)
        }
    }

    override fun onDialogDismissed(dialogId: Int) {
        // 다이얼로그 닫힐 때 특별 처리 없음
    }

    private fun getRandomColor(): Int {
        val r = (180..255).random()
        val g = (180..255).random()
        val b = (180..255).random()
        return Color.rgb(r, g, b)
    }

    private fun addToScrapCollection(title: String, imageUrl: String, pageUrl: String, color: Int) {
        val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: "unknown_user"

        // Firestore 중복 확인
        firestore.collection("Scrap")
            .whereEqualTo("title", title)
            .whereEqualTo("nickname", userEmail)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    // 이미 존재
                    Toast.makeText(this, "이미 스크랩에 추가된 항목입니다.", Toast.LENGTH_SHORT).show()
                } else {
                    // order 필드 최대값 찾기
                    firestore.collection("Scrap")
                        .get()
                        .addOnSuccessListener { allDocuments ->
                            val maxOrder = allDocuments.maxOfOrNull { it.getLong("order") ?: 0L } ?: 0L
                            val newOrder = maxOrder + 1

                            val scrapData = mapOf(
                                "title" to title,
                                "imageUrl" to imageUrl,
                                "pageUrl" to pageUrl,
                                "color" to color,
                                "nickname" to userEmail,
                                "추가순서" to newOrder
                            )

                            firestore.collection("Scrap")
                                .add(scrapData)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "스크랩에 추가되었습니다!", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "스크랩 추가 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "order 값 불러오기 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "중복 확인 실패: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // 웹페이지 열기
    private fun openWebPage(url: String) {
        val webpage = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, webpage)
        startActivity(intent)
    }
}
