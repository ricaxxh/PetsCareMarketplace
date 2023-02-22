package com.softgames.petscare.base

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.softgames.petscare.presentation.register.view.RegisterActivity
import java.time.Duration

abstract class BaseFragment<VB : ViewBinding> : Fragment() {

    protected lateinit var binding: VB

    abstract fun setView(p0: LayoutInflater, p1: ViewGroup?): VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = setView(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recoverData()
        main()
        launchEvents()
    }

    protected open fun recoverData() {}

    protected abstract fun main()

    protected open fun launchEvents() {}

    protected open fun valideTextBoxes() = false

    protected open fun saveUiData() {}

    protected fun message(message: Any, duration: Int = Toast.LENGTH_LONG) {
        Toast.makeText(context, message.toString(), duration).show()
    }

    override fun onPause() {
        saveUiData()
        super.onPause()
    }

}