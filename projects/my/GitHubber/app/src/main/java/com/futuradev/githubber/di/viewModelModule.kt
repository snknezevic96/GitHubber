package com.futuradev.githubber.di

import com.futuradev.githubber.ui.repository.RepositoryViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel { RepositoryViewModel(get()) }
}