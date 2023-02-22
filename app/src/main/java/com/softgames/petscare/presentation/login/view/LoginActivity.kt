package com.softgames.petscare.presentation.login.view

import android.content.Intent
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.softgames.petscare.base.BaseActivity
import com.softgames.petscare.databinding.ActivityLoginBinding
import com.softgames.petscare.presentation.menu.consumer.MenuConsumerActivity
import com.softgames.petscare.presentation.menu.seller.view.MenuSellerActivity
import com.softgames.petscare.services.firebase_auth.AuthService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : BaseActivity<ActivityLoginBinding>() {
    override fun setView() = ActivityLoginBinding.inflate(layoutInflater)

    private val current_user by lazy { Firebase.auth.currentUser }

    override fun onStart() {
        super.onStart()
        checkAuthInfo()
    }

    private fun checkAuthInfo() {
        if (current_user != null) {
            lifecycleScope.launch {
                CoroutineScope(Dispatchers.IO).launch {
                    AuthService.getUserById(current_user!!.uid).collect { user ->
                        val user_type = user.getString("user_type")
                        if (user_type == "Seller") navigateToMenuSellerScreen()
                        else if (user_type == "Consumer") navigateToMenuConsumerScreen()
                    }
                }
            }
        }
    }

    private fun navigateToMenuSellerScreen() {
        startActivity(Intent(this, MenuSellerActivity::class.java))
        finish()
    }

    private fun navigateToMenuConsumerScreen() {
        startActivity(Intent(this, MenuConsumerActivity::class.java))
        finish()
    }

    override fun main() {}

}