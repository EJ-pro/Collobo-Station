package com.example.collobo_station.Login

import com.example.collobo_station.Fragment.Login.Fragment_IDFind
import ViewPagerFindIDAdapter
import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.collobo_station.R
import androidx.viewpager2.widget.ViewPager2
import com.example.collobo_station.Fragment.Login.Fragment_PWFind

class ID_PW_Find : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_id_pw)

        // ViewPager2 객체 가져오기
        val viewPager2 = findViewById<ViewPager2>(R.id.id_pw_find_viewpage)

        // ViewPager2 어댑터 생성
        val adapter = ViewPagerFindIDAdapter(this)
        val findidFragment = Fragment_IDFind()
        adapter.addFragment(findidFragment)

        val adapter2 = ViewPagerFindIDAdapter(this)
        val findidFragment2 = Fragment_PWFind()
        adapter2.addFragment(findidFragment2)
        // ViewPager2에 어댑터 설정
        viewPager2.adapter = adapter

        val btn_id : Button = findViewById(R.id.go_findid_btn);
        btn_id.setOnClickListener {
            // 클릭 이벤트가 발생했을 때 실행할 코드를 여기에 작성합니다.
            viewPager2.adapter = adapter
        }
        val btn_pw : Button = findViewById(R.id.go_findpw_btn);
        btn_pw.setOnClickListener {
            // 클릭 이벤트가 발생했을 때 실행할 코드를 여기에 작성합니다.
            viewPager2.adapter = adapter2
        }
    }
}
