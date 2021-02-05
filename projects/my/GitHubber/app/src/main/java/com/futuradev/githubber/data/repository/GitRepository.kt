package com.futuradev.githubber.data.repository

import com.futuradev.githubber.data.model.Repository
import com.futuradev.githubber.data.model.retrofit.response.OrganizationsResponse
import com.futuradev.githubber.data.model.retrofit.response.SearchResponse
import com.futuradev.githubber.utils.ResultWrapper

interface GitRepository {

    suspend fun getRepository(owner: String, repository: String) : ResultWrapper<Repository>

    suspend fun search(query: String) : ResultWrapper<SearchResponse>

    suspend fun getUserOrganizations(user: String) : ResultWrapper<OrganizationsResponse>
}