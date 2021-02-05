package com.futuradev.githubber.di

import com.futuradev.githubber.data.remote.GitService
import com.futuradev.githubber.data.repository.GitRepository
import com.futuradev.githubber.data.repository.GitRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {

    fun provideRepository(gitService: GitService) : GitRepository {

        return GitRepositoryImpl(gitService)
    }

    single { provideRepository(get()) }
}