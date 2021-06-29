package com.diegoparra.veggie.main_activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.IdRes
import androidx.core.view.isVisible
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.diegoparra.veggie.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

private const val TAG = "MainActivityVeggie"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainActivityViewModel by viewModels()
    private lateinit var bottomNavView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        /*
            Splash screen:
                https://stackoverflow.com/questions/5486789/how-do-i-make-a-splash-screen/15832037#15832037
                https://www.youtube.com/watch?v=E5Xu2iNHRkk&t=160s
            Splash screen is actually intended just to hide loading while app/activity is being created.
            Do not use this as an additional activity with fixed times, as it will not reduce loading
            times at all, just add extra time making it disgusting to the final user.
            The loading time is due to objects instantiation and processes in the application class.
            In this case/app, processes are small, but object instantiation do take some time,
            as I have to init firebase remoteconfig, firestore, room (and the ones defined as
            singletons in hilt modules)
         */
        //  Make sure this is before calling super.onCreate
        setTheme(R.style.Theme_Veggie)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavView = findViewById<BottomNavigationView>(R.id.nav_view_main)
        bottomNavSetUp()
        addBadgeToMenuCart()
    }

    private fun bottomNavSetUp() {
        /*
            Note: Do not use val navController = findNavController(R.id.nav_host_main)
            as it is not compatible with FragmentContainerView in MainActivityLayout.
         */
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_main) as NavHostFragment
        val navController = navHostFragment.navController
        NavigationUI.setupWithNavController(bottomNavView, navController)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            bottomNavView.isVisible =
                !isNavGraphIdParentOf(navGraph = R.id.nav_order, destination = destination)
                        && destination.id != R.id.nav_cart
                        && destination.id != R.id.clearCartDialogFragment
        }
    }


    private fun isNavGraphIdParentOf(@IdRes navGraph: Int, destination: NavDestination): Boolean {
        Timber.tag(TAG).d("---------- destination = ${destination.label}, navGraph(R.id.nav_order) = $navGraph")
        var parentNavGraph = destination.parent
        while (parentNavGraph != null) {
            Timber.tag(TAG).d("iteration: parentNavGraphId=${parentNavGraph.id}")
            if (parentNavGraph.id == navGraph) {
                Timber.tag(TAG).d("nav_order is in parentNavGraph")
                return true
            }
            parentNavGraph = parentNavGraph.parent
        }
        Timber.tag(TAG).d("nav_order is not in parentNavGraph")
        return false
    }


    private fun addBadgeToMenuCart() {
        val cartBadge = bottomNavView.getOrCreateBadge(R.id.nav_cart)
        viewModel.cartSize.observe(this) {
            if (cartBadge != null) {
                if (it > 0) {
                    cartBadge.number = it
                    if (!cartBadge.isVisible) {
                        cartBadge.isVisible = true
                    }
                } else {
                    if (cartBadge.isVisible) {
                        cartBadge.isVisible = false
                    }
                    cartBadge.clearNumber()
                }
            }
        }
    }
}