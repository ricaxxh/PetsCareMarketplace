package com.softgames.petscare.presentation.menu.consumer.purchases

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.softgames.petscare.base.BaseFragment
import com.softgames.petscare.data.ProductRepository
import com.softgames.petscare.databinding.FragmentPurchasesBinding
import com.softgames.petscare.doman.model.CartProduct
import com.softgames.petscare.doman.model.Consumer
import com.softgames.petscare.doman.model.toProduct
import com.softgames.petscare.presentation.menu.consumer.ConsumerViewModel
import com.softgames.petscare.presentation.menu.consumer.cart.CartAdapter
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PurchasesFragment : BaseFragment<FragmentPurchasesBinding>() {
    override fun setView(p0: LayoutInflater, p1: ViewGroup?) =
        FragmentPurchasesBinding.inflate(p0, p1, false)

    private lateinit var customer: Consumer
    private val view_model: ConsumerViewModel by activityViewModels()
    private val purchases_products = ArrayList<CartProduct>()

    override fun main() {
        getConsumerUser()
    }

    private fun getConsumerUser() {
        view_model.consumer_user.observe(viewLifecycleOwner) {
            customer = it
            getPurchasesProducts()
        }
    }

    private fun getPurchasesProducts() {
        viewLifecycleOwner.lifecycleScope.launch {
            if (customer.purchases != null) {
                customer.purchases!!.forEach { item ->
                    val product = Firebase.firestore.collection("Products")
                        .document(item.key.toString()).get().await().toProduct()
                    purchases_products.add(CartProduct(product, item.value as Long))
                }
                setupRecyclerView()
            }
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerCart.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerCart.adapter = PurchasesAdapter(
            requireContext(), purchases_products, onRating
        )
    }

    private val onRating: (String, Float) -> Unit = { product_id, value ->
        message("SE CALIFICO EL PRODUCTO")
        ProductRepository.ratingProduct(product_id, value)
    }
}