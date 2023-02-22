package com.softgames.petscare.doman.model

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import java.io.Serializable

data class Seller(
    @get:Exclude
    val id: String,
    var name: String,
    var company_id: String,
    val company_name: String,
    val user_type: String,
    var phone_number: String?
) : Serializable

fun DocumentSnapshot.toSeller() =
    Seller(
        id = id,
        name = getString("name")!!,
        company_name = getString("company_name")!!,
        company_id = getString("company_id")!!,
        user_type = getString("user_type")!!,
        phone_number = getString("phone_number")
    )
