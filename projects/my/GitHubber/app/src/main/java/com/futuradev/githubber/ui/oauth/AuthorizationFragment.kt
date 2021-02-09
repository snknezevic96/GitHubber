package com.futuradev.githubber.ui.oauth

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.futuradev.githubber.R
import com.futuradev.githubber.utils.listeners.ToolbarListener
import kotlinx.android.synthetic.main.fragment_authorization.*
import kotlinx.coroutines.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.lang.StringBuilder
import kotlin.coroutines.CoroutineContext

class AuthorizationFragment(override val coroutineContext: CoroutineContext = Dispatchers.Main) : Fragment(), CoroutineScope {

    private val viewModel : AuthorizationViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_authorization, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        customizeToolbar()
        setObservers()

        viewModel.checkVerificationCodes()
    }

    private fun setObservers() {

        viewModel.verificationCodes.observe(viewLifecycleOwner, Observer {
            it ?: return@Observer

            setupWebView()

            web_view?.loadUrl(it.verificationUri)
        })

        viewModel.user.observe(viewLifecycleOwner, Observer {
            it ?: return@Observer

            findNavController().popBackStack()
        })
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

                if(url == "https://github.com/login/device") {
                    launch(coroutineContext) {
                        progress.visibility = View.VISIBLE
                        delay(1500)
                        view?.loadUrl(getVerificationJavaScript())
                        progress.visibility = View.GONE
                    }
                }
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

    private fun customizeToolbar() {
        (activity as ToolbarListener).apply {
            setSearchVisibility(View.GONE)
            setLoginButtonVisibility(View.GONE)
        }

    }
}