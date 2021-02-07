package com.futuradev.githubber.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.futuradev.githubber.data.local.AppDatabase
import com.futuradev.githubber.data.local.DataPersistence
import org.koin.dsl.module

val databaseModule = module {

    fun provideDataPersistence(context: Context) : DataPersistence {
        return DataPersistence(context)
    }

    fun provideAppDatabase(application: Application): AppDatabase {
        val builder = Room.databaseBuilder(
            application, AppDatabase::class.java,
            "GitHubber_DB"
        ).fallbackToDestructiveMigration()

        return builder.build()
    }

    single { provideDataPersistence(get()) }

    single { provideAppDatabase(get()) }
}