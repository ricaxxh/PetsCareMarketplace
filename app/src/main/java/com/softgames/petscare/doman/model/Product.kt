package com.softgames.petscare.doman.model

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude

data class Product(
    @get:Exclude
    val id: String,
    val name: String,
    var photo_url: String?,
    val pet_type: String,
    val category: String,
    val stock: Int,
    val price: Float,
    val company_id: String,
    val company_name: String,
    var times_rating: Int = 0,
    var favorites: Int? = 0,
    var total_rating: Float? = 0f,
    var purchases: Int? = 0,
)

fun DocumentSnapshot.toProduct() =
    Product(
        id = id,
        name = getString("name")!!,
        photo_url = getString("photo_url")!!,
        pet_type = getString("pet_type")!!,
        category = getString("category")!!,
        stock = getLong("stock")!!.toInt(),
        price = getDouble("price")!!.toFloat(),
        company_id = getString("company_id")!!,
        company_name = getString("company_name")!!,
        times_rating = (getLong("times_ratings")?.toInt() ?:0),
        favorites = (getLong("favorites")?.toInt() ?: 0),
        total_rating = (getLong("total_rating")?.toFloat() ?: 0f),
        purchases = (getLong("purchases")?.toInt() ?: 0)
    )
