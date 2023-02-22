package com.softgames.petscare.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import com.yalantis.ucrop.UCrop

class CropImageUtil : ActivityResultContract<Pair<Uri, Uri>, Uri?>() {

    override fun createIntent(context: Context, input: Pair<Uri, Uri>): Intent =
        UCrop.of(input.first, input.second)
            .withOptions(opcionesRecorte())
            .getIntent(context)

    private fun opcionesRecorte(): UCrop.Options {
        val crop_options = UCrop.Options()
        crop_options.withAspectRatio(1f, 1f)
        crop_options.withMaxResultSize(1000, 1000)
        crop_options.setCompressionQuality(70)
        crop_options.setHideBottomControls(false)
        crop_options.setShowCropGrid(false)
        crop_options.setFreeStyleCropEnabled(false)
        crop_options.setStatusBarColor(Color.WHITE)
        crop_options.setToolbarColor(Color.WHITE)
        crop_options.setToolbarTitle("Recortar foto")
        return crop_options
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        if (resultCode != Activity.RESULT_OK || intent == null) return null
        return UCrop.getOutput(intent)
    }
}