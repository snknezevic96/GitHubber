package com.futuradev.githubber.ui.repository

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.futuradev.githubber.R
import com.futuradev.githubber.data.model.Organization
import com.futuradev.githubber.data.model.Repository
import com.futuradev.githubber.data.repository.GitRepository
import com.futuradev.githubber.utils.enum.SortType
import com.futuradev.githubber.utils.getResult
import com.futuradev.githubber.utils.wrapper.ViewWrapper
import com.futuradev.githubber.utils.wrapper.ViewWrapper.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RepositoryViewModel(private val gitRepository: GitRepository) : ViewModel() {

    val repository  = MutableLiveData<Repository>()
    var repositories : Array<Repository>? = null
    val repositoriesLive = MutableLiveData<ViewWrapper<Array<Repository>>>()
    val userOrganizationsLive = MutableLiveData<ViewWrapper<List<Organization>>>()

    private var sortType : SortType? = null

    private fun Array<Repository>.findRepository(repositoryId: Int) : Repository? = find { it.id == repositoryId }

    fun findRepository(repositoryId: Int) = viewModelScope.launch(Dispatchers.IO) {
        repositories
            ?.findRepository(repositoryId)
            ?.let { repository.postValue(it) }
    }

    private fun postRepositories(sortType: SortType?, list: Array<Repository>?) {
        val updated = list?.apply {
            when(sortType) {
                SortType.STARS -> sortByDescending { it.stargazers_count }
                SortType.FORKS -> sortByDescending { it.forks_count }
                SortType.UPDATED -> sortByDescending { it.updated_at }
            }
        } ?: emptyArray()

        repositoriesLive.postValue(Success(updated))
    }

    fun sortBy(sortType: SortType) = viewModelScope.launch(Dispatchers.IO) {
        this@RepositoryViewModel.sortType = sortType

        postRepositories(sortType, repositories)
    }

    fun search(query: String) = viewModelScope.launch(Dispatchers.IO) {
        repositoriesLive.postValue(InProgress)

        gitRepository.search(query).getResult(
            success = {
                repositories = it.items?.toTypedArray()
                postRepositories(sortType, repositories)
            },
            genericError = { code, message ->
                when(code) {
                    403 -> repositoriesLive.postValue(Failure(R.string.error_no_more_searching))
                    else -> repositoriesLive.postValue(Failure(R.string.error_generic))
                }
            },
            networkError = {
                repositoriesLive.postValue(Failure(R.string.error_network))
            }
        )
    }

    fun getUserOrganizations(user: String) = viewModelScope.launch(Dispatchers.IO) {
        userOrganizationsLive.postValue(InProgress)

        gitRepository.getUserOrganizations(user).getResult(
            success = {
                userOrganizationsLive.postValue(Success(it))
            },
            genericError = { code, message ->
                when(code) {
                    403 -> userOrganizationsLive.postValue(Failure(R.string.error_no_more_searching))
                    else -> userOrganizationsLive.postValue(Failure(R.string.error_generic))
                }
            },
            networkError = {
                userOrganizationsLive.postValue(Failure(R.string.error_network))
            }
        )
    }

}