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
import com.futuradev.githubber.utils.*
import com.futuradev.githubber.utils.enum.SortType
import com.futuradev.githubber.utils.manager.KeyboardManager
import com.futuradev.githubber.utils.listeners.ToolbarController
import com.futuradev.githubber.utils.listeners.ToolbarListener
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_toolbar.view.*
import kotlinx.android.synthetic.main.navigation_header.*
import kotlinx.android.synthetic.main.navigation_header.view.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    ToolbarListener {

    private val keyboardManager : KeyboardManager by inject()
    private val appConfig : AppConfig by inject()
    private val viewModel : MainViewModel by viewModel()

    var topMenu : Menu? = null


    private lateinit var navController : NavController
    private lateinit var drawerLayout : DrawerLayout
    private lateinit var sideView : NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupNavigation()
        setupToolbar()
        setObservers()
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

        sideView.apply {
            setNavigationItemSelectedListener(this@MainActivity)

            appConfig.isPremiumVersion {
                menu.findItem(R.id.premium_app).isVisible = false
            }
        }
    }

    private fun setObservers() {

        viewModel.user.observe(this, Observer {
            if (it == null) {
                sideView.apply {
                    getHeaderView(0).header_body.visibility = View.GONE
                    getHeaderView(0).header_logo.visibility = View.VISIBLE

                    menu.findItem(R.id.logout).isVisible = false
                }
                topMenu?.apply { findItem(R.id.login)?.isVisible  = true }

            } else {
                sideView.apply {
                    getHeaderView(0).apply {
                        header_body.visibility = View.VISIBLE
                        header_logo.visibility = View.GONE

                        menu.findItem(R.id.logout).isVisible = true

                        Glide.with(this)
                            .load(it.avatar_url)
                            .circleCrop()
                            .into(user_thumbnail)

                        username.text = it.login
                    }

                    topMenu?.apply { findItem(R.id.login)?.isVisible  = false }

                    viewModel.apply { getFollowers() }
                }
            }
        })

        viewModel.followers.observe(this, Observer {
            sideView.getHeaderView(0).apply {
                followers_recycler.setData("Followers", it?.map { CustomItemUrls(it.avatar_url, it.html_url) } ?: emptyList())
            }
        })

        viewModel.following.observe(this, Observer {
            sideView.getHeaderView(0).apply {
                following_recycler.setData("Following", it?.map { CustomItemUrls(it.avatar_url, it.html_url) } ?: emptyList())
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

    private fun getToolbarListener() : ToolbarController? {
        supportFragmentManager.findFragmentById(R.id.repositoryListFragment)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navigation_host_fragment)

        val fragment = navHostFragment?.childFragmentManager?.fragments?.first()

        return (fragment as? ToolbarController)
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
                viewModel.logout()
                true
            }

            R.id.premium_app -> {
                UpdateDialog.newInstance().show(supportFragmentManager, "")
                true
            }
            else -> false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        appConfig.isPremiumVersion {
            menuInflater.inflate(R.menu.search_menu, menu)

            topMenu = menu?.apply {
                findItem(R.id.login).isVisible = viewModel.user.value == null
            }
        }
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
            R.id.login -> {
                navController.navigate(R.id.authorizationFragment)
                //navigateTo(AuthorizationActivity::class.java)
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

        topMenu?.findItem(R.id.sort_submenu_item)?.isVisible =
            visibility == View.VISIBLE
    }

    override fun requestSearchFocus() {
        toolbar.search.requestFocus()
        keyboardManager.showKeyboard(this)
    }

    override fun setLoginButtonVisibility(visibility: Int) {
        topMenu?.findItem(R.id.login)?.isVisible =
            visibility == View.VISIBLE
    }
}