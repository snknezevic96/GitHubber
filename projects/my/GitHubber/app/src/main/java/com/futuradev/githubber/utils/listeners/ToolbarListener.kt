package com.futuradev.githubber.utils.listeners

interface ToolbarListener {

    fun setSearchVisibility(visibility: Int)

    fun requestSearchFocus()

    fun setLoginButtonVisibility(visibility: Int)
}