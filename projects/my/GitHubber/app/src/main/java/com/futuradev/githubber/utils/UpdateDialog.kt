package com.futuradev.githubber.utils

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
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
import com.futuradev.githubber.utils.wrapper.DownloadState
import kotlinx.android.synthetic.main.dialog_update.*
import java.io.File

class UpdateDialog : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_update, container, false)
    }

    var listener: UpdateListener? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        btnUpdate.setOnClickListener {
            startUpdate()
        }

        btnClose.setOnClickListener {
            dismiss()
        }

        setTranslations()

        DownloadHelper.downloadState.observe(viewLifecycleOwner, Observer {
            when (it) {
                is DownloadState.InProgress -> {
                    progress.text = String.format(resources.getString(R.string.download_progress), "Downloaded", it.progress)
                    progressBar.progress = it.progress
                }
                is DownloadState.Failure ->
                    listener?.updateError()
                is DownloadState.Success ->
                    startInstall(it.file)
            }
        })
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun setTranslations() {
        btnUpdate.text = "Download"
        btnClose.text = "Cancel"
        title.text = "Download premium app"
    }

    private fun startUpdate() {
        buttonContainer.visibility = View.GONE
        progress.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE

        val oneTimeWorkRequest = OneTimeWorkRequest.Builder(DownloadWorker::class.java).build()
        WorkManager.getInstance(requireContext()).enqueue(oneTimeWorkRequest)
    }

    private fun startInstall(file: File) {
        val apkUri = FileProvider.getUriForFile(
            requireContext(),
            BuildConfig.APPLICATION_ID + ".provider",
            file
        )
        val intent = Intent(Intent.ACTION_INSTALL_PACKAGE)
            .setDataAndType(apkUri, "application/vnd.android.package-archive")
            .setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        requireContext().startActivity(intent)
        listener?.updateFinished()
    }

    companion object {
        fun newInstance(): UpdateDialog {

            val dialog = UpdateDialog()
            dialog.isCancelable = false
            return dialog
        }
    }

    interface UpdateListener {
        fun dismissUpdate(isMandatory: Boolean)
        fun updateFinished()
        fun updateError()
    }
}