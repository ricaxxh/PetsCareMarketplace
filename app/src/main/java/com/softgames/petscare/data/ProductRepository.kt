package com.softgames.petscare.data

import android.util.Log
import com.airbnb.lottie.L
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.softgames.petscare.doman.model.CartProduct
import com.softgames.petscare.doman.model.Product
import com.softgames.petscare.doman.model.toProduct
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow

class ProductRepository {

    companion object {

        fun registerProduct(product: Product) = callbackFlow {
            Firebase.firestore.collection("Products").add(product)
                .addOnSuccessListener { trySend(true); close() }
            awaitClose()
        }

        fun getCompanyProductList(company_id: String) = callbackFlow<List<Product>> {
            val product_list = ArrayList<Product>()
            Firebase.firestore.collection("Products").whereEqualTo("company_id", company_id)
                .addSnapshotListener { value, error ->
                    for (document in value!!.documents) {
                        val product = document.toProduct()
                        product_list.add(product)
                    }
                    trySend(product_list); close()
                }
            awaitClose()
        }

        fun getAllProductList() = callbackFlow<List<Product>> {
            val product_list = ArrayList<Product>()
            Firebase.firestore.collection("Products").addSnapshotListener { value, error ->
                for (document in value!!.documents) {
                    val product = document.toProduct()
                    product_list.add(product)
                }
                trySend(product_list); close()
            }
            awaitClose()
        }

        fun addProductToShoppingCart(user_id: String, product_id: String) {
            Firebase.firestore.collection("Users").document(user_id).update(
                mapOf("shopping_cart.$product_id" to FieldValue.increment(1))
            )
        }

        fun subtractProductFromShoppingCart(user_id: String, product_id: String) {
            Firebase.firestore.collection("Users").document(user_id).update(
                mapOf("shopping_cart.$product_id" to FieldValue.increment(-1))
            )
        }

        fun setPurchasesToProduct(product_id: String, queantity: Long) {
            Firebase.firestore.collection("Products")
                .document(product_id).update("purchases", FieldValue.increment(queantity))
        }

        fun clearShoppingCart(user_id: String) {
            Firebase.firestore.collection("Users").document(user_id).update(
                mapOf("shopping_cart" to FieldValue.delete())
            )
        }

        fun addProductToFavorites(product_id: String) {
            Firebase.firestore.collection("Products")
                .document(product_id).update("favorites", FieldValue.increment(1))
        }

        fun removeProductFromFavorites(product_id: String) {
            Firebase.firestore.collection("Products")
                .document(product_id).update("favorites", FieldValue.increment(-1))
        }

        fun addPurchaseToHistory(user_id: String, product_id: String, quantity: Long) {
            Firebase.firestore.collection("Users").document(user_id).update(
                mapOf("purchases.$product_id" to FieldValue.increment(quantity))
            )
        }

        fun ratingProduct(product_id: String, rating: Float) {
            val database = Firebase.firestore
            val doc_ref = database.collection("Products").document(product_id)
            Log.d("RATING", "profucy: $product_id")
            database.runTransaction { transaction ->
                val product_doc = transaction.get(doc_ref)
                val total_rating = (product_doc.get("total_rating") as Number).toDouble()
                val new_rating = (total_rating + rating)
                transaction.update(doc_ref, "total_rating", new_rating)
                transaction.update(doc_ref, "times_ratings", FieldValue.increment(1))
            }.addOnSuccessListener { Log.d("RATING", "Transaction success!") }
                .addOnFailureListener { e -> Log.d("RATING", "Transaction failure.", e) }
        }
    }
}