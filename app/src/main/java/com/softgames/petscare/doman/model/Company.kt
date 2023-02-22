package com.softgames.petscare.doman.model

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude

data class Company(
    @get: Exclude
    val id: String,
    val name: String,
    val category: String
)

fun DocumentSnapshot.toCompany() =
    Company(
        id = id,
        name = getString("name")!!,
        category = getString("category")!!
    )
