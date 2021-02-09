package com.futuradev.githubber.data.model.retrofit.response

import com.futuradev.githubber.data.model.Repository

data class SearchResponse(
    val incomplete_results: Boolean,
    val items: List<Repository>?,
    val total_count: Int
)