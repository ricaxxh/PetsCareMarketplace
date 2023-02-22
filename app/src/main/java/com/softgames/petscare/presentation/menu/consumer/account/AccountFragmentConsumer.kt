package com.softgames.petscare.presentation.menu.consumer.account

import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.softgames.petscare.base.BaseFragment
import com.softgames.petscare.databinding.FragmentAccountBinding

class AccountFragmentConsumer : BaseFragment<FragmentAccountBinding>() {
    override fun setView(p0: LayoutInflater, p1: ViewGroup?) =
        FragmentAccountBinding.inflate(p0, p1, false)

    override fun main() {
        val user_id = Firebase.auth.currentUser!!.uid
        message("User id: $user_id")
        binding.txtUserId.text = user_id
    }
}