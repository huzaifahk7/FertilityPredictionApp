package com.example.LifeSeed

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        window.statusBarColor = Color.TRANSPARENT

        // Navigate to DOBActivity after splash screen
        Handler(Looper.getMainLooper()).postDelayed({
            android.util.Log.d("MainActivity", "Navigating to DOBActivity")
            startActivity(Intent(this, DOBActivity::class.java)) // Navigate to DOBActivity
            finish() // Close the MainActivity
        }, 3000)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.imageView2)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


    }
    }

