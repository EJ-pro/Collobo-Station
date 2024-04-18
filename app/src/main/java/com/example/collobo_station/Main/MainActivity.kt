package com.example.collobo_station.Main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.collobo_station.Fragment.Fragment_BookMark
import com.example.collobo_station.Fragment.Fragment_Chat
import com.example.collobo_station.Fragment.Fragment_Home
import com.example.collobo_station.Fragment.Fragment_Portfolio
import com.example.collobo_station.Fragment.Fragment_User
import com.example.collobo_station.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

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
                    replaceFragment(Fragment_Portfolio())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_item5 -> {
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
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
