package com.futuradev.githubber.data.repository

import com.futuradev.githubber.data.model.Owner
import com.futuradev.githubber.data.model.Repository
import com.futuradev.githubber.data.model.entity.User
import com.futuradev.githubber.data.model.retrofit.response.*
import com.futuradev.githubber.utils.wrapper.ResultWrapper

interface GitRepository {

    suspend fun getRepository(owner: String, repository: String) : ResultWrapper<Repository>

    suspend fun search(query: String) : ResultWrapper<SearchResponse>

    suspend fun getUserOrganizations(user: String) : ResultWrapper<OrganizationsResponse>

    suspend fun getVerificationCodes() : ResultWrapper<VerificationCodesResponse>

    suspend fun getToken(deviceCode: String) : ResultWrapper<TokenResponse>

    suspend fun getUserData(token: String) : ResultWrapper<User>

    suspend fun getFollowers() : ResultWrapper<List<Owner>>

    suspend fun getFollowing() : ResultWrapper<List<Owner>>
}