package com.futuradev.githubber.ui.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.futuradev.githubber.data.model.Organization
import com.futuradev.githubber.data.model.Repository
import com.futuradev.githubber.data.repository.GitRepository
import com.futuradev.githubber.utils.enum.SortType
import com.futuradev.githubber.utils.getResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RepositoryViewModel(private val gitRepository: GitRepository) : ViewModel() {

    val repository  = MutableLiveData<Repository>()
    val repositories = MutableLiveData<Array<Repository>?>()
    val errorMessage = MutableLiveData<String>()
    val userOrganizations = MutableLiveData<List<Organization>>()

    private var sortType : SortType? = null

    private fun Array<Repository>.findRepository(repositoryId: Int) : Repository? = find { it.id == repositoryId }

    fun findRepository(repositoryId: Int) = viewModelScope.launch(Dispatchers.IO) {
        repositories.value
            ?.findRepository(repositoryId)
            ?.let { repository.postValue(it) }
    }

    private fun postRepositories(sortType: SortType?, list: Array<Repository>?) {

        repositories.postValue(list?.apply {
            when(sortType) {
                SortType.STARS -> sortByDescending { it.stargazers_count }
                SortType.FORKS -> sortByDescending { it.forks_count }
                SortType.UPDATED -> sortByDescending { it.updated_at }
            }
        })
    }

    fun sortBy(sortType: SortType) = viewModelScope.launch(Dispatchers.IO) {
        this@RepositoryViewModel.sortType = sortType

        postRepositories(sortType, repositories.value)
    }

    fun search(query: String) = viewModelScope.launch(Dispatchers.IO) {

        gitRepository.search(query).getResult(
            success = {
                      postRepositories(sortType, it.items.toTypedArray())
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