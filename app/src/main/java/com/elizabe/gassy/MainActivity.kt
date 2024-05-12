package com.elizabe.gassy

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.*
import androidx.fragment.app.*
import com.google.android.material.bottomnavigation.*

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var ip:String
    private lateinit var thBtn:Button
    // Variables to store the last received values


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        thBtn = findViewById(R.id.thresholdBtn)
        thBtn.setOnClickListener {
            showSetValueDialog()
        }
        ip = intent.getStringExtra("ip").toString()
        SocketHandler.setSocket(ip = ip)
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
    private fun showSetValueDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_set_value, null)
        val currentValueTextView = dialogView.findViewById<TextView>(R.id.text_view_current_value)
        val newValueEditText = dialogView.findViewById<EditText>(R.id.edit_text_new_value)
        val setButton = dialogView.findViewById<Button>(R.id.button_set)
        var currentValue = this.getSharedPreferences("sp",Context.MODE_PRIVATE).getInt("thValue",400)
        // Display the current value in the dialog
        currentValueTextView.text = "Current Value: $currentValue"

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        // Set click listener for the Set button
        setButton.setOnClickListener {
            val newValueStr = newValueEditText.text.toString()
            if (newValueStr.isNotEmpty()) {
                val newValue = newValueStr.toInt()
                this.getSharedPreferences("sp",Context.MODE_PRIVATE).edit().putInt("thValue",newValue).apply()
                dialog.dismiss()
            } else {
                // Show a message or toast indicating that the input is empty
                Toast.makeText(this, "Please enter a value", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
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