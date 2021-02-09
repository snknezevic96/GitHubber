package com.futuradev.githubber.utils

interface AppConfig {

    fun isPremiumVersion(action: () -> Unit)
}