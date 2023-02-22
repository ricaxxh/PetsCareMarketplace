package com.softgames.petscare.presentation.login.view

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.softgames.petscare.R
import com.softgames.petscare.base.BaseFragment
import com.softgames.petscare.databinding.FragmentLoginBinding
import com.softgames.petscare.presentation.menu.consumer.MenuConsumerActivity
import com.softgames.petscare.presentation.menu.seller.view.MenuSellerActivity
import com.softgames.petscare.presentation.register.view.RegisterActivity
import com.softgames.petscare.services.firebase_auth.AuthService
import com.softgames.petscare.util.text
import kotlinx.coroutines.launch

class LoginFragment : BaseFragment<FragmentLoginBinding>() {
    override fun setView(p0: LayoutInflater, p1: ViewGroup?) =
        FragmentLoginBinding.inflate(p0, p1, false)

    override fun main() {}

    override fun launchEvents() {
        binding.apply {

            tbxCountry.setOnClickListener {

            }

            btnGoogle.setOnClickListener {
                signInWithGoogle()
            }

            btnNext.setOnClickListener {
                if (valideTextBoxes()) {
                    navigateToPhoneCodeScreen()
                }
            }
        }
    }

    override fun valideTextBoxes(): Boolean {
        binding.apply {
            if (tbxPhone.text.isEmpty()) {
                tbxPhone.error = "Ingresa tu número telefónico."
                return false
            } else tbxPhone.error = null

            if (tbxPhone.text.length < 10) {
                tbxPhone.error = "Ingresa los 10 dígitos del teléfono."
                return false
            } else tbxPhone.error = null
        }
        return true
    }

    private fun signInWithGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(resources.getString(R.string.web_client_id))
            .requestEmail().build()
        val client = GoogleSignIn.getClient(requireContext(), gso)
        signInWithGoogleResult.launch(client.signInIntent)
    }

    private fun signInPetscare(credential: AuthCredential) {
        viewLifecycleOwner.lifecycleScope.launch {
            Log.d("IOTEC", "SIGN IN")
            AuthService.signInWithCredential(credential).collect { task ->
                if (task.isSuccessful) {
                    Log.d("IOTEC", "AUTH TRUE")
                    checkIfUserExist(task.result.user!!.uid)
                } else {
                    Log.d("IOTEC", "AUTH FALSE")
                    message("Error al iniciar sesión: ${task.exception!!.message}")
                }
            }
        }
    }

    private fun checkIfUserExist(user_id: String) {
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

    private fun navigateToRegisterScreen(user_id: String) {
        startActivity(
            Intent(requireContext(), RegisterActivity::class.java)
                .putExtra("USER_ID", user_id)
        )
    }

    private fun navigateToMenuSellerScreen() {
        startActivity(Intent(requireContext(), MenuSellerActivity::class.java))
        requireActivity().finish()
    }

    private fun navigateToMenuConsumerScreen() {
        startActivity(Intent(requireContext(), MenuConsumerActivity::class.java))
        requireActivity().finish()
    }

    private fun navigateToPhoneCodeScreen() {
        setFragmentResult("AUTH_DATA", bundleOf("PHONE_NUMBER" to binding.tbxPhone.text))
        findNavController().navigate(R.id.NAV_LOGIN_TO_PHONE_CODE)
    }

    private val signInWithGoogleResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val auth_task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val credential = GoogleAuthProvider
                    .getCredential(auth_task.result.idToken, null)
                signInPetscare(credential)
            } catch (e: Exception) {

            }
        }
}