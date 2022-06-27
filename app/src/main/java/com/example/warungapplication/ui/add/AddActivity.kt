package com.example.warungapplication.ui.add

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.warungapplication.MyApplication
import com.example.warungapplication.R
import com.example.warungapplication.ViewModelFactory
import com.example.warungapplication.databinding.ActivityAddBinding
import com.example.warungapplication.databinding.BottoomSheetBinding
import com.example.warungapplication.ui.main.MainActivity
import com.example.warungapplication.utils.Event
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class AddActivity : AppCompatActivity() {

    companion object{
        private val PERMISSION_ID = 1010
    }
    @Inject
    lateinit var factory: ViewModelFactory

    private val viewModel: AddViewModel by viewModels {
        factory
    }

    private val activityScope = CoroutineScope(Dispatchers.Main)
    private var location = ""
    private var imgPayment = ""
    private var _binding: ActivityAddBinding? = null
    private val binding get() = _binding!!
    private lateinit var fusedLocationProviderClient : FusedLocationProviderClient


    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val bundle = result.data?.extras
            val bitmap = bundle?.get("data") as Bitmap
            if (saveImageToExternalStorage(UUID.randomUUID().toString(), bitmap)) {
                binding.imgWarung.setImageBitmap(bitmap)
            }
        } else {
            Toast.makeText(this, "Upload Failed", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as MyApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        _binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.apply {
            title = "Tambah Warung"
            setNavigationIcon(R.drawable.ic_back)
            setNavigationOnClickListener {
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        binding.imgEdit.setOnClickListener {
            setPicture()
        }
        binding.btnSubmit.setOnClickListener {
            binding.progressBar2.visibility = View.GONE
            addWarung()
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        binding.edKoordinat.setOnClickListener {

            binding.progressBar2.visibility = View.VISIBLE
            activityScope.launch {
                delay(3000L)
                binding.progressBar2.visibility = View.GONE
                checkPermission()
            }
        }
    }
    @SuppressLint("SetTextI18n")
    private fun checkPermission() {
        if (ActivityCompat.checkSelfPermission(
                applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_ID
            )
            return
        }
        fusedLocationProviderClient.getCurrentLocation(com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY, object : CancellationToken(){
            override fun onCanceledRequested(p0: OnTokenCanceledListener): CancellationToken {
                return CancellationTokenSource().token
            }

            override fun isCancellationRequested(): Boolean {
                return false
            }

        }).addOnSuccessListener {
            if (it == null){
                Toast.makeText(this, "Cannot get Location",Toast.LENGTH_SHORT).show()
            }
            else{
                binding.edKoordinat.setText("${it.longitude} , ${it.latitude}").toString()
                location = binding.edKoordinat.text.toString()
            }
        }
    }
    private fun moveToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun addWarung() {
        val name = binding.edNameWarung.text.toString()
        val addres = binding.edAddress.text.toString()

        viewModel.isNavigateTo.observe(this) { status ->
            if (status == true) {
                moveToMain()
            }
        }
        viewModel.addWarung(name, addres, location, imgPayment)
        viewModel.snackBarText.observe(this) {
            showSnackBar(it)
        }
    }

    private fun showSnackBar(eventMessage: Event<Int>) {
        val message = eventMessage.getContentIfNotHandled() ?: return
        Snackbar.make(
            binding.constraint,
            getString(message),
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun camera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraLauncher.launch(cameraIntent)

    }



    private fun setPicture() {
        val dialog = Dialog(this)
        val binding = BottoomSheetBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)

        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(false)
        binding.imgCamera.setOnClickListener {
            takePhotoFromCamera()
            dialog.dismiss()
        }
        dialog.show()
    }



    private fun saveImageToExternalStorage(imgName: String, bmp: Bitmap): Boolean {

        val imageCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
        val resolver: ContentResolver = this.contentResolver
        val contentValue = ContentValues()
        contentValue.put(MediaStore.Images.Media.DISPLAY_NAME, "$imgName.jpg")
        contentValue.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        val imageUri: Uri? = resolver.insert(imageCollection, contentValue)
        val img = Uri.parse(imageUri.toString())
        imgPayment = img.toString()
        Log.d("Kamera", imgPayment)


        try {

            val outputStream = resolver.openOutputStream(requireNotNull(imageUri))
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            requireNotNull(outputStream)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false

    }

    private fun takePhotoFromCamera() {

        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    // Here after all the permission are granted launch the CAMERA to capture an image.
                    camera()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?,
                ) {
                }
            }).onSameThread()
            .check()
    }

}