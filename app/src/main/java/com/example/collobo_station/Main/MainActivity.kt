package com.example.collobo_station.Main

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.animation.AccelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.collobo_station.Fragment.Home.Fragment_Home
import com.example.collobo_station.Fragment.Scrap.Fragment_Scrap
import com.example.collobo_station.Fragment.Splash.Fragment_User
import com.example.collobo_station.R
import com.example.collobo_station.Team_Matching.Project_Participation
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private lateinit var fabAction: FloatingActionButton
    private lateinit var fabSub2: FloatingActionButton
    private var isExpanded = false
    private var doubleBackToExitPressedOnce = false

    private var currentSelectedItemId = R.id.navigation_item1

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
        android.os.Handler().postDelayed({
            doubleBackToExitPressedOnce = false
        }, 2000)
    }

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        if (item.itemId == currentSelectedItemId) {
            return@OnNavigationItemSelectedListener false
        }

        when (item.itemId) {
            R.id.navigation_item1 -> {
                replaceFragment(Fragment_Home())
                currentSelectedItemId = R.id.navigation_item1
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_item2 -> {
                val intent = Intent(this, Project_Participation::class.java)
                startActivity(intent)
                currentSelectedItemId = R.id.navigation_item2
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_item3 -> {
                replaceFragment(Fragment_Scrap())
                currentSelectedItemId = R.id.navigation_item3
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_item4 -> {
                replaceFragment(Fragment_User())
                currentSelectedItemId = R.id.navigation_item4
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

        replaceFragment(Fragment_Home())
        currentSelectedItemId = R.id.navigation_item1
        navView.selectedItemId = R.id.navigation_item1

        fabAction = findViewById(R.id.fab_action)
        fabSub2 = findViewById(R.id.fab_sub2)

        fabAction.setOnClickListener {
            if (isExpanded) {
                collapseFabMenu()
            } else {
                expandFabMenu()
            }
        }

        fabSub2.setOnClickListener {
            val intent = Intent(this, Portfolio_management::class.java)
            startActivity(intent)
        }
    }

    private fun expandFabMenu() {
        isExpanded = true
        // fabSub1 제거로 인해 fabSub2만 남음
        val animSub2 = ObjectAnimator.ofFloat(fabSub2, "translationY", -resources.getDimension(R.dimen.standard_65))

        AnimatorSet().apply {
            play(animSub2)
            interpolator = AccelerateInterpolator()
            duration = 300
            start()
        }
        fabAction.animate().rotation(45f)
    }

    private fun collapseFabMenu() {
        isExpanded = false
        val animSub2 = ObjectAnimator.ofFloat(fabSub2, "translationY", 0f)

        AnimatorSet().apply {
            play(animSub2)
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
