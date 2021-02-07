package com.futuradev.githubber.utils.listeners

interface RepositoryListener {

    fun openOwnersProfile(profileUrl: String)

    fun openDetails(repositoryId: Int)
}