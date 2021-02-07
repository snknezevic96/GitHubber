package com.futuradev.githubber.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.futuradev.githubber.R
import com.futuradev.githubber.ui.oauth.AuthorizationActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        runHandler()
    }

    private fun runHandler() {
        Handler().postDelayed({
            startMainActivity()
        }, 2000)
    }

    private fun startMainActivity() {
        val intent = Intent(this, AuthorizationActivity::class.java)
        startActivity(intent)
    }
}