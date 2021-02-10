package com.futuradev.githubber.data.repository


import com.futuradev.githubber.data.model.Organization
import com.futuradev.githubber.data.model.Owner
import com.futuradev.githubber.data.model.Repository
import com.futuradev.githubber.data.model.entity.User
import com.futuradev.githubber.data.model.retrofit.response.*
import com.futuradev.githubber.data.remote.GitService
import com.futuradev.githubber.utils.wrapper.ResultWrapper

class GitRepositoryImpl(private val gitService: GitService) : GitRepository {

    override suspend fun getRepository(owner: String, repository: String) : ResultWrapper<Repository> =
        gitService.getRepository(owner, repository)

    override suspend fun search(query: String) : ResultWrapper<SearchResponse> =
        gitService.search(query)

    override suspend fun getUserOrganizations(user: String): ResultWrapper<List<Organization>> =
        gitService.getUserOrganizations(user)

    override suspend fun getVerificationCodes(): ResultWrapper<VerificationCodesResponse> =
        gitService.requestVerificationCodes()

    override suspend fun getToken(deviceCode: String): ResultWrapper<TokenResponse> =
        gitService.getToken(deviceCode)

    override suspend fun getUserData(token: String): ResultWrapper<User> =
        gitService.getUserData(token)

    override suspend fun getFollowers(): ResultWrapper<List<Owner>> =
        gitService.getFollowers()

    override suspend fun getFollowing(): ResultWrapper<List<Owner>> =
        gitService.getFollowing()


}