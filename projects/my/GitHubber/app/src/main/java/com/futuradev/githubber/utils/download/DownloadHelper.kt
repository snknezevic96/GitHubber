package com.futuradev.githubber.utils.download

import androidx.lifecycle.MutableLiveData
import com.futuradev.githubber.utils.wrapper.DownloadState

object DownloadHelper {
    val downloadState: MutableLiveData<DownloadState> = MutableLiveData()
}