package com.futuradev.githubber.utils.listeners

import android.text.TextWatcher

interface ToolbarListener {

    fun toolbarLogoClicked()

    fun getTextWatcher() : TextWatcher? = null
}