package com.softgames.petscare.services.firebase_auth

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

class AuthService {
    companion object {

        fun getUserById(user_id: String) = callbackFlow {
            Firebase.firestore.collection("Users").document(user_id).get()
                .addOnSuccessListener { trySend(it); close() }
            awaitClose()
        }

        fun signInWithCredential(credential: AuthCredential) = callbackFlow<Task<AuthResult>> {
            Firebase.auth.signInWithCredential(credential)
                .addOnCompleteListener {
                    trySend(it)
                    close()
                }
            awaitClose()
        }
    }
}