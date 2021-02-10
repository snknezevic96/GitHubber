package com.futuradev.githubber.ui.main

import android.content.Intent
import android.net.Uri
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
import com.futuradev.githubber.data.model.ImageItemUrls
import com.futuradev.githubber.ui.custom.DownloadDialog
import com.futuradev.githubber.utils.*
import com.futuradev.githubber.utils.enum.SortType
import com.futuradev.githubber.utils.listeners.BrowserListener
import com.futuradev.githubber.utils.listeners.DownloadListener
import com.futuradev.githubber.utils.manager.KeyboardManager
import com.futuradev.githubber.utils.listeners.ToolbarController
import com.futuradev.githubber.utils.listeners.ToolbarListener
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.main_toolbar.view.*
import kotlinx.android.synthetic.main.main_toolbar.view.toolbar
import kotlinx.android.synthetic.main.navigation_header.*
import kotlinx.android.synthetic.main.navigation_header.view.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, ToolbarListener, BrowserListener {

    private val keyboardManager : KeyboardManager by inject()
    private val appConfig : AppConfig by inject()
    private val viewModel : MainViewModel by viewModel()

    private var topMenu : Menu? = null

    private lateinit var navController : NavController
    private lateinit var drawerLayout : DrawerLayout
    private lateinit var sideView : NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupNavigation()
        setupToolbarController()

        setObservers()
    }

    private fun setupNavigation() {
        drawerLayout = drawer_layout
        sideView = navigation_drawer_view
        navController = findNavController(R.id.navigation_host_fragment)

        toolbar.initActionBar().also {
            NavigationUI.setupActionBarWithNavController(this, navController, it)
            NavigationUI.setupWithNavController(sideView, navController)
        }
        sideView.initSideMenu()
    }

    private fun View.initActionBar() : AppBarConfiguration {
        setSupportActionBar(this.toolbar)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)

            setHomeAsUpIndicator(R.drawable.ic_hamburger)
        }

        return AppBarConfiguration(
            topLevelDestinationIds = setOf(
                R.id.repositoryListFragment
            ),
            drawerLayout = drawerLayout
        )
    }

    private fun NavigationView.initSideMenu() {
        setNavigationItemSelectedListener(this@MainActivity)

        appConfig.isPremiumVersion {
            menu.findItem(R.id.premium_app).isVisible = false
        }

        getHeaderView(0).user_thumbnail.setOnClickListener {
            viewModel.getUserProfileUrl()?.let {
                openInBrowser(it)
            }
        }
    }

    private fun setObservers() {

        viewModel.user.observe(this, Observer { user ->
            if (user == null) {
                sideView.apply {
                    getHeaderView(0).header_body.visibility = View.GONE
                    getHeaderView(0).header_logo.visibility = View.VISIBLE

                    menu.findItem(R.id.logout).isVisible = false
                }
                setLoginButtonVisibility(View.VISIBLE)
            } else {
                sideView.apply {
                    getHeaderView(0).apply {
                        header_body.visibility = View.VISIBLE
                        header_logo.visibility = View.GONE

                        Glide.with(this)
                            .load(user.avatar_url)
                            .circleCrop()
                            .into(user_thumbnail)

                        username.text = user.login
                    }
                    menu.findItem(R.id.logout).isVisible = true
                }
                setLoginButtonVisibility(View.GONE)
                viewModel.apply { getFollowers() }
            }
        })

        viewModel.followers.observe(this, Observer {
            it ?: return@Observer

            sideView.getHeaderView(0).apply {
                followers_recycler.setData("Followers", it.map { ImageItemUrls(it.avatar_url, it.html_url) })
            }
        })

        viewModel.following.observe(this, Observer {
            it ?: return@Observer

            sideView.getHeaderView(0).apply {
                following_recycler.setData("Following", it.map { ImageItemUrls(it.avatar_url, it.html_url) })
            }
        })
    }

    private fun setupToolbarController() {
        getToolbarController()?.let { fragment ->

            toolbar.apply {

                fragment.getTextWatcher().also { watcher ->
                    watcher ?: return@also
                    search.addTextChangedListener(watcher)
                }

                logo.setOnClickListener { fragment.toolbarLogoClicked() }
            }
        }
    }

    private fun getToolbarController() : ToolbarController? {
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
            R.id.authorizationFragment -> {
                setLoginButtonVisibility(View.VISIBLE)
                navController.popBackStack()
            }
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
                DownloadDialog.newInstance()
                    .apply {
                        listener = object : DownloadListener {
                            override fun downloadFinished() {
                                dismiss()
                            }

                            override fun downloadError() {
                                dismiss()
                                body.showSnackMessage(resources.getString(R.string.error_download))
                            }

                        }
                    }
                    .show(supportFragmentManager, "")
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
                getToolbarController()?.sortBy(SortType.STARS)
                true
            }
            R.id.sort_forks -> {
                getToolbarController()?.sortBy(SortType.FORKS)
                true
            }
            R.id.sort_updated -> {
                getToolbarController()?.sortBy(SortType.UPDATED)
                true
            }
            R.id.login -> {
                navController.navigate(R.id.authorizationFragment)
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
                        showMessage = { body.showSnackMessage(resources.getString(R.string.close_app)) }
                    )
                }
                R.id.authorizationFragment -> {
                    setLoginButtonVisibility(View.VISIBLE)
                    super.onBackPressed()
                }
                else -> super.onBackPressed()
            }
        }
    }

    override fun setSearchVisibility(visibility: Int) {
        toolbar.search.visibility = visibility

        topMenu?.findItem(R.id.sort_submenu_item)?.isVisible = visibility == View.VISIBLE
    }

    override fun requestSearchFocus() {
        toolbar.search.requestFocus()
        keyboardManager.showKeyboard(this)
    }

    override fun setLoginButtonVisibility(visibility: Int) {
        topMenu?.apply {
            findItem(R.id.login)?.isVisible = visibility == View.VISIBLE
        }
    }

    override fun openInBrowser(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }
}