package com.futuradev.githubber.data.model.retrofit.response

data class TokenResponse(
    val access_token: String,
    val scope: String,
    val token_type: String
) {
    constructor(access_token: String) : this(access_token, "", "")
}