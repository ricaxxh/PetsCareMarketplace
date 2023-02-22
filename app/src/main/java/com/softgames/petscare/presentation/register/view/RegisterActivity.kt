package com.softgames.petscare.presentation.register.view

import com.softgames.petscare.base.BaseActivity
import com.softgames.petscare.databinding.ActivityRegisterBinding

class RegisterActivity : BaseActivity<ActivityRegisterBinding>() {
    override fun setView() = ActivityRegisterBinding.inflate(layoutInflater)

    override fun main() {}

    override fun launchEvents() {
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

}