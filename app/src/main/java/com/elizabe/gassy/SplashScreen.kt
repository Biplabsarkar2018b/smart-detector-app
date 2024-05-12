package com.elizabe.gassy

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*

class SplashScreen : AppCompatActivity() {
    private lateinit var ipEditText: EditText
    private lateinit var ipAdd : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        ipEditText = findViewById(R.id.ip_edit_text)
        val confirmButton: Button = findViewById(R.id.confirm_button)

        confirmButton.setOnClickListener {
            val ipAddress = ipEditText.text.toString()
            if (isValidIpAddress(ipAddress)) {
                saveIpAddress(ipAddress)
                navigateToNextActivity()
            } else {
                Toast.makeText(this,"Wrong IP!",Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun isValidIpAddress(ipAddress: String): Boolean {
        // Use a regular expression to validate the IP address format
        val ipRegex = ("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}"
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$")
        return ipAddress.matches(ipRegex.toRegex())
    }

    private fun saveIpAddress(ipAddress: String) {
        val sp = this.getSharedPreferences("sp",Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString("ip",ipAddress)
        editor.apply()
        ipAdd = ipAddress
    }

    private fun navigateToNextActivity() {
        // Start the next activity
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("ip",ipAdd)
        startActivity(intent)
        finish()
    }
}