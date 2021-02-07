package com.futuradev.githubber.di

import com.futuradev.githubber.data.local.AppDatabase
import com.futuradev.githubber.data.local.DataPersistence
import com.futuradev.githubber.data.repository.GitRepository
import com.futuradev.githubber.ui.main.MainViewModel
import com.futuradev.githubber.ui.oauth.AuthorizationViewModel
import com.futuradev.githubber.ui.repository.RepositoryViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    fun provideRepositoryViewModel(gitRepository: GitRepository) : RepositoryViewModel {
        return RepositoryViewModel(gitRepository)
    }

    fun provideAuthorizationViewModel(gitRepository: GitRepository,
                                      database: AppDatabase,
                                      dataPersistence: DataPersistence) : AuthorizationViewModel {

        return AuthorizationViewModel(gitRepository,database, dataPersistence)
    }

    fun provideMainViewModel(gitRepository: GitRepository,
                             database: AppDatabase,
                             dataPersistence: DataPersistence) : MainViewModel {
        return MainViewModel(gitRepository, database, dataPersistence)
    }

    viewModel { provideRepositoryViewModel(get()) }
    viewModel { provideAuthorizationViewModel(get(), get(), get()) }
    viewModel { provideMainViewModel(get(), get(), get()) }
}