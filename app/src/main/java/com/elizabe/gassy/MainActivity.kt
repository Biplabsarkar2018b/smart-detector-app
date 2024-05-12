package com.elizabe.gassy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.*
import com.google.android.material.bottomnavigation.*

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        SocketHandler.setSocket()
        SocketHandler.establishConnection()

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.navigation_notifications -> {
                    replaceFragment(NotificationsFragment())
                    true
                }
                else -> false
            }
        }

        // Set the initial fragment
        bottomNavigationView.selectedItemId = R.id.navigation_home

    }

    override fun onDestroy() {
        super.onDestroy()
        SocketHandler.closeConnection()
    }
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}