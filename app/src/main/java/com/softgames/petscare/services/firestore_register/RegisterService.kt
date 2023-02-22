package com.softgames.petscare.services.firestore_register

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.softgames.petscare.doman.model.Company
import com.softgames.petscare.doman.model.Consumer
import com.softgames.petscare.doman.model.Seller
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

class RegisterService {
    companion object {
        fun registerConsumer(consumer: Consumer) = callbackFlow {
            Firebase.firestore.collection("Users")
                .document(consumer.id).set(consumer)
                .addOnSuccessListener { trySend(true); close() }
                .addOnFailureListener { trySend(false); close() }
            awaitClose()
        }

        fun registerSeller(seller: Seller) = callbackFlow {
            Firebase.firestore.collection("Users")
                .document(seller.id).set(seller)
                .addOnSuccessListener { trySend(true); close() }
                .addOnSuccessListener { trySend(false); close() }
            awaitClose()
        }

        fun registerCompany(company: Company) = callbackFlow {
            Firebase.firestore.collection("Companys").add(company)
                .addOnSuccessListener { trySend(it); close() }
            awaitClose()
        }

    }
}