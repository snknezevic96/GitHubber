package com.futuradev.githubber.utils.listeners

import android.text.TextWatcher
import com.futuradev.githubber.utils.enum.SortType

interface ToolbarController {

    fun toolbarLogoClicked()

    fun getTextWatcher() : TextWatcher? = null

    fun sortBy(sortType: SortType)
}