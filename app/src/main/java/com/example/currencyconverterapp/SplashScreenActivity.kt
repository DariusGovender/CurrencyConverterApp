package com.example.currencyconverterapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        // Delay for 3 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            // Start MainActivity after the delay
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            // Finish SplashScreenActivity so the user cannot return to it
            finish()
        }, 3000) // 3000 milliseconds = 3 seconds
    }
}