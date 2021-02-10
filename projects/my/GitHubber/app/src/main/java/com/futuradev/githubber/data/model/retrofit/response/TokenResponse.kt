package com.futuradev.githubber.data.model.retrofit.response

import com.google.gson.annotations.SerializedName

data class TokenResponse(
    @SerializedName("access_token")
    val token: String,
    @SerializedName("scope")
    val scope: String,
    @SerializedName("token_type")
    val tokenType: String
)