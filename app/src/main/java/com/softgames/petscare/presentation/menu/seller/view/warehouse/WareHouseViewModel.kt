package com.softgames.petscare.presentation.menu.seller.view.warehouse

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.softgames.petscare.data.ProductRepository
import com.softgames.petscare.doman.model.Product

class WareHouseViewModel : ViewModel() {

    private val _product_list = MutableLiveData<List<Product>>()
    val product_list = _product_list as LiveData<List<Product>>

    suspend fun getProductList(company_id: String) {
        ProductRepository.getCompanyProductList(company_id).collect() {
            _product_list.value = it
        }
    }
}