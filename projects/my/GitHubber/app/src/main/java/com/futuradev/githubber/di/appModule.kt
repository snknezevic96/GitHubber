package com.futuradev.githubber.di

import com.futuradev.githubber.utils.manager.KeyboardManager
import org.koin.dsl.module

val appModule = module {

    single { KeyboardManager() }
}