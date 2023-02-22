package com.softgames.petscare.presentation.menu.seller.view.register_product


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.softgames.petscare.R
import com.softgames.petscare.base.BaseActivity
import com.softgames.petscare.data.ProductRepository
import com.softgames.petscare.data.list.choose_photo_options
import com.softgames.petscare.data.pet_type_list
import com.softgames.petscare.data.product_category_list
import com.softgames.petscare.databinding.ActivityRegisterProductBinding
import com.softgames.petscare.doman.model.Product
import com.softgames.petscare.doman.model.Seller
import com.softgames.petscare.doman.model.toSeller
import com.softgames.petscare.doman.user_case.CasePermissions
import com.softgames.petscare.doman.user_case.CasePermissions.Companion.CAMERA_PERMISSION_CODE
import com.softgames.petscare.presentation.others.adapters.SelectorAdapter
import com.softgames.petscare.services.firebase_auth.AuthService
import com.softgames.petscare.services.firebase_storage.StorageService
import com.softgames.petscare.util.CropImageUtil
import com.softgames.petscare.util.text
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

class RegisterProductActivity : BaseActivity<ActivityRegisterProductBinding>() {
    override fun setView() = ActivityRegisterProductBinding.inflate(layoutInflater)
    override fun setActivityTheme(theme: Int) = R.style.THEME_REGISTER_PRODUCT

    private lateinit var photo_file: File
    private lateinit var photo_path: String
    private lateinit var absolute_photo_path: String
    private var photo_uri: Uri? = null

    private lateinit var seller: Seller

    override fun main() {
        setupSpinnerPetType()
        setupSpinnerCategory()
        getSeller()
    }

    override fun launchEvents() {
        binding.apply {

            toolbar.setOnClickListener {
                finish()
            }
            btnRegister.setOnClickListener {
                if (valideTextBoxes()) registerProduct()
            }
            imgPhoto.setOnClickListener {
                showPhotoSelector()
            }
        }
    }

    private fun getSeller() {
        lifecycleScope.launch {
            val user_id = Firebase.auth.currentUser!!.uid
            CoroutineScope(Dispatchers.IO).launch {
                AuthService.getUserById(user_id).collect {
                    seller = it.toSeller()
                }
            }
        }
    }

