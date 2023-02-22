package com.softgames.petscare.presentation.menu.consumer.cart

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.softgames.petscare.R
import com.softgames.petscare.base.BaseFragment
import com.softgames.petscare.data.ProductRepository
import com.softgames.petscare.databinding.FragmentCartBinding
import com.softgames.petscare.doman.model.CartProduct
import com.softgames.petscare.doman.model.Consumer
import com.softgames.petscare.doman.model.toProduct
import com.softgames.petscare.presentation.menu.consumer.ConsumerViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.collections.ArrayList

class CartFragment : BaseFragment<FragmentCartBinding>() {
    override fun setView(p0: LayoutInflater, p1: ViewGroup?) =
        FragmentCartBinding.inflate(p0, p1, false)

    private lateinit var customer: Consumer
    private val view_model: ConsumerViewModel by activityViewModels()
    private val shopping_cart_products = ArrayList<CartProduct>()
    private var total = 0f

    override fun main() {
        getConsumerUser()

        binding.btnPay.setOnClickListener {
            pay()
        }
    }

    private fun getConsumerUser() {
        view_model.consumer_user.observe(viewLifecycleOwner) {
            customer = it
            getShoppingCartProducts()
        }
    }

    private fun getShoppingCartProducts() {
        viewLifecycleOwner.lifecycleScope.launch {
            if (customer.shopping_cart != null) {
                customer.shopping_cart!!.forEach { item ->
                    val product = Firebase.firestore.collection("Products")
                        .document(item.key.toString()).get().await().toProduct()
                    shopping_cart_products.add(CartProduct(product, item.value as Long))
                }
                setupRecyclerView()
                calculateTotal()
            }
        }
    }

    private fun setupRecyclerView() {
        binding.apply {
            recyclerCart.layoutManager = LinearLayoutManager(requireContext())
            recyclerCart.adapter = CartAdapter(
                requireContext(),
                shopping_cart_products,
                onIncreaseClick,
                onDecreaseClick
            )
        }
    }

    private val onIncreaseClick: (CartProduct, Int) -> Unit = { cart_product, index ->
        shopping_cart_products[index].quantity += 1
        view_model.addProductToShoppingCart(customer.id, cart_product.product.id)
        changeTotal(cart_product.product.price)
    }

    private val onDecreaseClick: (CartProduct, Int) -> Unit = { cart_product, index ->
        shopping_cart_products[index].quantity -= 1
        view_model.subtractProductFromShoppingCart(customer.id, cart_product.product.id)
        changeTotal(-cart_product.product.price)
    }

    private fun changeTotal(value: Float) {
        total += value
        binding.btnPay.text = "Pagar $$total MXN"
    }

    private fun calculateTotal() {
        shopping_cart_products.forEach {
            total += it.product.price * it.quantity
            binding.btnPay.text = "Pagar $$total MXN"
        }
    }

    private fun pay() {
        if (total > 0) {
            viewLifecycleOwner.lifecycleScope.launch {
                shopping_cart_products.forEach {
                    Log.d("IOTEC", it.quantity.toString())
                    ProductRepository.setPurchasesToProduct(it.product.id, it.quantity)
                    ProductRepository.addPurchaseToHistory(customer.id, it.product.id, it.quantity)
                }
                ProductRepository.clearShoppingCart(customer.id)
                message("Compra realizada con éxito")
                requireActivity().finish()
                startActivity(requireActivity().intent)
            }
        } else {
            message("No hay productos en el carrito")
        }
    }
}