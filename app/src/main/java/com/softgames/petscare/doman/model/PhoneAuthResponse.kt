package com.softgames.petscare.doman.model

import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

sealed class PhoneAuthResponse {

    data class AUTOMATIC_VERIFICATION(
        val credential: PhoneAuthCredential
    ) : PhoneAuthResponse()

    data class ON_CODE_SEND(
        val verification_id: String,
        val token: PhoneAuthProvider.ForceResendingToken
    ) : PhoneAuthResponse()

    data class FAILURE(
        val exception: Exception
    ) : PhoneAuthResponse()

    data class ON_SIGN_IN(
        val task: Task<AuthResult>
    ) : PhoneAuthResponse()
}
