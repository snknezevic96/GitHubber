package com.futuradev.githubber.ui.custom

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.futuradev.githubber.BuildConfig
import com.futuradev.githubber.R
import com.futuradev.githubber.utils.download.DownloadHelper
import com.futuradev.githubber.utils.download.DownloadWorker
import com.futuradev.githubber.utils.listeners.DownloadListener
import com.futuradev.githubber.utils.wrapper.DownloadState
import kotlinx.android.synthetic.main.dialog_update.*
import java.io.File

class DownloadDialog() : DialogFragment() {

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_update, container, false)
    }

    var listener: DownloadListener? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        bindData()

        setOnClickListeners()
        setObserver()
    }

    private fun bindData() {
        download.text = resources.getString(R.string.download_dialog_download_button)
        cancel.text = resources.getString(R.string.download_dialog_cancel_button)
        title.text = resources.getString(R.string.download_dialog_title)
    }

    private fun setOnClickListeners() {
        download.setOnClickListener {
            startDownload()
        }
        cancel.setOnClickListener {
            dismiss()
        }
    }

    private fun setObserver() {

        DownloadHelper.downloadState.observe(viewLifecycleOwner, Observer {
            it ?: return@Observer

            when (it) {
                is DownloadState.InProgress -> {
                    progress.progress = it.progress
                    progress_text.text = it.progress.formatProgress()
                }
                is DownloadState.Failure -> listener?.downloadError()
                is DownloadState.Success -> startInstall(it.file)
            }
        })
    }

    private fun Int.formatProgress() : String {
        return String.format(
            resources.getString(R.string.download_dialog_progress), resources.getString(
                R.string.downlaod_dialog_progress_text
            ), this
        )
    }

    private fun startDownload() {
        buttonContainer.visibility = View.GONE
        progress_text.visibility = View.VISIBLE
        progress.visibility = View.VISIBLE

        startWorker()
    }

    private fun startWorker() {
        val oneTimeWorkRequest = OneTimeWorkRequest.Builder(DownloadWorker::class.java).build()
        WorkManager.getInstance(requireContext()).enqueue(oneTimeWorkRequest)
    }

    private fun startInstall(file: File) {
        val type = "application/vnd.android.package-archive"
        val authority = "${BuildConfig.APPLICATION_ID}.provider"

        context.apply {
            this ?: return@apply

            val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val apk = FileProvider.getUriForFile(this, authority, file)

                Intent(Intent.ACTION_INSTALL_PACKAGE)
                    .setDataAndType(apk, type)
                    .setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP )
            } else {
                Intent(Intent.ACTION_VIEW)
                    .setDataAndType(Uri.fromFile(file), type)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }

            listener?.downloadFinished()
            startActivity(intent)
        }
    }


    companion object {
        fun newInstance(): DownloadDialog {

            val dialog = DownloadDialog()
            dialog.isCancelable = false
            return dialog
        }
    }
}