package com.softgames.petscare.presentation.login.view

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.softgames.petscare.base.BaseFragment
import com.softgames.petscare.databinding.FragmentPhoneCodeBinding
import com.softgames.petscare.doman.model.PhoneAuthResponse
import com.softgames.petscare.doman.user_case.CaseAuth
import com.softgames.petscare.presentation.menu.consumer.MenuConsumerActivity
import com.softgames.petscare.presentation.menu.seller.view.MenuSellerActivity
import com.softgames.petscare.presentation.register.view.RegisterActivity
import com.softgames.petscare.services.firebase_auth.AuthService
import com.softgames.petscare.services.firebase_auth.PhoneAuthService
import com.softgames.petscare.util.text
import kotlinx.coroutines.launch

class PhoneCodeFragment : BaseFragment<FragmentPhoneCodeBinding>() {
    override fun setView(p0: LayoutInflater, p1: ViewGroup?) =
        FragmentPhoneCodeBinding.inflate(p0, p1, false)

    private lateinit var phone_number: String
    private lateinit var verification_id: String
    private lateinit var token: PhoneAuthProvider.ForceResendingToken
    private var codeWasSend = false

    override fun recoverData() {
        setFragmentResultListener("AUTH_DATA") { _, bundle ->
            phone_number = bundle.getString("PHONE_NUMBER")!!
            binding.txtPhoneNumber.text = "+52 ".plus(phone_number)
            sendVerificationCode()
        }
    }

    override fun main() {}

    override fun launchEvents() {
        binding.btnLogin.setOnClickListener {

            //CHECK IF CODE WAS SEND
            if (codeWasSend) {

                //CREATE AUTH CREDENTIAL
                val credential = PhoneAuthService.getCredential(
                    code = binding.tbxCode.text,
                    verification_id = verification_id
                )

                //SIGN IN WITH AUTH CREDENTIAL
                signInWhenCredential(credential)
            } else message("Espere a que se envie el código de verificación.")
        }
    }

    private fun sendVerificationCode() {

        //CREATE A COURUTINE
        viewLifecycleOwner.lifecycleScope.launch {

            //SEND VERIFICATION CODE
            PhoneAuthService.sendVerificationCode(
                phone_number = "+52${phone_number}",
                activity = requireActivity()
            ).collect { response ->
                when (response) {
                    is PhoneAuthResponse.AUTOMATIC_VERIFICATION -> {
                        //SET SMS CODE IN TBX_CODE
                        binding.tbxCode.text = response.credential.smsCode!!
                        message("¡Verificacion automatica exitosa!")
                        signInWhenCredential(response.credential)
                    }
                    is PhoneAuthResponse.ON_CODE_SEND -> {
                        message("Se envió el código de verificación")
                        verification_id = response.verification_id
                        token = response.token
                        codeWasSend = true
                    }
                    is PhoneAuthResponse.FAILURE -> showMessageError(response.exception)
                    else -> {}
                }
            }
        }
    }

    private fun signInWhenCredential(credential: PhoneAuthCredential) {

        //CREATE A COURUTINE
        viewLifecycleOwner.lifecycleScope.launch {
            PhoneAuthService.sighInWithPhoneAuthCredential(
                credential = credential
            ).collect { response ->
                when (response) {

                    //CHECK RESPONSE TYPE
                    is PhoneAuthResponse.ON_SIGN_IN -> {

                        //CHECK IF TASK IS SUCCESSFUL
                        if (response.task.isSuccessful) {

                            //GET USER FROM TASK
                            val user = response.task.result!!.user!!

                            //SHOW MESSAGE TO USER
                            message("Se inicio sesión con exito.")
                            binding.tbxCode.error = null

                            //CHECK IF USER IS EXIST
                            checkIfUserExists(user.uid)
                        } else {
                            showMessageError(response.task.exception!!)
                        }
                    }

                    is PhoneAuthResponse.FAILURE -> showMessageError(response.exception)

                    else -> {}
                }
            }
        }
    }

    private fun showMessageError(exception: Exception) {
        if (exception is FirebaseAuthInvalidCredentialsException) {
            binding.tbxCode.error = "Código incorrecto"
        } else message(CaseAuth.getAuthError(exception))
    }

    private fun checkIfUserExists(user_id: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            AuthService.getUserById(user_id).collect { user ->
                if (user.exists()) {

                    val user_type = user.getString("user_type")
                    if (user_type.equals("Seller")) navigateToMenuSellerScreen()
                    else if (user_type.equals("Consumer")) navigateToMenuConsumerScreen()

                } else navigateToRegisterScreen(user_id)
            }
        }
    }

    private fun navigateToMenuSellerScreen() {
        startActivity(Intent(context, MenuSellerActivity::class.java))
        requireActivity().finish()
    }

    private fun navigateToMenuConsumerScreen() {
        startActivity(Intent(context, MenuConsumerActivity::class.java))
        requireActivity().finish()
    }

    private fun navigateToRegisterScreen(user_id: String) {
        startActivity(
            Intent(requireActivity(), RegisterActivity::class.java)
                .putExtra("USER_ID", user_id)
                .putExtra("PHONE_NUMBER", phone_number)
        )
        requireActivity().finish()
    }
}