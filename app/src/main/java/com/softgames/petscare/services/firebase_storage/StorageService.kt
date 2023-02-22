package com.softgames.petscare.services.firebase_storage

import android.net.Uri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import java.util.*

class StorageService {
    companion object {
        fun uploadImage(uri: Uri, company_id: String) = callbackFlow {
            Firebase.storage.reference.child("Productos").child(company_id)
                .child(UUID.randomUUID().toString()).putFile(uri)
                .addOnSuccessListener { task ->
                    task.storage.downloadUrl.addOnSuccessListener { trySend(it); close() }
                }
            awaitClose()
        }
    }
}