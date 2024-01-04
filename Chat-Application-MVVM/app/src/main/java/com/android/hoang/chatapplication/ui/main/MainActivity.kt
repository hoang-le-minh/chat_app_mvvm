package com.android.hoang.chatapplication.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.base.BaseActivity
import com.android.hoang.chatapplication.util.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    //region vars
    private var currentNavController: LiveData<NavController>? = null
    //endregion

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun prepareView(savedInstanceState: Bundle?) {
        //viewModel.friend()
        if (savedInstanceState == null) {
            initBottomNavigationBar()
        }
        supportActionBar?.hide()
    }

    /**
     * Called on first creation and when restoring state.
     */
    private fun initBottomNavigationBar() {
        val navGraphIds = listOf(R.navigation.nav_graph_home, R.navigation.nav_graph_friend, R.navigation.nav_graph_profile)
        // Setup the bottom navigation view with a list of navigation graphs
        val controller = findViewById<BottomNavigationView>(R.id.bottom_nav).setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_container,
            intent = intent
        )
        // Whenever the selected controller changes, setup action bar
        controller.observe(this) { navController ->
            // Set destination listener to selected controller
            navController.addOnDestinationChangedListener { controller, destination, arguments ->
                // Don something when destination change
            }
        }
        currentNavController = controller
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Now that BottomNavigationBar has restored its instance state
        // and its selectedItemId, we can proceed with setting up the
        // BottomNavigationBar with Navigation
        initBottomNavigationBar()
    }
}