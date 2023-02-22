package com.softgames.petscare.presentation.others.dialogs

import android.content.Context
import android.view.LayoutInflater
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.softgames.petscare.databinding.DialogPhotoProductBinding
import com.softgames.petscare.util.message

fun CreateProductPhotoDialog(context: Context, name: String, photo_url: String?) {

    val dialog = MaterialAlertDialogBuilder(context)
    val binding = DialogPhotoProductBinding.inflate(
        LayoutInflater.from(context), null, false
    )
    binding.apply {
        txtProductName.text = name
        Glide.with(context).load(photo_url).into(binding.imgPhoto)

        btnDownload.setOnClickListener { }
        btnShare.setOnClickListener { }
        btnInfo.setOnClickListener { }
    }
    dialog.setView(binding.root)
    dialog.show()
}