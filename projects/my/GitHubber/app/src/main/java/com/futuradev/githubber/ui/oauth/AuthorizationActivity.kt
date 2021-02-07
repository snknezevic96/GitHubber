package com.futuradev.githubber.ui.oauth

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.futuradev.githubber.R
import com.futuradev.githubber.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_authorization.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.lang.StringBuilder

class AuthorizationActivity : AppCompatActivity() {

    private val viewModel : AuthorizationViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authorization)

        setObservers()

        viewModel.checkVerificationCodes()
    }

    private fun setObservers() {

        viewModel.verificationCodes.observe(this, Observer {
            it ?: return@Observer

            setupWebView()

            web_view?.loadUrl(it.verificationUri)
        })

        viewModel.user.observe(this, Observer {
            it ?: return@Observer

            goToMainActivity(it.id)
        })
    }

    private fun goToMainActivity(userId: Int) {
        val intent = Intent(this, MainActivity::class.java)
        val bundle = Bundle().apply { putInt("userId", userId) }

        startActivity(intent.apply { putExtras(bundle) })
    }

    private fun setupWebView() {
        val webSettings = web_view.settings

        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.loadWithOverviewMode = false
        webSettings.useWideViewPort = true
        webSettings.setSupportZoom(true)
        webSettings.builtInZoomControls = false

        web_view.setPadding(0, 0, 0, 0)
        web_view.setInitialScale(1)

        if (Build.VERSION.SDK_INT >= 21) {
            // chromium, enable hardware acceleration
            web_view.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        } else {
            // older android version, disable hardware acceleration
            web_view.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        }
        web_view.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        web_view!!.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {

                return when(url) {
                    "https://github.com/login/device/success" -> {
                        viewModel.getToken()
                        false
                    }
                    else -> false
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                if(url == "https://github.com/login/device")
                    view?.loadUrl(getVerificationJavaScript())
            }
        }
    }

    private fun getVerificationJavaScript() : String {
        return StringBuilder("javascript: ").apply {
            viewModel.verificationCodes.value?.userCode?.forEachIndexed { index, c ->

                append("document.getElementById('user-code-$index').setAttribute('value', '$c'); ")
            }
        }.toString()
    }


}