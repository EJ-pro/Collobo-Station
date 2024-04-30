package com.example.collobo_station

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.storage.storage

class ContestDetailActivity : AppCompatActivity() {
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

        // 이미지를 로드하고 ImageView에 설정
        val storageReference = contestImage?.let { Firebase.storage.reference.child(it) }
        storageReference?.getBytes(Long.MAX_VALUE)
            ?.addOnSuccessListener { bytes ->
                // 다운로드 성공 시 바이트 데이터를 비트맵으로 변환하여 ImageView에 설정
                val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                contestImageView.setImageBitmap(bmp)
            }
            ?.addOnFailureListener { exception ->
                // 다운로드 실패 시
            }

        // url_Button을 클릭하면 contestUrl 주소로 이동
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

        val shareBtn: Button = findViewById(R.id.shareBtn)
        shareBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
            intent.type = "text/plain"
            val blogUrl = contestHomepageUrl
            val content = "친구가 링크를 공유했어요!\n어떤 링크인지 들어가서 확인해볼까요?"
            intent.putExtra(Intent.EXTRA_TEXT,"$content\n\n$blogUrl")

            val chooserTitle = "친구에게 공유하기"
            startActivity(Intent.createChooser(intent, chooserTitle))
        }
    }
    // 웹페이지 열기
    private fun openWebPage(url: String) {
        val webpage = Uri.parse(url)

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(webpage.toString()))
        startActivity(intent)

    }
}
