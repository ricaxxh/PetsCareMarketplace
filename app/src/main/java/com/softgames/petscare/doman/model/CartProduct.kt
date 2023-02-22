package com.softgames.petscare.doman.model

import com.google.firebase.firestore.DocumentSnapshot

data class CartProduct(val product: Product, var quantity: Long)

