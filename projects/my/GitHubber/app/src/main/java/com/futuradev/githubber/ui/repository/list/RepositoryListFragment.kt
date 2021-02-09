package com.futuradev.githubber.ui.repository.list

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.futuradev.githubber.R
import com.futuradev.githubber.data.model.Repository
import com.futuradev.githubber.ui.repository.RepositoryViewModel
import com.futuradev.githubber.utils.*
import com.futuradev.githubber.utils.enum.SortType
import com.futuradev.githubber.utils.listeners.BrowserListener
import com.futuradev.githubber.utils.listeners.RepositoryListener
import com.futuradev.githubber.utils.listeners.ToolbarController
import com.futuradev.githubber.utils.listeners.ToolbarListener
import kotlinx.android.synthetic.main.fragment_repository_details.*
import kotlinx.android.synthetic.main.fragment_repository_list.*
import kotlinx.android.synthetic.main.image_recycler_view.*
import kotlinx.android.synthetic.main.main_toolbar.*
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel
import kotlin.coroutines.CoroutineContext


class RepositoryListFragment(override val coroutineContext: CoroutineContext = Dispatchers.Main) :
    Fragment(), CoroutineScope, ToolbarController, RepositoryListener {

    private val appConfig : AppConfig by inject()

    private val viewModel: RepositoryViewModel by sharedViewModel()

    private var adapter: RepositoryListAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_repository_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showSearchPlaceholder()

        customizeToolbar()
        setAdapters()
        setObservers()

        search_placeholder.setOnClickListener {
            (activity as ToolbarListener).requestSearchFocus()
        }
    }

    private fun customizeToolbar() {
        (activity as ToolbarListener).apply {
            setSearchVisibility(View.VISIBLE)
        }
    }

    private fun setAdapters() {

        adapter = RepositoryListAdapter().apply {
            setListener(this@RepositoryListFragment)
        }
        repository_recycler.adapter = adapter
    }

    private fun setObservers() {

        viewModel.repositories.observe(viewLifecycleOwner, Observer {
            it ?: return@Observer

            if (it.isEmpty())
                showSearchNotFoundPlaceholder()
            else
                refreshRecycler(it)
        })

        viewModel.errorMessage.observe(viewLifecycleOwner, Observer {
            it ?: return@Observer

            view?.showSnackMessage(it)
            viewModel.errorMessage.value = null
        })
    }

    private fun showSearchNotFoundPlaceholder() {
        repository_recycler.visibility = View.GONE

        label_not_found.apply {
            visibility = View.VISIBLE
            text = resources.getString(R.string.fragment_repository_list_no_search_result)
        }

        search_placeholder.apply {
            visibility = View.VISIBLE
            setImageDrawable(
                ResourcesCompat.getDrawable(resources, R.drawable.ic_not_found, null)
            )
        }
    }

    private fun showSearchPlaceholder() {
        repository_recycler.visibility = View.GONE

        label_not_found.apply {
            visibility = View.VISIBLE
            text = resources.getString(R.string.fragment_repository_list_search)
        }

        search_placeholder.apply {
            visibility = View.VISIBLE
            setImageDrawable(
                ResourcesCompat.getDrawable(resources, R.drawable.ic_search, null)
            )
        }
    }

    private fun refreshRecycler(list: Array<Repository?>) {
        repository_recycler.visibility = View.VISIBLE
        label_not_found.visibility = View.GONE
        search_placeholder.visibility = View.GONE

        adapter?.refreshData(list.filterNotNull().toList())
    }

    override fun toolbarLogoClicked() {
        repository_recycler.smoothScrollToPosition(0)
    }

    override fun getTextWatcher(): TextWatcher = object: TextWatcher {
        var searchFor = ""

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val searchText = s?.toString().orEmpty()

            if (searchText == searchFor)
                return

            searchFor = searchText

            launch {
                delay(300)
                if (searchText != searchFor)
                    return@launch

                if(searchText.isNotEmpty())
                    viewModel.search(searchText)
                else
                   showSearchPlaceholder()
            }
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    override fun sortBy(sortType: SortType) {
        viewModel.sortBy(sortType)
        repository_recycler.smoothScrollToPosition(0)
    }

    override fun openOwnersProfile(profileUrl: String) {
        appConfig.isPremiumVersion {
            (activity as? BrowserListener)?.openInBrowser(profileUrl)
        }
    }

    override fun openDetails(repositoryId: Int) {
        appConfig.isPremiumVersion {
            RepositoryListFragmentDirections.toDetails(repositoryId).also {
                findNavController().navigate(it)
            }
        }
    }
}