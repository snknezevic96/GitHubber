package com.futuradev.githubber.data.repository


import com.futuradev.githubber.data.model.Repository
import com.futuradev.githubber.data.model.retrofit.response.*
import com.futuradev.githubber.data.remote.GitService
import com.futuradev.githubber.utils.ResultWrapper

class GitRepositoryImpl(private val gitService: GitService) : GitRepository {

    override suspend fun getRepository(owner: String, repository: String) : ResultWrapper<Repository> =
        gitService.getRepository(owner, repository)

    override suspend fun search(query: String) : ResultWrapper<SearchResponse> =
        gitService.search(query)

    override suspend fun getUserOrganizations(user: String): ResultWrapper<OrganizationsResponse> =
        gitService.getUserOrganizations(user)

}