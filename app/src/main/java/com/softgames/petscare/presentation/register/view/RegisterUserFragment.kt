package com.softgames.petscare.presentation.register.view

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.softgames.petscare.R
import com.softgames.petscare.base.BaseFragment
import com.softgames.petscare.databinding.FragmentRegisterUserBinding
import com.softgames.petscare.doman.model.Consumer
import com.softgames.petscare.presentation.menu.seller.view.MenuSellerActivity
import com.softgames.petscare.services.firestore_register.RegisterService
import com.softgames.petscare.util.text
import kotlinx.coroutines.launch
import com.softgames.petscare.doman.model.Seller
import com.softgames.petscare.presentation.menu.consumer.MenuConsumerActivity

class RegisterUserFragment : BaseFragment<FragmentRegisterUserBinding>() {
    override fun setView(p0: LayoutInflater, p1: ViewGroup?) =
        FragmentRegisterUserBinding.inflate(p0, p1, false)

    private var option_selected = 0
    private lateinit var user_id: String
    private var phone_number: String? = null

    override fun recoverData() {
        user_id = requireActivity().intent.extras!!.getString("USER_ID")!!
        phone_number = requireActivity().intent.extras!!.getString("PHONE_NUMBER")

        Log.d("IOTEC", "USER_ID: $user_id")
        Log.d("IOTEC", "PHONE NUMBER: $phone_number")
    }

    override fun main() {
        setupUserSelector(0)
    }

    override fun launchEvents() {
        binding.apply {

            btnNext.setOnClickListener {
                if (valideTextBoxes()) {
                    Log.d("IOTEC", "SAVE: $option_selected")
                    when (option_selected) {
                        0 -> registerConsumer()
                        1 -> navigateToRegisterCompanyScreen()
                    }
                }
            }

            opcClient.setOnClickListener {
                setupUserSelector(0)
            }

            opcSeller.setOnClickListener {
                setupUserSelector(1)
            }
        }
    }

    private fun setupUserSelector(option: Int) {
        binding.apply {
            when (option) {
                0 -> {
                    opcClient.isChecked = true
                    opcClient.strokeWidth = 8
                    opcClient.strokeColor =
                        ContextCompat.getColor(requireContext(), R.color.primary)
                    opcSeller.isChecked = false
                    opcSeller.strokeWidth = 2
                    opcSeller.strokeColor =
                        ContextCompat.getColor(requireContext(), R.color.gray)
                    option_selected = 0
                    Log.d("IOTEC", "CLICK OPC: $option")
                }

                1 -> {
                    opcClient.isChecked = false
                    opcClient.strokeWidth = 2
                    opcClient.strokeColor =
                        ContextCompat.getColor(requireContext(), R.color.gray)
                    opcSeller.isChecked = true
                    opcSeller.strokeWidth = 8
                    opcSeller.strokeColor =
                        ContextCompat.getColor(requireContext(), R.color.primary)
                    option_selected = 1
                    Log.d("IOTEC", "CLICK OPC: $option")
                }
            }

        }
    }

    private fun registerConsumer() {

        Log.d("IOTEC", "REGISTER COSUMER: $user_id | $binding.tbxName.text | $phone_number")

        val consumer = Consumer(
            id = user_id,
            name = binding.tbxName.text,
            phone_number = phone_number,
            user_type = "Consumer"
        )

        viewLifecycleOwner.lifecycleScope.launch {
            RegisterService.registerConsumer(consumer).collect { success ->
                if (success) navigateToMenuConsumerScreen()
                else message("Ocurrio un error al registrar al usuario.")
            }
        }
    }

    private fun navigateToMenuConsumerScreen() {
        startActivity(
            Intent(requireContext(), MenuConsumerActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
    }

    private fun navigateToRegisterCompanyScreen() {

        Log.d("IOTEC", "REGISTER COSUMER: $user_id | $binding.tbxName.text | $phone_number")

        val seller = Seller(
            id = user_id,
            name = binding.tbxName.text,
            company_id = "",
            user_type = "Seller",
            phone_number = phone_number,
            company_name = ""
        )

        setFragmentResult("SELLER_DATA", bundleOf("SELLER" to seller))
        findNavController().navigate(R.id.NAV_REGISTER_USER_TO_REGISTER_COMPANY)
    }

    override fun valideTextBoxes(): Boolean {
        binding.apply {
            if (tbxName.text.isEmpty()) {
                tbxName.error = "Ingrese su nombre."
                message("ERROR")
                return false
            } else tbxName.error = null
        }
        return true
    }
}