package com.futuradev.githubber.utils.wrapper

import java.io.File

sealed class DownloadState {
    data class Success(val file: File) : DownloadState()
    object Failure : DownloadState()
    data class InProgress(val progress: Int): DownloadState()
}
