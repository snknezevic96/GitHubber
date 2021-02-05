package com.futuradev.githubber.data.remote

import com.futuradev.githubber.data.model.Repository
import com.futuradev.githubber.data.model.retrofit.response.OrganizationsResponse
import com.futuradev.githubber.data.model.retrofit.response.SearchResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Git {

    @GET("repos/{owner}/{repo}")
    suspend fun getRepository(@Path("owner") owner: String,
                              @Path("repo") repository: String) : Repository

    @GET("search/repositories")
    suspend fun search(@Query("q") query: String) : SearchResponse

    @GET("users/{username}/orgs")
    suspend fun getUserOrganizations(@Path("username") user: String) : OrganizationsResponse
}