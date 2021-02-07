package com.futuradev.githubber.ui.repository.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.futuradev.githubber.R
import com.futuradev.githubber.data.model.Repository
import com.futuradev.githubber.utils.diffutil.RepositoryDiffUtil
import com.futuradev.githubber.utils.enum.SortType
import com.futuradev.githubber.utils.listeners.RepositoryListener
import kotlinx.android.synthetic.main.repository_recycler_item.view.*

class RepositoryListAdapter : RecyclerView.Adapter<RepositoryListAdapter.ViewHolder>() {

    private val repositories = arrayListOf<Repository>()
    private var context : Context? = null
    private var itemListener : RepositoryListener? = null

    fun setListener(itemListener: RepositoryListener) {
        this.itemListener = itemListener
    }

    fun refreshData(newData: List<Repository>) {
        val diffResult = DiffUtil.calculateDiff(
            RepositoryDiffUtil(
                repositories,
                newData)
        )
        this.repositories.clear()
        this.repositories.addAll(newData)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.repository_recycler_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = repositories[position]

        holder.apply {

            body.setOnClickListener {
                itemListener?.openDetails(current.id)
            }

            repositoryTitle.text = current.name
            repositoryDescription.text = current.description

            ownerName.text = current.owner.login

            context?.let {
                Glide.with(it)
                    .load(current.owner.avatar_url)
                    .circleCrop()
                    .into(ownerThumbnail)
            }

            ownerThumbnail.setOnClickListener {
                itemListener?.openOwnersProfile(current.owner.html_url)
            }

            if(current.open_issues != 0 ) {
                issuesNumber.apply {
                    text = current.open_issues.toString()
                    visibility = View.VISIBLE
                }
                issuesImage.visibility = View.VISIBLE
            }
            else {
                issuesNumber.visibility = View.GONE
                issuesImage.visibility = View.GONE
            }

            watcherNumber.text = current.watchers.toString()
            forkNumber.text = current.forks.toString()
        }
    }

    override fun getItemCount(): Int = repositories.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val body = itemView.body

        val repositoryTitle = itemView.repository_name
        val repositoryDescription = itemView.repository_description

        val ownerName = itemView.owner_name
        val ownerThumbnail = itemView.owner_thumbnail

        val issuesNumber = itemView.issues_number
        val issuesImage = itemView.issues_image

        val watcherNumber = itemView.watcher_number
        val forkNumber = itemView.fork_number
    }

}