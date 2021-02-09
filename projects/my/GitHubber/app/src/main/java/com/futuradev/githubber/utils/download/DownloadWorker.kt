package com.futuradev.githubber.utils.download

import android.content.Context
import android.os.Environment
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.futuradev.githubber.utils.wrapper.DownloadState
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URL

class DownloadWorker(val context: Context,
                     params: WorkerParameters) : Worker(context, params) {

    companion object {
        const val APK_NAME = "githubber_paid.apk"
    }

    private val url = URL("https://www.dropbox.com/s/x68iolwz3nzn3tu/GitHubber_v1.0.0rc1.apk?dl=0&raw=1")
    private val fileDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
    private val buffer = ByteArray(1024)

    override fun doWork(): Result {
        return try {
            val connection = url.openConnection().apply { connect() }

            val file = File(fileDir, APK_NAME)
            val fileSize = connection.contentLength

            connection
                .getInputStream()
                .download(file, fileSize)
                .also {
                    DownloadHelper.downloadState.postValue(DownloadState.Success(it))
                }
            Result.success()
        } catch (e: Exception) {
            DownloadHelper.downloadState.postValue(DownloadState.Failure)
            Result.failure()
        }
    }

    private fun InputStream.download(file : File, fileSize: Int) : File {
        var totalBytes = read(buffer)
        var downloadedSize = 0L

        val output = FileOutputStream(file)

        while (totalBytes != -1) {
            output.write(buffer, 0, totalBytes)
            downloadedSize += totalBytes

            val percent = ((downloadedSize * 100) / fileSize).toInt()

            DownloadHelper.downloadState.postValue(DownloadState.InProgress(percent))

            totalBytes = read(buffer)
        }
        output.close()
        close()

        return file
    }
}