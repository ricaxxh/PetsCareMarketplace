package com.softgames.petscare.doman.model

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude

data class Consumer(
    @get:Exclude
    val id: String,
    var name: String,
    val user_type: String,
    var phone_number: String?,
    var shopping_cart: Map<*, *>? = null,
    var purchases: Map<*, *>? = null
)

fun DocumentSnapshot.toConsumer() =
    Consumer(
        id = id,
        name = getString("name")!!,
        user_type = getString("user_type")!!,
        phone_number = getString("phone_number"),
        shopping_cart = get("shopping_cart") as Map<*, *>?,
        purchases = get("purchases") as Map<*, *>?
    )