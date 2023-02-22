package com.softgames.petscare.presentation.menu.consumer.store

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.softgames.petscare.base.BaseFragment
import com.softgames.petscare.data.ProductRepository
import com.softgames.petscare.databinding.FragmentStoreBinding
import com.softgames.petscare.doman.model.Consumer
import com.softgames.petscare.doman.model.Product
import com.softgames.petscare.presentation.menu.consumer.ConsumerViewModel
import kotlinx.coroutines.launch

class StoreFragment : BaseFragment<FragmentStoreBinding>() {
    override fun setView(p0: LayoutInflater, p1: ViewGroup?) =
        FragmentStoreBinding.inflate(p0, p1, false)

    private lateinit var consumer_user: Consumer

    private val view_model: ConsumerViewModel by activityViewModels()
    private lateinit var product_list: List<Product>

    override fun main() {
        getConsumerUser()
        getProductList()
    }

    private fun getProductList() {
        viewLifecycleOwner.lifecycleScope.launch {

            view_model.getAllProductList()

            view_model.product_list.observe(viewLifecycleOwner) {
                product_list = it
                binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
                binding.recyclerView.adapter =
                    StoreAdapter(requireContext(), product_list, addProductToCart, onFavoriteClick)
            }
        }
    }

    private fun getConsumerUser() {
        view_model.consumer_user.observe(viewLifecycleOwner) {
            consumer_user = it
        }
    }

    private val addProductToCart: (product_id: String) -> Unit = { product_id ->
        viewLifecycleOwner.lifecycleScope.launch {
            view_model.addProductToShoppingCart(consumer_user.id, product_id)
            message("Producto agregado al carrito.", Toast.LENGTH_SHORT)
        }
    }

    private val onFavoriteClick: (product_id: String, is_checked: Boolean) -> Unit =
        { id, is_checked ->
            viewLifecycleOwner.lifecycleScope.launch {
                if (is_checked) {
                    ProductRepository.addProductToFavorites(id)
                } else {
                    ProductRepository.removeProductFromFavorites(id)
                }
            }
        }
}

