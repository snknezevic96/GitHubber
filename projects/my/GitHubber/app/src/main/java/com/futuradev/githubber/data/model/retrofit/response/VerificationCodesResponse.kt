package com.futuradev.githubber.data.model.retrofit.response

import com.google.gson.annotations.SerializedName

data class VerificationCodesResponse(
    @SerializedName("device_code")
    val deviceCode: String,
    @SerializedName("expires_in")
    val expiresIn: Int,
    @SerializedName("interval")
    val interval: Int,
    @SerializedName("user_code")
    val userCode: String,
    @SerializedName("verification_uri")
    val verificationUri: String
)