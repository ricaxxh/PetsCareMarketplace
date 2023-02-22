package com.softgames.petscare.doman.user_case

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import java.lang.Exception

class CaseAuth {
    companion object {
        fun getAuthError(exception: Exception) =
            when (exception) {
                is FirebaseAuthInvalidCredentialsException ->
                    "Código incorrecto."
                is FirebaseNetworkException ->
                    "No hay conexión a Internet."
                else -> "${exception.message}"
            }
    }
}