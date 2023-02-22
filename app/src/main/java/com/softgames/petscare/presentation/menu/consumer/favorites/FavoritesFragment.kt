package com.softgames.petscare.presentation.menu.consumer.favorites

import android.view.LayoutInflater
import android.view.ViewGroup
import com.softgames.petscare.base.BaseFragment
import com.softgames.petscare.databinding.FragmentFavoritesBinding

class FavoritesFragment : BaseFragment<FragmentFavoritesBinding>() {
    override fun setView(p0: LayoutInflater, p1: ViewGroup?) =
        FragmentFavoritesBinding.inflate(p0, p1, false)

    override fun main() {

    }
}