package com.softgames.petscare.presentation.menu.seller.view.orders

import android.view.LayoutInflater
import android.view.ViewGroup
import com.softgames.petscare.base.BaseFragment
import com.softgames.petscare.databinding.FragmentOrdersBinding

class OrdersFragment : BaseFragment<FragmentOrdersBinding>() {
    override fun setView(p0: LayoutInflater, p1: ViewGroup?) =
        FragmentOrdersBinding.inflate(p0, p1, false)

    override fun main() {

    }
}