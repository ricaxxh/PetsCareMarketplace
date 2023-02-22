package com.softgames.petscare.services.firebase_auth

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.softgames.petscare.doman.model.PhoneAuthResponse
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import java.util.concurrent.TimeUnit

class PhoneAuthService {
    companion object {

        private val auth = Firebase.auth

        fun sendVerificationCode(
            phone_number: String,
            activity: Activity
        ) = callbackFlow {

            val callback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    trySend(PhoneAuthResponse.AUTOMATIC_VERIFICATION(credential))
                    close()
                }

                override fun onVerificationFailed(exception: FirebaseException) {
                    trySend(PhoneAuthResponse.FAILURE(exception))
                    close()
                }

                override fun onCodeSent(
                    verification_id: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    trySend(PhoneAuthResponse.ON_CODE_SEND(verification_id, token))
                    close()
                }
            }

            val options = buildOptions(phone_number, activity, callback)
            auth.setLanguageCode("es-mx")
            PhoneAuthProvider.verifyPhoneNumber(options)
            awaitClose()
        }

        private fun buildOptions(
            phone_number: String,
            activity: Activity,
            callback: PhoneAuthProvider.OnVerificationStateChangedCallbacks
        ): PhoneAuthOptions {
            return PhoneAuthOptions.newBuilder()
                .setPhoneNumber(phone_number)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(callback)
                .build()
        }

        fun sighInWithPhoneAuthCredential(credential: PhoneAuthCredential) = callbackFlow {
            auth.signInWithCredential(credential)
                .addOnCompleteListener {
                    trySend(PhoneAuthResponse.ON_SIGN_IN(it))
                    close()
                }
                .addOnFailureListener {
                    trySend(PhoneAuthResponse.FAILURE(it))
                    close()
                }
            awaitClose()
        }

        fun getCredential(code: String, verification_id: String) =
            PhoneAuthProvider.getCredential(verification_id, code)
    }
}