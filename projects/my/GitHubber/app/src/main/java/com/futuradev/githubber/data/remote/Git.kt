package com.futuradev.githubber.data.remote

import com.futuradev.githubber.data.model.Owner
import com.futuradev.githubber.data.model.Repository
import com.futuradev.githubber.data.model.retrofit.response.*
import retrofit2.http.*

interface Git {

    @GET("repos/{owner}/{repo}")
    suspend fun getRepository(@Path("owner") owner: String,
                              @Path("repo") repository: String) : Repository

    @GET("search/repositories")
    suspend fun search(@Query("q") query: String) : SearchResponse

    @GET("users/{username}/orgs")
    suspend fun getUserOrganizations(@Path("username") user: String) : OrganizationsResponse

    @POST
    suspend fun requestVerificationCodes(@Url url: String) : VerificationCodesResponse

    @POST
    suspend fun getToken(@Url url: String) : TokenResponse

    @GET("user")
    suspend fun getUserData(@Header("Authorization") token : String) : UserResponse

    @GET("user/followers")
    suspend fun getFollowers(@Header("Authorization") token : String) : List<Owner>

    @GET("user/following")
    suspend fun getFollowing(@Header("Authorization") token : String) : List<Owner>
}