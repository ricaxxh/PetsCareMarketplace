package com.softgames.petscare.util

import android.app.Activity
import android.content.Context
import android.view.WindowManager
import android.widget.Toast
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.material.textfield.TextInputLayout

var TextInputLayout.text: String
    get() = editText?.text.toString()
    set(value) {
        editText?.setText(value)
    }

fun message(context: Context, text: String, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(context, text, duration).show()
}

fun Activity.changeStatusBarColor(color: Int, is_light: Boolean) {
    //window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.statusBarColor = color
    WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = is_light
}
