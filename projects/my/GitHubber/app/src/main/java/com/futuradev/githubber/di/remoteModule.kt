package com.futuradev.githubber.di

import com.futuradev.githubber.BuildConfig.*
import com.futuradev.githubber.data.local.DataPersistence
import com.futuradev.githubber.data.remote.Git
import com.futuradev.githubber.data.remote.GitService
import com.futuradev.githubber.data.remote.HeaderInterceptor
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val remoteModule = module {

    val url = "https://api.github.com/"

    fun provideGson(): Gson = GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
        .create()

    fun provideHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .addInterceptor(HeaderInterceptor())
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    fun provideRetrofit(gsonFactory: Gson, client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(GsonConverterFactory.create(gsonFactory))
        .client(client)
        .build()

    fun provideGitService(git: Git,
                          dataPersistence: DataPersistence) : GitService {
        return GitService(git, dataPersistence)
    }

    single { provideGson() }
    single { provideHttpClient() }
    single { provideRetrofit(get(), get()) }
    single { provideGitService(get(), get()) }

    single { get<Retrofit>().create(Git::class.java) }
}