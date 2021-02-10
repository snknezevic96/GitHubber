package com.futuradev.githubber.data.model.retrofit.response

import com.futuradev.githubber.data.model.Repository
import com.google.gson.annotations.SerializedName

data class SearchResponse(
    @SerializedName("incomplete_results")
    val incompleteResults: Boolean,
    @SerializedName("items")
    val items: List<Repository>?,
    @SerializedName("total_count")
    val totalCount: Int
)