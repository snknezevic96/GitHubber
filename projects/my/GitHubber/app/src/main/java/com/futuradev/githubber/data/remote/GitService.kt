package com.futuradev.githubber.data.remote

import com.futuradev.githubber.data.local.DataPersistence
import com.futuradev.githubber.utils.safeApiCall
import kotlinx.coroutines.Dispatchers

class GitService(private val api: Git,
                 private val dataPersistence: DataPersistence) {

    suspend fun getRepository(owner: String, repository: String) = safeApiCall(Dispatchers.IO) {
        api.getRepository(owner, repository)
    }

    suspend fun search(query: String) = safeApiCall(Dispatchers.IO) {
        api.search(query)
    }

    suspend fun getUserOrganizations(user: String) = safeApiCall(Dispatchers.IO) {
        api.getUserOrganizations(user)
    }

    suspend fun requestVerificationCodes() = safeApiCall(Dispatchers.IO) {
        val urlBase = "https://github.com/login/device/code"
        val clientId = "8e2fb386227ffc128ffb"

        api.requestVerificationCodes(
            url = "$urlBase?client_id=$clientId"
        )
    }

    suspend fun getToken(deviceCode: String) = safeApiCall(Dispatchers.IO) {
        val urlBase = "https://github.com/login/oauth/access_token"
        val clientId = "8e2fb386227ffc128ffb"
        val grantType = "urn:ietf:params:oauth:grant-type:device_code"

        api.getToken(
            url = "$urlBase?client_id=$clientId&device_code=$deviceCode&grant_type=$grantType")
    }

    suspend fun getUserData(token: String) = safeApiCall(Dispatchers.IO) {
        api.getUserData("token $token")
    }

    suspend fun getFollowers() = safeApiCall(Dispatchers.IO) {
        api.getFollowers("token ${dataPersistence.userToken}")
    }

    suspend fun getFollowing() = safeApiCall(Dispatchers.IO) {
        api.getFollowing("token ${dataPersistence.userToken}")
    }

}