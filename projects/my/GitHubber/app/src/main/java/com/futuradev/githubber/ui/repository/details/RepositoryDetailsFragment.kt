package com.futuradev.githubber.ui.repository.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.futuradev.githubber.R
import com.futuradev.githubber.data.model.CustomItemUrls
import com.futuradev.githubber.data.model.Repository
import com.futuradev.githubber.ui.repository.RepositoryViewModel
import com.futuradev.githubber.utils.manager.KeyboardManager
import com.futuradev.githubber.utils.listeners.SearchListener
import com.futuradev.githubber.utils.formatDate
import kotlinx.android.synthetic.main.fragment_repository_details.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel


class RepositoryDetailsFragment : Fragment() {

    private val args : RepositoryDetailsFragmentArgs by navArgs()
    private val keyboardManager : KeyboardManager by inject()
    private val viewModel: RepositoryViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_repository_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        keyboardManager.hideKeyboard(activity)
        customizeToolbar()

        setObservers()

        viewModel.findRepository(args.repositoryId)
    }

    private fun setObservers() {

        viewModel.repository.observe(viewLifecycleOwner, Observer {
            it ?: return@Observer

            bindData(it)
        })

        viewModel.userOrganizations.observe(viewLifecycleOwner, Observer {
            it ?: return@Observer

            organizations_recycler.setData("Organizations", it.map { CustomItemUrls(it.avatar_url, null) })
        })
    }


    private fun bindData(repository: Repository) {

        viewModel.getUserOrganizations(repository.owner.login)

        owner_name.text = repository.owner.login

        Glide.with(this@RepositoryDetailsFragment)
            .load(repository.owner.avatar_url)
            .circleCrop()
            .into(owner_thumbnail)

        repository_name.text = repository.name
        repository_description.text = repository.description

        stars_number.text = repository.stargazers_count.toString()
        watcher_number.text = repository.watchers_count.toString()
        fork_number.text = repository.forks_count.toString()
        issues_number.text = repository.open_issues_count.toString()
        code_language.text = repository.language

        val dateCreated = repository.created_at.formatDate()
        date_created.text = "Created: $dateCreated"

        val dateUpdated = repository.updated_at.formatDate()
        date_updated.text = "Last update: $dateUpdated"
    }

    private fun customizeToolbar() {
        (activity as? SearchListener)?.setSearchVisibility(View.GONE)
    }
}