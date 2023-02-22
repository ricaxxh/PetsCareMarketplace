package com.softgames.petscare.presentation.menu.consumer.store

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.softgames.petscare.databinding.ItemProductConsumerBinding
import com.softgames.petscare.doman.model.Product
import com.softgames.petscare.presentation.others.dialogs.CreateProductPhotoDialog
import java.lang.Exception

class StoreAdapter(
    private val context: Context,
    private val productList: List<Product>,
    private val addProductToCart: (product_id: String) -> Unit,
    private val onFavoriteClick: (product_id: String, is_checked: Boolean) -> Unit,
) :
    RecyclerView.Adapter<StoreAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            ItemProductConsumerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(productList[position])
    }

    override fun getItemCount() = productList.size

    inner class ViewHolder(val binding: ItemProductConsumerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.apply {
                txtProductName.text = product.name
                txtCategory.text = "Categoria: ".plus(product.category)
                txtPetType.text = "Mascota(s): ".plus(product.pet_type)
                txtPrice.text = "$ ".plus(product.price)
                Glide.with(context).load(product.photo_url).into(binding.imgProduct)
                txtCantity.text = "[".plus(product.times_rating).plus("]")

                var rating = 0f
                if (product.times_rating == 0) {
                    ratingBar.rating = 0f
                } else {
                    rating = (product.total_rating!! / product.times_rating).toFloat()
                    ratingBar.rating = 0f
                }

                ratingBar.rating = rating

                imgProduct.setOnClickListener {
                    CreateProductPhotoDialog(context, product.name, product.photo_url)
                }
                btnAddCart.setOnClickListener { addProductToCart(product.id) }

                btnFavorite.setOnClickListener {
                    if (btnFavorite.isChecked) {
                        onFavoriteClick(product.id, true)
                    } else {
                        onFavoriteClick(product.id, false)
                    }
                }
            }
        }
    }

}