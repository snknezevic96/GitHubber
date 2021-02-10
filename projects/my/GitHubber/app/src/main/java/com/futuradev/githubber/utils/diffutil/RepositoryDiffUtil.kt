package com.futuradev.githubber.utils.diffutil

import androidx.recyclerview.widget.DiffUtil
import com.futuradev.githubber.data.model.Repository

class RepositoryDiffUtil(
    private val oldRepositories: Array<Repository>,
    private val newRepositories: Array<Repository>) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldRepositories[oldItemPosition].id == newRepositories[newItemPosition].id
    }

    override fun getOldListSize(): Int {
        return oldRepositories.size
    }

    override fun getNewListSize(): Int {
        return newRepositories.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldRepositories[oldItemPosition].id == newRepositories[newItemPosition].id
    }
}