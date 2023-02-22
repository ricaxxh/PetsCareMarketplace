package com.softgames.petscare.presentation.menu.seller.view.warehouse

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.softgames.petscare.base.BaseFragment
import com.softgames.petscare.databinding.FragmentWarehouseBinding
import com.softgames.petscare.doman.model.Seller
import com.softgames.petscare.doman.model.toSeller
import com.softgames.petscare.presentation.menu.seller.view.register_product.RegisterProductActivity
import com.softgames.petscare.services.firebase_auth.AuthService
import kotlinx.coroutines.launch

class WareHouseFragment : BaseFragment<FragmentWarehouseBinding>() {
    override fun setView(p0: LayoutInflater, p1: ViewGroup?) =
        FragmentWarehouseBinding.inflate(p0, p1, false)

    private lateinit var seller: Seller
    private val vm: WareHouseViewModel by viewModels()

    override fun main() {

        viewLifecycleOwner.lifecycleScope.launch {
            AuthService.getUserById(Firebase.auth.currentUser!!.uid).collect {
                seller = it.toSeller()
                vm.getProductList(seller.company_id)
            }
        }

        vm.product_list.observe(viewLifecycleOwner) { product_list ->
            binding.apply {
                recyclerView.layoutManager = LinearLayoutManager(context)
                recyclerView.adapter = ProductsSellerAdapter(requireContext(), product_list)
            }
        }
    }

    override fun launchEvents() {
        binding.apply {
            btnAdd.setOnClickListener {
                navigateToRegisterProductScreen()
            }
        }
    }

    private fun navigateToRegisterProductScreen() {
        startActivity(Intent(activity, RegisterProductActivity::class.java))
    }
}