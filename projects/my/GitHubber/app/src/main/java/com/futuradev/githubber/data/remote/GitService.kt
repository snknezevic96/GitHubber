package com.futuradev.githubber.data.remote

import com.futuradev.githubber.utils.safeApiCall
import kotlinx.coroutines.Dispatchers

class GitService(private val api: Git) {

    suspend fun getRepository(owner: String, repository: String) = safeApiCall(Dispatchers.IO) {
        api.getRepository(owner, repository)
    }

    suspend fun search(query: String) = safeApiCall(Dispatchers.IO) {
        api.search(query)
    }

    suspend fun getUserOrganizations(user: String) = safeApiCall(Dispatchers.IO) {
        api.getUserOrganizations(user)
    }
}