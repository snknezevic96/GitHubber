package com.futuradev.githubber.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.futuradev.githubber.R
import com.futuradev.githubber.utils.SearchController
import com.futuradev.githubber.utils.ToolbarListener
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_toolbar.view.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, ToolbarListener {

    private lateinit var navController : NavController
    private lateinit var drawerLayout : DrawerLayout
    private lateinit var sideView : NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupNavigation()
        setupToolbar()
    }

    private fun setupToolbar() {
        supportFragmentManager.findFragmentById(R.id.repositoryListFragment)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navigation_host_fragment)
        val fragment : Fragment? = navHostFragment?.childFragmentManager?.fragments?.first()

        (fragment as SearchController).getTextWatcher().also { watcher ->
            watcher ?: return@also

            toolbar.search.addTextChangedListener(watcher)
        }
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

    override fun setSearchVisibility(visibility: Int) {
        toolbar.search.visibility = visibility
    }
}