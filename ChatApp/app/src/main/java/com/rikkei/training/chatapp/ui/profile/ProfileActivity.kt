package com.rikkei.training.chatapp.ui.profile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.rikkei.training.chatapp.MainActivity
import com.rikkei.training.chatapp.R
import com.rikkei.training.chatapp.REQUEST_PERMISSION_CODE
import com.rikkei.training.chatapp.TAG
import com.rikkei.training.chatapp.databinding.ActivityProfileBinding


class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private var mUri: Uri? = null
    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if(result.resultCode == RESULT_OK){
            val intent = result.data ?: return@registerForActivityResult
            val uri = intent.data
            mUri = uri
            uri?.let {
                val bitmap = when {
                    Build.VERSION.SDK_INT < 28 -> MediaStore.Images.Media.getBitmap(
                        contentResolver,
                        uri
                    )
                    else -> {
                        val source = ImageDecoder.createSource(contentResolver, uri)
                        ImageDecoder.decodeBitmap(source)
                    }
                }
                Log.d(TAG, "$bitmap")
                binding.userAvt.setImageBitmap(bitmap)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUserInformation()

        binding.userAvt.setOnClickListener {
            onClickUserAvatar()
        }

        binding.btnUpdate.setOnClickListener {
            updateProfile()
        }
    }

    private fun setUserInformation() {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val name = user.displayName
        val email = user.email ?: ""
        val photoUrl = user.photoUrl

        binding.edtEmail.setText(email)
        binding.edtUsername.setText(name)
        Glide.with(this).load(photoUrl).error(R.drawable.no_image).into(binding.userAvt)

    }

    private fun onClickUserAvatar(){
        onClickRequestPermission()
    }

    private fun onClickRequestPermission() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            openGallery()
            return
        }
        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            openGallery()
        } else {
            val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            requestPermissions(permissions, REQUEST_PERMISSION_CODE)

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                openGallery()
            } else {
                Toast.makeText(this, "Allow permission read external storage to access gallery!", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        activityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"))
    }

    private fun updateProfile() {
        val user = FirebaseAuth.getInstance().currentUser ?: return

        val name = binding.edtEmail.text.toString().trim()

        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(name)
            .setPhotoUri(mUri)
            .build()

        user.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User profile updated.")
                }
            }

    }

}