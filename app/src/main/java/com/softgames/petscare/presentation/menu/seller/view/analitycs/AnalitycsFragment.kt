package com.softgames.petscare.presentation.menu.seller.view.analitycs

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.softgames.petscare.databinding.DialogProductDetailBinding
import com.softgames.petscare.databinding.FragmentAnalitycsBinding
import com.softgames.petscare.doman.model.Product
import com.softgames.petscare.doman.model.Seller
import com.softgames.petscare.doman.model.toProduct
import com.softgames.petscare.doman.model.toSeller
import ir.mahozad.android.PieChart
import kotlin.collections.ArrayList

class AnalitycsFragment : Fragment() {

    private var _binding: FragmentAnalitycsBinding? = null
    private val binding get() = _binding!!
    private lateinit var seller: Seller

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalitycsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Firebase.firestore.collection("Users").document(Firebase.auth.uid!!).get()
            .addOnSuccessListener {
                seller = it.toSeller()
                setupCharts()
            }
    }

    fun setupCharts() {
        val colors = listOf(
            Color.RED,
            Color.BLUE,
            Color.parseColor("#00CA68"),
            Color.parseColor("#FFA500"),
            Color.GRAY,
            Color.MAGENTA
        )
        val colors2 = listOf(
            Color.GRAY,
            Color.BLUE,
            Color.parseColor("#00CA68"),
            Color.parseColor("#FFA500"),
            Color.RED
        )

        //MOST SELLED PRODUCTS ----------------------------------------------------------------------------------------------------
        Firebase.firestore.collection("Products")
            .orderBy("purchases", Query.Direction.DESCENDING)
            .addSnapshotListener { values, error ->
                val products = ArrayList<Product>()
                for (value in values!!.documents) {
                    if (products.size < 5) {
                        val product = value.toProduct()
                        if (product.company_id == seller.company_id) {
                            products.add(product)
                        }
                    }
                }

                products.sortBy { it.purchases }

                val data_charts = ArrayList<Pair<String, Float>>()
                products.map { product ->
                    data_charts.add(Pair(product.name, product.purchases!!.toFloat()))
                }

                binding.mostSelledProducts.animation.duration = 1000
                binding.mostSelledProducts.barsColorsList = colors2
                binding.mostSelledProducts.animate(data_charts)
                binding.mostSelledProducts.onDataPointTouchListener = { index, _, _ ->
                    showProductsDetailsDialog(
                        data_charts[index].first,
                        "Cantidad vendida: ${(data_charts[index].second).toInt()}",
                        products[index].photo_url!!
                    )
                }
            }


        //MOST VALUED PRODUCTS ----------------------------------------------------------------------------------------------------
        Firebase.firestore.collection("Products")
            .orderBy("total_rating", Query.Direction.DESCENDING)
            .addSnapshotListener { values, error ->
                val products = ArrayList<Pair<Product, Float>>()
                for (value in values!!.documents) {
                    if (products.size < 5) {
                        val product = value.toProduct()
                        if (product.company_id == seller.company_id) {
                            var rating = 0f
                            if (product.times_rating != 0) {
                                rating = product.total_rating!! / product.times_rating
                            }
                            products.add(Pair(product, rating))
                        }
                    }
                }

                products.sortBy { it.second }

                val data_charts = ArrayList<Pair<String, Float>>()
                products.map { product ->
                    data_charts.add(Pair(product.first.name, product.second))
                }

                binding.topRatedProducts.animation.duration = 1000
                binding.topRatedProducts.barsColorsList = colors2
                binding.topRatedProducts.animate(data_charts)
                binding.topRatedProducts.onDataPointClickListener = { index, _, _ ->
                    showProductsDetailsDialog(
                        data_charts[index].first, "Calificación: ${data_charts[index].second}",
                        products[index].first.photo_url!!
                    )
                }
            }


        //FAVORITE PRODUCTS --------------------------------------------------------------------------------------------------------
        Firebase.firestore.collection("Products")
            .orderBy("favorites", Query.Direction.DESCENDING).addSnapshotListener { values, error ->
                val products = ArrayList<Pair<String, Float>>()
                var total = 0f
                for (value in values!!.documents) {
                    if (products.size < 5) {
                        val product = value.toProduct()
                        if (product.company_id == seller.company_id) {
                            total += product.favorites!!
                            products.add(Pair(product.name, product.favorites!!.toFloat()))
                        }
                    }
                    colors.shuffled()
                    val slice_list = ArrayList<PieChart.Slice>()
                    repeat(products.size) {
                        val tam = ((products[it].second * 100) / total) / 100
                        Log.d("IOTEC", "$tam")
                        slice_list.add(
                            PieChart.Slice(
                                tam,
                                colors[it],
                                label = (tam * 100).toInt().toString() + "% ${products[it].first}",
                                labelColor = Color.BLACK,
                                outsideLabelMargin = 25f,
                                labelSize = 32f,
                            )
                        )

                    }
                    binding.favoriteProducts.slices = slice_list
                }
            }


        //MOST SELLED CATEGORY  ----------------------------------------------------------------------------------------------------
        Firebase.firestore.collection("Products")
            .orderBy("purchases", Query.Direction.DESCENDING)
            .addSnapshotListener { values, error ->

                val categories = mutableMapOf<String, Float>()
                var total = 0f
                for (value in values!!.documents) {
                    val product = value.toProduct()
                    if (product.company_id == seller.company_id) {
                        if (product.category in categories.keys) {
                            categories[product.category] =
                                categories[product.category]?.plus(product.purchases!!) ?: 0f
                        } else {
                            categories[product.category] = product.purchases!!.toFloat()
                        }
                        total += product.purchases!!
                    }
                }

                val final_list = ArrayList<PieChart.Slice>()
                for ((key, value) in categories) {
                    val tam = ((value * 100) / total) / 100
                    final_list.add(
                        PieChart.Slice(
                            tam,
                            colors.shuffled().first(),
                            label = "${(tam * 100).toInt()}% $key (${value.toInt()})",
                            labelColor = Color.BLACK,
                            labelSize = 32f,
                        )
                    )
                }
                 binding.mostSelledCategory.slices = final_list
            }
    }

    fun showProductsDetailsDialog(name: String, message: String, photo_url: String) {
        val binding =
            DialogProductDetailBinding.inflate(LayoutInflater.from(context), null, false)
        binding.txtProductName.text = name
        binding.txtValue.text = message
        Glide.with(requireContext()).load(photo_url).into(binding.imgPhoto)
        MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
