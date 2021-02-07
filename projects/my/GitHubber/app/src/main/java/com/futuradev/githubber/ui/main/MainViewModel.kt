package com.futuradev.githubber.ui.main

import android.os.Handler
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.futuradev.githubber.data.local.AppDatabase
import com.futuradev.githubber.data.local.DataPersistence
import com.futuradev.githubber.data.model.Owner
import com.futuradev.githubber.data.model.retrofit.response.UserResponse
import com.futuradev.githubber.data.repository.GitRepository
import com.futuradev.githubber.utils.getResult
import com.futuradev.githubber.utils.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(private val gitRepository: GitRepository,
                    private val database: AppDatabase,
                    private val dataPersistence: DataPersistence) : ViewModel() {

    private var timeToCloseApp = false

    val user = MutableLiveData<UserResponse>()
    val followers = MutableLiveData<List<Owner>>()
    val following = MutableLiveData<List<Owner>>()

    fun getUser(userId: Int) = viewModelScope.launch(Dispatchers.IO) {
        database.userDao().getUser(userId)?.let {
            user.postValue(it)

            getFollowers()
        }
    }

    private suspend fun getFollowers() = viewModelScope.launch(Dispatchers.IO) {
        gitRepository.getFollowers().getResult(
            success = {
                followers.postValue(it)
            },
            genericError = { code, message ->
                log("GIT GENERIC ERROR $code - get followers; message=$message")
            },
            networkError = {
                log("GIT NETWORK ERROR - get followers")
            }
        )

        gitRepository.getFollowing().getResult(
            success = {
                following.postValue(it)
            },
            genericError = { code, message ->
                log("GIT GENERIC ERROR $code - get followers; message=$message")
            },
            networkError = {
                log("GIT NETWORK ERROR - get followers")
            }
        )
    }

    fun appClosing(closeApp: () -> Unit,
                   showMessage: () -> Unit) {

        if (timeToCloseApp)
            closeApp()
        else {
            timeToCloseApp = true

            Handler().postDelayed( { timeToCloseApp = false }, 2000)

            showMessage()
        }
    }
}