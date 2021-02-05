package com.futuradev.githubber.utils

interface RepositoryListener {

    fun openOwnersProfile(profileUrl: String)

    fun openDetails(repositoryId: Int)
}