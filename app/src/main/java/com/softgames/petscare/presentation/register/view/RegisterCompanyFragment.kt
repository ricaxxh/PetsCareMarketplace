package com.softgames.petscare.presentation.register.view

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import com.softgames.petscare.base.BaseFragment
import com.softgames.petscare.databinding.FragmentRegisterCompanyBinding
import com.softgames.petscare.doman.model.Company
import com.softgames.petscare.doman.model.Seller
import com.softgames.petscare.presentation.menu.seller.view.MenuSellerActivity
import com.softgames.petscare.services.firestore_register.RegisterService
import com.softgames.petscare.util.text
import kotlinx.coroutines.launch

class RegisterCompanyFragment : BaseFragment<FragmentRegisterCompanyBinding>() {
    override fun setView(p0: LayoutInflater, p1: ViewGroup?) =
        FragmentRegisterCompanyBinding.inflate(p0, p1, false)

    private lateinit var seller: Seller

    override fun recoverData() {
        setFragmentResultListener("SELLER_DATA") { _, bundle ->
            seller = bundle.getSerializable("SELLER") as Seller
        }
    }

    override fun main() {}

    override fun launchEvents() {
        binding.apply {

            btnFinish.setOnClickListener {
                if (valideTextBoxes()) registerCompany()
            }
        }
    }

    private fun registerCompany() {

        val company = Company(
            id = "",
            name = binding.tbxName.text,
            category = binding.tbxCategory.text
        )

        viewLifecycleOwner.lifecycleScope.launch {
            RegisterService.registerCompany(company).collect { document ->
                seller.company_id = document.id
                registerSeller()
            }
        }
    }

    private fun registerSeller() {

        viewLifecycleOwner.lifecycleScope.launch {
            RegisterService.registerSeller(seller).collect { success ->
                if (success) navigateToMenuScreen()
                else message("Error al registrar al vendedor")
            }
        }
    }

    private fun navigateToMenuScreen() {
        startActivity(Intent(context, MenuSellerActivity::class.java))
        requireActivity().finish()
    }

    override fun valideTextBoxes(): Boolean {
        binding.apply {

            if (tbxName.text.isEmpty()) {
                tbxName.error = "Ingrese el nombre de la compañia."
                return false
            } else tbxName.error = null

            if (spiCategory.text.isEmpty()) {
                tbxCategory.error = "Seleccione una categoria."
                return false
            } else tbxCategory.error = null

            return true
        }
    }
}