    private fun setupSpinnerPetType() {
        binding.spiPetType.setAdapter(
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                pet_type_list
            )
        )
    }

    private fun setupSpinnerCategory() {
        binding.spiCategory.setAdapter(
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                product_category_list
            )
        )
    }

    private fun registerProduct() {

        val product = Product(
            id = "",
            name = binding.tbxName.text,
            photo_url = "",
            category = binding.tbxCategory.text,
            pet_type = binding.tbxPetType.text,
            stock = binding.tbxStock.text.toInt(),
            price = binding.tbxPrice.text.toFloat(),
            company_id = seller.company_id,
            company_name = seller.company_name,
        )

        //UPLOAD IMAGE TO FIREBASE STORAGE
        if (photo_uri != null) {
            lifecycleScope.launch {
                StorageService.uploadImage(photo_uri!!, seller.company_id).collect { photo_url ->
                    product.photo_url = photo_url.toString()

                    //SAVE PRODUCT TO FIREBASE DATABASE
                    lifecycleScope.launch {
                        CoroutineScope(Dispatchers.IO).launch {
                            ProductRepository.registerProduct(product).collect { success ->
                                if (success) finish()
                            }
                        }
                    }
                }
            }
        } else{
            lifecycleScope.launch {
                CoroutineScope(Dispatchers.IO).launch {
                    ProductRepository.registerProduct(product).collect { success ->
                        if (success) finish()
                    }
                }
            }
        }
    }

    override fun valideTextBoxes(): Boolean {
        binding.apply {

            if (tbxName.text.isEmpty()) {
                tbxName.error = "Ingrese el nombre del producto"
                return false
            } else tbxName.error = null;

            if (tbxCategory.text.isEmpty()) {
                tbxCategory.error = "Seleccione una categoria"
                return false
            } else tbxCategory.error = null

            if (tbxStock.text.isEmpty()) {
                tbxStock.error = "Ingrese el stock del producto"
                return false
            } else tbxStock.error = null

            if (tbxPrice.text.isEmpty()) {
                tbxPrice.error = "Ingrese el precio del producto"
                return false
            } else tbxPrice.error = null

            try {
                tbxPrice.text.toFloat()
                tbxPrice.error = null
            } catch (e: Exception) {
                tbxPrice.error = "Ingrese un precio valido"
                return false
            }

            return true
        }
    }

    //CHOSE PHOTO ----------------------------------------------------------------------------------------------------

    private fun showPhotoSelector() {
        MaterialAlertDialogBuilder(this)
            .setCustomTitle(
                layoutInflater.inflate(
                    R.layout.title_dialog_selector,
                    null
                )
            )
            .setAdapter(
                (SelectorAdapter(this, choose_photo_options).list_adapter)
            ) { dialog, index ->
                when (index) {
                    0 -> openCamera()
                    1 -> openGallery()
                    2 -> openFileExplorer()
                    3 -> dialog.dismiss()
                }
            }
            .show()
    }

    private fun openCamera() {
        if (CasePermissions.checkCameraPermission(this)) takePhoto()
        else CasePermissions.requestCameraPermission(this)
    }

    private fun takePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            try {
                photo_file = createPhotoFile()!!
                val photo_uri: Uri = FileProvider.getUriForFile(
                    this, "com.softgames.petscare", photo_file
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photo_uri)
                takePhotoResult.launch(intent)
            } catch (e: Exception) {
                message("Error al crear el archivo de la foto")
                Log.d("IOTEC", e.message!!)
            }
        }
    }

    private fun openGallery() {
        val intent = Intent()
            .setAction(Intent.ACTION_PICK)
            .setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        pickPhotoResult.launch(intent)
    }

    private fun openFileExplorer() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        pickPhotoResult.launch(intent)
    }

    private fun createPhotoFile(): File? {
        val path_directory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File.createTempFile("${Date()}_foto", ".jpg", path_directory)
        photo_path = "file: ${file.absolutePath}"
        absolute_photo_path = file.absolutePath
        return file
    }

    private fun cropPhoto() {
        crop_photo_result.launch(
            Pair(
                Uri.fromFile(photo_file),
                Uri.fromFile(photo_file)
            )
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_PERMISSION_CODE
            && (grantResults.isNotEmpty())
            && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            takePhoto()
        } else {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                showMessagePermissionDenied()
            } else {
                showMessagePermissionFailed()
            }
        }
    }

    private fun showMessagePermissionFailed() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Solicitud de permiso fallida")
            .setMessage(R.string.permissions_failed)
            .setPositiveButton("Aceptar") { dialog, _ ->
                openCamera()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
    }

    private fun showMessagePermissionDenied() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Permiso de camara denegado")
            .setMessage(R.string.permissions_denied)
            .setPositiveButton("Aceptar") { dialog, which ->
                navigateToAppInfoScreen()
            }
            .setNegativeButton("Cancelar") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    private fun navigateToAppInfoScreen() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        camera_permission_result.launch(intent)
    }

    private val camera_permission_result =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (CasePermissions.checkCameraPermission(this)) takePhoto()
            else showMessagePermissionDenied()
        }

    private val takePhotoResult =   //CAMERA
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) cropPhoto()
        }

    private val pickPhotoResult =   //GALLERY AND FILE EXPLORER
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val source_uri = result.data!!.data!!
                try {
                    photo_file = createPhotoFile()!!
                } catch (e: Exception) {
                    message("Error al crear el archivo de la foto")
                }
                val end_uri = Uri.fromFile(photo_file)
                crop_photo_result.launch(Pair(source_uri, end_uri) as Pair<Uri, Uri>?)
            }
        }

    private val crop_photo_result = registerForActivityResult(CropImageUtil()) { uri ->
        showPhoto(uri)
    }

    private fun showPhoto(uri: Uri?) {
        if (uri != null) {
            photo_uri = uri
            val bitmap = BitmapFactory.decodeFile(uri.path)
            val round_bitmap = RoundedBitmapDrawableFactory.create(resources, bitmap)
            round_bitmap.cornerRadius = 30f
            binding.imgPhoto.setImageResource(0)
            binding.imgPhoto.background = round_bitmap
        }
    }
}