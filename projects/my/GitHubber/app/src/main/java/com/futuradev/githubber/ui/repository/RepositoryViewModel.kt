package com.futuradev.githubber.ui.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.futuradev.githubber.data.model.Organization
import com.futuradev.githubber.data.model.Repository
import com.futuradev.githubber.data.repository.GitRepository
import com.futuradev.githubber.utils.getResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RepositoryViewModel(private val gitRepository: GitRepository) : ViewModel() {

    val repository  = MutableLiveData<Repository>()
    val repositories = MutableLiveData<List<Repository>?>()
    val errorMessage = MutableLiveData<String>()

    val userOrganizations = MutableLiveData<List<Organization>>()

    private fun List<Repository>.findRepository(repositoryId: Int) : Repository? = find { it.id == repositoryId }

    fun findRepository(repositoryId: Int) = viewModelScope.launch(Dispatchers.IO) {
        repositories.value
            ?.findRepository(repositoryId)
            ?.let { repository.postValue(it) }
    }

    fun search(query: String) = viewModelScope.launch(Dispatchers.IO) {

        gitRepository.search(query).getResult(
            success = {
                      repositories.postValue(it.items)
            },
            genericError = { code, message ->
                when(code) {
                    403 -> {
                        errorMessage.postValue("Sorry, but GitHub says no more searching.\nToo many API requests :/")
                    }
                    else -> {
                        errorMessage.postValue("Oops, some error occurred.")
                    }
                }
                Log.d("REPOSITORY", "ERROR_CODE=$code;\nmessage=$message")
            },
            networkError = {
                errorMessage.postValue("Network error.\nPlease check internet connection.")
                Log.d("REPOSITORY", "NETWORK ERROR")
            }
        )
    }

    fun getUserOrganizations(user: String) = viewModelScope.launch(Dispatchers.IO) {

        gitRepository.getUserOrganizations(user).getResult(
            success = {
                userOrganizations.postValue(it)
            },
            genericError = { code, message ->
                when(code) {
                    403 -> {
                        errorMessage.postValue("Sorry, but GitHub says no more searching.\nToo many API requests :/")
                    }
                    else -> {
                        errorMessage.postValue("Oops, some error occurred.")
                    }
                }
                Log.d("REPOSITORY", "ERROR_CODE=$code; /n message=$message")
            },
            networkError = {
                errorMessage.postValue("Network error.\nPlease check internet connection.")
                Log.d("REPOSITORY", "NETWORK ERROR")
            }
        )
    }

}