package com.softgames.petscare.presentation.menu.consumer.cart

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.softgames.petscare.databinding.ItemCartProductBinding
import com.softgames.petscare.doman.model.CartProduct
import com.softgames.petscare.util.text

class CartAdapter(
    private val context: Context,
    private val product_list: ArrayList<CartProduct>,
    private val onIncreaseClick: (CartProduct, Int) -> Unit,
    private val onDecreaseClick: (CartProduct, Int) -> Unit,
) : RecyclerView.Adapter<CartAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Holder(ItemCartProductBinding.inflate(LayoutInflater.from(context), parent, false))

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(product_list[position])
    }

    override fun getItemCount() = product_list.size

    inner class Holder(private val binding: ItemCartProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CartProduct) {
            binding.apply {
                txtProductName.text = item.product.name
                Glide.with(context).load(item.product.photo_url).into(imgProduct)
                tbxCantity.text = item.quantity.toString()
                txtPrice.text = "$ ${item.product.price}"

                txtCantity.text = "[".plus(item.product.times_rating).plus("]")

                var rating = 0f
                Log.d("IOTEC",item.product.times_rating.toString())
                if (item.product.times_rating == 0) {
                    ratingBar.rating = 0f
                } else {
                    rating = (item.product.total_rating!! / item.product.times_rating).toFloat()
                    ratingBar.rating = rating
                }

                btnPlus.setOnClickListener {
                    val quantity = tbxCantity.text.toInt()
                    tbxCantity.text = (quantity + 1).toString()
                    onIncreaseClick(item, absoluteAdapterPosition)
                }

                btnMinus.setOnClickListener {
                    val quantity = tbxCantity.text.toInt()
                    if (quantity > 1) {
                        tbxCantity.text = (quantity - 1).toString()
                        onDecreaseClick(item, absoluteAdapterPosition)
                    }
                }
            }
        }
    }
}