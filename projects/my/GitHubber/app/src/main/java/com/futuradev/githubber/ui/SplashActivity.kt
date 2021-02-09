package com.futuradev.githubber.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.futuradev.githubber.R
import com.futuradev.githubber.ui.main.MainActivity
import com.futuradev.githubber.utils.navigateTo

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        runHandler()
    }

    private fun runHandler() {
        Handler().postDelayed({
            navigateTo(MainActivity::class.java)
        }, 2000)
    }
}