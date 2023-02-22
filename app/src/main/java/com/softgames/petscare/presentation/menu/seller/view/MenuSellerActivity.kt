package com.softgames.petscare.presentation.menu.seller.view

import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.softgames.petscare.R
import com.softgames.petscare.base.BaseActivity
import com.softgames.petscare.databinding.ActivityMenuSellerBinding

class MenuSellerActivity : BaseActivity<ActivityMenuSellerBinding>() {
    override fun setView() = ActivityMenuSellerBinding.inflate(layoutInflater)

    override fun setActivityTheme(theme: Int) = R.style.THEME_REGISTER_PRODUCT

    override fun main() {
        setupNavigation()
    }

    private fun setupNavigation() {
        val nav_host =
            supportFragmentManager.findFragmentById(R.id.cont_frags_seller) as NavHostFragment
        val nav_controller = nav_host.navController
        binding.navbarSellers.setupWithNavController(nav_controller)

    }
}