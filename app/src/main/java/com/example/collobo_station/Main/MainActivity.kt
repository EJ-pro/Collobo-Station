package com.example.collobo_station.Main

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.animation.AccelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.collobo_station.Fragment.Fragment_BookMark
import com.example.collobo_station.Fragment.Fragment_Chat
import com.example.collobo_station.Fragment.Fragment_Home
import com.example.collobo_station.Fragment.Fragment_Portfolio
import com.example.collobo_station.Fragment.Splash.Fragment_User
import com.example.collobo_station.Login.ID_PW_Find
import com.example.collobo_station.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private lateinit var fabAction: FloatingActionButton
    private lateinit var fabSub1: FloatingActionButton
    private lateinit var fabSub2: FloatingActionButton
    private lateinit var fabSub3: FloatingActionButton
    private var isExpanded = false
    private var doubleBackToExitPressedOnce = false

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()

        android.os.Handler().postDelayed({
            doubleBackToExitPressedOnce = false
        }, 2000) // 2초간 두 번째 뒤로가기 버튼이 클릭되지 않으면 리셋
    }
    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_item1 -> {
                    replaceFragment(Fragment_Home())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_item2 -> {
                    replaceFragment(Fragment_Chat())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_item3 -> {
                    replaceFragment(Fragment_BookMark())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_item4 -> {
                    replaceFragment(Fragment_User())
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        navView.selectedItemId = R.id.navigation_item1

        fabAction = findViewById(R.id.fab_action)
        fabSub1 = findViewById(R.id.fab_sub1)
        fabSub2 = findViewById(R.id.fab_sub2)

        fabAction.setOnClickListener {
            if (isExpanded) {
                collapseFabMenu()
            } else {
                expandFabMenu()
            }
        }
        fabSub1.setOnClickListener {
            val intent = Intent(this, Project_Participation::class.java)
            startActivity(intent)
        }
        fabSub2.setOnClickListener {
            val intent = Intent(this, Portfolio_management::class.java)
            startActivity(intent)
        }
    }

    private fun expandFabMenu() {
        isExpanded = true

        val animSub1 = ObjectAnimator.ofFloat(fabSub1, "translationY", -resources.getDimension(R.dimen.standard_65))
        val animSub2 = ObjectAnimator.ofFloat(fabSub2, "translationY", -resources.getDimension(R.dimen.standard_125))

        AnimatorSet().apply {
            playTogether(animSub1, animSub2)
            interpolator = AccelerateInterpolator()
            duration = 300
            start()
        }

        fabAction.animate().rotation(45f)
    }

    private fun collapseFabMenu() {
        isExpanded = false

        val animSub1 = ObjectAnimator.ofFloat(fabSub1, "translationY", 0f)
        val animSub2 = ObjectAnimator.ofFloat(fabSub2, "translationY", 0f)

        AnimatorSet().apply {
            playTogether(animSub1, animSub2)
            interpolator = AccelerateInterpolator()
            duration = 300
            start()
        }

        fabAction.animate().rotation(0f)
    }



    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
