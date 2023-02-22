package com.softgames.petscare.presentation.menu.consumer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.softgames.petscare.data.ProductRepository
import com.softgames.petscare.doman.model.*
import com.softgames.petscare.services.firebase_auth.AuthService
import kotlinx.coroutines.launch

class ConsumerViewModel : ViewModel() {

    var consumer_user = MutableLiveData<Consumer>()

    private val _product_list = MutableLiveData<List<Product>>()
    val product_list = _product_list as LiveData<List<Product>>

    init {
        getConsumerUser()
    }

    private fun getConsumerUser() {
        viewModelScope.launch {
            val user_id = Firebase.auth.currentUser!!.uid
            viewModelScope.launch {
                AuthService.getUserById(user_id).collect() {
                    consumer_user.value = it.toConsumer()
                }
            }
        }
    }

    suspend fun getAllProductList() {
        ProductRepository.getAllProductList().collect() {
            _product_list.value = it
        }
    }

    fun addProductToShoppingCart(user_id: String, product_id: String) {
        viewModelScope.launch {
            ProductRepository.addProductToShoppingCart(user_id, product_id)
        }
    }

    fun subtractProductFromShoppingCart(user_id: String, product_id: String) {
        viewModelScope.launch {
            ProductRepository.subtractProductFromShoppingCart(user_id, product_id)
        }
    }
}