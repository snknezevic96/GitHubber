package com.futuradev.githubber.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.Glide
import com.futuradev.githubber.R
import com.futuradev.githubber.data.model.CustomItemUrls
import com.futuradev.githubber.utils.enum.SortType
import com.futuradev.githubber.utils.manager.KeyboardManager
import com.futuradev.githubber.utils.listeners.ToolbarListener
import com.futuradev.githubber.utils.listeners.SearchListener
import com.futuradev.githubber.utils.showSnackMessage
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_toolbar.view.*
import kotlinx.android.synthetic.main.navigation_header.*
import kotlinx.android.synthetic.main.navigation_header.view.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    SearchListener {

    private val keyboardManager : KeyboardManager by inject()
    private val viewModel : MainViewModel by viewModel()

    private lateinit var navController : NavController
    private lateinit var drawerLayout : DrawerLayout
    private lateinit var sideView : NavigationView

    private var menu : Menu? = null
    private var userId : Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupNavigation()
        setupToolbar()
        setObservers()

        getUserIdFromArgs()?.let { viewModel.getUser(it) }
    }

    private fun setupNavigation() {

        setSupportActionBar(toolbar.toolbar)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)

            setHomeAsUpIndicator(R.drawable.ic_hamburger)
        }

        navController = findNavController(R.id.navigation_host_fragment)
        drawerLayout = drawer_layout
        sideView = navigation_drawer_view

        val appBarConfig = AppBarConfiguration(
            topLevelDestinationIds = setOf(
                R.id.repositoryListFragment
            ),
            drawerLayout = drawerLayout
        )

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfig)
        NavigationUI.setupWithNavController(sideView, navController)

        sideView.setNavigationItemSelectedListener(this)
    }

    private fun setObservers() {

        viewModel.user.observe(this, Observer {
            it ?: return@Observer

            sideView.getHeaderView(0).apply {

                Glide.with(this)
                    .load(it.avatar_url)
                    .circleCrop()
                    .into(user_thumbnail)

                username.text = it.login
            }
        })

        viewModel.followers.observe(this, Observer {
            it ?: return@Observer

            sideView.getHeaderView(0).apply {
                followers_recycler.setData("Followers", it.map { CustomItemUrls(it.avatar_url, it.html_url) })
            }
        })

        viewModel.following.observe(this, Observer {
            it ?: return@Observer

            sideView.getHeaderView(0).apply {
                following_recycler.setData("Following", it.map { CustomItemUrls(it.avatar_url, it.html_url) })
            }
        })
    }

    private fun setupToolbar() {
        getToolbarListener()?.let { fragment ->

            toolbar.apply {

                fragment.getTextWatcher().also { watcher ->
                    watcher ?: return@also
                    search.addTextChangedListener(watcher)
                }

                logo.setOnClickListener { fragment.toolbarLogoClicked() }
            }
        }
    }

    private fun getToolbarListener() : ToolbarListener? {
        supportFragmentManager.findFragmentById(R.id.repositoryListFragment)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navigation_host_fragment)

        val fragment = navHostFragment?.childFragmentManager?.fragments?.first()

        return (fragment as? ToolbarListener)
    }

    private fun getUserIdFromArgs() : Int? {
        intent.extras?.let {
            userId = it.getInt("userId")
        }
        return userId
    }

    private fun closeApp() {
        Intent(Intent.ACTION_MAIN)
            .apply {
                addCategory(Intent.CATEGORY_HOME)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            .also { startActivity(it) }
    }

    override fun onSupportNavigateUp(): Boolean {
        when(navController.currentDestination?.id) {
            R.id.repositoryListFragment -> drawerLayout.openDrawer(GravityCompat.START)
            else -> navController.popBackStack()
        }
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        item.isChecked = true

        drawerLayout.closeDrawers()

        return when(item.itemId) {

            R.id.logout -> {
                // TODO: logout
                true
            }
            else -> false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        this.menu = menu
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.isChecked = !item.isChecked

        return when(item.itemId) {
            R.id.sort_stars -> {
                getToolbarListener()?.sortBy(SortType.STARS)
                true
            }
            R.id.sort_forks -> {
                getToolbarListener()?.sortBy(SortType.FORKS)
                true
            }
            R.id.sort_updated -> {
                getToolbarListener()?.sortBy(SortType.UPDATED)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        else {
            when(navController.currentDestination?.id) {
                R.id.repositoryListFragment -> {

                    viewModel.appClosing(
                        closeApp = { closeApp() },
                        showMessage = { body.showSnackMessage("Please click BACK again to close app.") }
                    )
                }
                else -> super.onBackPressed()
            }
        }
    }

    override fun setSearchVisibility(visibility: Int) {
        toolbar.search.visibility = visibility

        menu?.findItem(R.id.sort_submenu_item)?.isVisible =
            visibility == View.VISIBLE
    }

    override fun requestSearchFocus() {
        toolbar.search.requestFocus()
        keyboardManager.showKeyboard(this)
    }
}