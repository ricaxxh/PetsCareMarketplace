package com.softgames.petscare.presentation.menu.consumer.purchases

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.softgames.petscare.databinding.ItemPurchasesBinding
import com.softgames.petscare.doman.model.CartProduct

class PurchasesAdapter(
    private val context: Context,
    private val product_list: ArrayList<CartProduct>,
    private val OnRanting: (String, Float) -> Unit
) : RecyclerView.Adapter<PurchasesAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Holder(ItemPurchasesBinding.inflate(LayoutInflater.from(context), parent, false))

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(product_list[position])
    }

    override fun getItemCount() = product_list.size

    inner class Holder(private val binding: ItemPurchasesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CartProduct) {
            binding.apply {
                txtProductName.text = item.product.name
                Glide.with(context).load(item.product.photo_url).into(imgProduct)
                txtCantity.text = "(${item.quantity} Unidades)"
                txtPrice.text = "$ ${item.product.price}"

                ratingBar.setOnRatingBarChangeListener { ratingBar, value, b ->
                    OnRanting(item.product.id, value)
                }
            }
        }
    }
}