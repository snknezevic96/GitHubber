package com.futuradev.githubber.utils

import android.content.Context
import android.os.Environment
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.futuradev.githubber.utils.wrapper.DownloadState
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class DownloadWorker(val context: Context, params: WorkerParameters) : Worker(context, params) {

    companion object {
        const val APK_NAME = "githubber_paid.apk"
    }

    override fun doWork(): Result {
        try {

            val url = URL("https://www.dropbox.com/s/x68iolwz3nzn3tu/GitHubber_v1.0.0rc1.apk?dl=0&raw=1")
            val connection = url.openConnection()
            connection.connect()
            val fileSize = connection.contentLength
            val inputStream = connection.getInputStream()
            val buffer = ByteArray(1024)
            val file = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            val outputFile = File(file, APK_NAME)
            var len = 0
            var total = 0L
            val fos = FileOutputStream(outputFile)

            len = inputStream.read(buffer)
            while (len != -1) {
                fos.write(buffer, 0, len)
                total += len
                val percent = ((total * 100) / fileSize).toInt()
                DownloadHelper.downloadState.postValue(DownloadState.InProgress(percent))

                len = inputStream.read(buffer)
            }

            fos.close()
            inputStream.close()
            DownloadHelper.downloadState.postValue(DownloadState.Success(outputFile))
        } catch (e: Exception) {
            DownloadHelper.downloadState.postValue(DownloadState.Failure)
            return Result.failure()
        }

        return Result.success()
    }
}