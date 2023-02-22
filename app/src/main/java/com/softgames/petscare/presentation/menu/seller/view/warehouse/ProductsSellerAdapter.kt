package com.softgames.petscare.presentation.menu.seller.view.warehouse

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.softgames.petscare.databinding.ItemProductSellerBinding
import com.softgames.petscare.doman.model.Product
import com.softgames.petscare.presentation.others.dialogs.CreateProductPhotoDialog

class ProductsSellerAdapter(
    private val context: Context,
    private val productList: List<Product>
) :
    RecyclerView.Adapter<ProductsSellerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            ItemProductSellerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(productList[position])
    }

    override fun getItemCount() = productList.size

    inner class ViewHolder(val binding: ItemProductSellerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.apply {
                txtProductName.text = product.name
                txtCategory.text = "Categoria: ".plus(product.category)
                txtPetType.text = "Mascota(s): ".plus(product.pet_type)
                txtPrice.text = "$ ".plus(product.price)
                Glide.with(context).load(product.photo_url).into(binding.imgProduct)
                Log.d("IOTEC","${product.times_rating}")

                txtCantity.text = "[".plus(product.times_rating).plus("]")

                var rating = 0f
                Log.d("IOTEC",product.times_rating.toString())
                if (product.times_rating == 0) {
                    ratingBar.rating = 0f
                } else {
                    rating = (product.total_rating!! / product.times_rating).toFloat()
                    ratingBar.rating = rating
                }

                imgProduct.setOnClickListener {
                    CreateProductPhotoDialog(context, product.name, product.photo_url)
                }
            }
        }
    }

}