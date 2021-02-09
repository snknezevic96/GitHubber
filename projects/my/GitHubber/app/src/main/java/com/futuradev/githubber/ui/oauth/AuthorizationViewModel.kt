package com.futuradev.githubber.ui.oauth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.futuradev.githubber.data.local.AppDatabase
import com.futuradev.githubber.data.local.DataPersistence
import com.futuradev.githubber.data.model.entity.User
import com.futuradev.githubber.data.model.retrofit.response.VerificationCodesResponse
import com.futuradev.githubber.data.repository.GitRepository
import com.futuradev.githubber.utils.getResult
import com.futuradev.githubber.utils.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthorizationViewModel(private val gitRepository: GitRepository,
                             private val database: AppDatabase,
                             private val dataPersistence: DataPersistence) : ViewModel() {

    val verificationCodes = MutableLiveData<VerificationCodesResponse>()
    val user = MutableLiveData<User>()

    private suspend fun getUserData(token: String) {

        gitRepository.getUserData(token).getResult(
            success = {
                dataPersistence.userId = it.id
                database.userDao().insert(it)
                user.postValue(it)
                log("GIT SUCCESS - user data")
            },
            genericError = { code, message ->
                log("GIT GENERIC ERROR $code - get user data; message=$message")
            },
            networkError = {
                log("GIT NETWORK ERROR - get user data")
            }
        )
    }

    fun checkVerificationCodes() = viewModelScope.launch(Dispatchers.IO) {

        dataPersistence.userToken.apply {
            if(isNotEmpty())
                getUserData(this)
            else
                requestVerificationCodes()
        }
    }

    private fun requestVerificationCodes() = viewModelScope.launch(Dispatchers.IO) {

        gitRepository.getVerificationCodes().getResult(
            success = {
                dataPersistence.apply {
                    userCode = it.userCode
                    deviceCode = it.deviceCode
                }
                verificationCodes.postValue(it)
                log("GIT SUCCESS - get verification code")
                log("verification_response=$it")
            },
            genericError = { code, message ->
                log("GIT GENERIC ERROR $code - get verification code; message=$message")
            },
            networkError = {
                log("GIT NETWORK ERROR - get verification code")
            }
        )
    }

    fun getToken() = viewModelScope.launch(Dispatchers.IO) {

        verificationCodes.value?.deviceCode?.let {
            gitRepository.getToken(it).getResult(
                success = {
                    launch { getUserData(it.access_token) }
                    dataPersistence.userToken = it.access_token

                    log("GIT SUCCESS - get token")
                    log("token_response=$it")
                },
                genericError = { code, message ->
                    log("GIT GENERIC ERROR $code - get token; message=$message")
                },
                networkError = {
                    log("GIT NETWORK ERROR - get token")
                }
            )
        }
    }
}