package com.futuradev.githubber.di

import com.futuradev.githubber.utils.AppConfig
import com.futuradev.githubber.utils.AppConfigImpl
import com.futuradev.githubber.utils.manager.KeyboardManager
import org.koin.dsl.module

val appModule = module {

    fun provideAppConfig() : AppConfig {
        return AppConfigImpl()
    }

    fun provideKeyboardManager() : KeyboardManager {
        return KeyboardManager()
    }

    single { provideAppConfig() }
    single { provideKeyboardManager() }
}