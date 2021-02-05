package com.futuradev.githubber

import android.app.Application
import com.facebook.stetho.Stetho
import com.futuradev.githubber.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class GitHubber : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@GitHubber)

            modules(
                listOf(remoteModule, repositoryModule, viewModelModule)
            )

            Stetho.initializeWithDefaults(applicationContext)
        }
    }
}