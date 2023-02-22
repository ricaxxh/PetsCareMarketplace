package com.softgames.petscare.base

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.softgames.petscare.R
import java.time.Duration

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    protected lateinit var binding: VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(setActivityTheme())
        binding = setView()
        setContentView(binding.root)
        main()
        launchEvents()
    }

    protected abstract fun setView(): VB

    open fun setActivityTheme(theme: Int = R.style.THEME_IOTEC) = theme

    protected abstract fun main()

    protected open fun launchEvents() {}

    protected open fun valideTextBoxes() = false

    protected open fun saveData() {}

    protected fun message(message: Any, duration: Int = Toast.LENGTH_LONG) {
        Toast.makeText(this, message.toString(), duration).show()
    }

}
