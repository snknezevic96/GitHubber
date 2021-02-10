package com.futuradev.githubber.ui.oauth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.futuradev.githubber.R
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
    val errorMessage = MutableLiveData<Int>()
    val user = MutableLiveData<User>()

    private suspend fun getUserData(token: String) {

        gitRepository.getUserData(token).getResult(
            success = {
                dataPersistence.userId = it.id
                database.userDao().insert(it)
                user.postValue(it)
            },
            genericError = { code, message ->
                errorMessage.postValue(R.string.error_generic)
            },
            networkError = {
                errorMessage.postValue(R.string.error_network)
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
            },
            genericError = { code, message ->
                errorMessage.postValue(R.string.error_generic)
            },
            networkError = {
                errorMessage.postValue(R.string.error_network)
            }
        )
    }

    fun getToken() = viewModelScope.launch(Dispatchers.IO) {

        verificationCodes.value?.deviceCode?.let {
            gitRepository.getToken(it).getResult(
                success = {
                    launch { getUserData(it.token) }
                    dataPersistence.userToken = it.token
                },
                genericError = { code, message ->
                    errorMessage.postValue(R.string.error_generic)
                },
                networkError = {
                    errorMessage.postValue(R.string.error_network)
                }
            )
        }
    }
}