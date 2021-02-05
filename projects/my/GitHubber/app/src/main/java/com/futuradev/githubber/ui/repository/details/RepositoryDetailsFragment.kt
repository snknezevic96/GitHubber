package com.futuradev.githubber.ui.repository.details

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.futuradev.githubber.R
import com.futuradev.githubber.data.model.Repository
import com.futuradev.githubber.ui.repository.RepositoryViewModel
import com.futuradev.githubber.utils.ToolbarListener
import com.futuradev.githubber.utils.formatDate
import kotlinx.android.synthetic.main.fragment_repository_details.*
import org.koin.android.viewmodel.ext.android.sharedViewModel


class RepositoryDetailsFragment : Fragment() {

    private var organizationsAdapter : OrganizationsAdapter? = null

    private val args : RepositoryDetailsFragmentArgs by navArgs()

    private val viewModel: RepositoryViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_repository_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hideKeyboard()
        customizeToolbar()

        setAdapter()
        setObservers()
    }

    private fun setAdapter() {
        organizationsAdapter = OrganizationsAdapter()
        organizations_recycler.adapter = organizationsAdapter
    }

    private fun setObservers() {

        viewModel.repositories.observe(viewLifecycleOwner, Observer { repositories ->
            repositories ?: return@Observer

            repositories.findRepository()?.let { repository ->
                bindData(repository)
            }
        })

        viewModel.userOrganizations.observe(viewLifecycleOwner, Observer {
            it ?: return@Observer

            organizationsAdapter?.setData(it)
        })
    }

    private fun List<Repository>.findRepository() : Repository? = find { it.id == args.repositoryId }

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
        code_language.text = repository.language

        val dateCreated = repository.created_at.formatDate()
        date_created.text = "Created: $dateCreated"

        val dateUpdated = repository.updated_at.formatDate()
        date_updated.text = "Last update: $dateUpdated"
    }

    private fun hideKeyboard() {
        activity?.let {
            val inputMethodManager = it
                .getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

            inputMethodManager.hideSoftInputFromWindow(it.currentFocus?.windowToken, 0)
        }
    }

    private fun customizeToolbar() {
        (activity as? ToolbarListener)?.setSearchVisibility(View.GONE)
    }
}