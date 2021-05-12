package com.diegoparra.veggie.main_activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.diegoparra.veggie.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel : MainActivityViewModel by viewModels()
    private lateinit var bottomNavView : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavView = findViewById<BottomNavigationView>(R.id.nav_view_main)
        bottomNavSetUp()
        addBadgeToMenuCart()
    }

    private fun bottomNavSetUp(){
        /*
            Note: Do not use val navController = findNavController(R.id.nav_host_main)
            as it is not compatible with FragmentContainerView in MainActivityLayout.
         */
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_main) as NavHostFragment
        val navController = navHostFragment.navController
        NavigationUI.setupWithNavController(bottomNavView, navController)
    }

    private fun addBadgeToMenuCart(){
        val cartBadge = bottomNavView.getOrCreateBadge(R.id.nav_cart)
        viewModel.cartSize.observe(this) {
            if(cartBadge!=null){
                if(it > 0){
                    cartBadge.number = it
                    if(!cartBadge.isVisible) { cartBadge.isVisible = true }
                }else{
                    if(cartBadge.isVisible) { cartBadge.isVisible = false }
                    cartBadge.clearNumber()
                }
            }
        }
    }
}