package com.android.hoang.chatapplication.ui.main.profile.editprofile

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.android.hoang.chatapplication.R
import com.android.hoang.chatapplication.base.BaseFragment
import com.android.hoang.chatapplication.data.remote.model.UserFirebase
import com.android.hoang.chatapplication.databinding.FragmentEditProfileBinding
import com.android.hoang.chatapplication.ui.main.MainActivity
import com.android.hoang.chatapplication.util.Constants.LOG_TAG
import com.android.hoang.chatapplication.util.Status
import com.android.hoang.chatapplication.util.showMessage
import com.blankj.utilcode.util.StringUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class EditProfileFragment : BaseFragment<FragmentEditProfileBinding>() {

    private val editProfileViewModel: EditProfileFragmentViewModel by viewModels()

    private var selectedDate: Calendar = Calendar.getInstance()
    private var filePath: Uri? = null

    private val args: EditProfileFragmentArgs by navArgs()

    private val permissionResultLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ){ result ->
        var allAreGranted = true
        for(b in result.values) {
            allAreGranted = allAreGranted && b
        }
        if(allAreGranted) {
            openDialogChooseCameraOrGallery()
        } else {
            Toast.makeText(requireContext(), R.string.permission_not_granted, Toast.LENGTH_LONG).show()
        }
    }


    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){ result ->
        val data = result.data
//        Log.d(LOG_TAG, "imageUri: ${data?.data}")
//        Log.d(LOG_TAG, "filePath: $filePath")

        if (getPickImageResultUri(data) != null){
            val uri = getPickImageResultUri(data)
            Log.d(LOG_TAG, "ImageUri: $uri")
            Glide.with(requireContext()).load(uri).error(R.drawable.no_image).apply(RequestOptions.circleCropTransform()).into(binding.userAvt)
        } else {
            Log.d(LOG_TAG, "ImageUri: null")
        }
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentEditProfileBinding.inflate(inflater, container, false)

    override fun prepareView(savedInstanceState: Bundle?) {
        Log.d(LOG_TAG, "prepareView filePath: $filePath")
        Log.d(LOG_TAG, "editProfileFragment.prepareView: ${args.currentUser.id}")
        loadInfoCurrentUser(args.currentUser)
        checkObserverModel()

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        // avatar
        binding.userAvt.setOnClickListener {
            onClickRequestPermission()
        }
        binding.btnCamera.setOnClickListener {
            onClickRequestPermission()
        }

        binding.edtBirthday.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus){
                showDatePickerDialog()
            }
        }

        binding.txtSave.setOnClickListener {
            openDialogAcceptUpdate()
        }

    }

    private fun openDialogAcceptUpdate() {
        requireContext().showMessage(R.string.question_edit_profile){
            onClickUpdateUser()
        }
    }

    private fun checkObserverModel(){
        binding.editProfileViewModel = editProfileViewModel
        binding.lifecycleOwner = this
        editProfileViewModel.name.observe(viewLifecycleOwner){
            editProfileViewModel.updateCheckResultState()
        }
        editProfileViewModel.dob.observe(viewLifecycleOwner){
            editProfileViewModel.updateCheckResultState()
        }
    }

    private fun onClickUpdateUser(){
        val name = binding.edtFullName.text.toString().trim()
        val phoneNumber = binding.edtPhoneNumber.text.toString()
        val dob = binding.edtBirthday.text.toString()

        editProfileViewModel.updateProfile(name, filePath.toString(), phoneNumber, dob)
        editProfileViewModel.result.observe(viewLifecycleOwner){
            when(it.status){
                Status.LOADING -> showLoading()
                Status.SUCCESS -> {
                    it.data?.let { result ->
                        if (filePath == null) result.imageUrl = args.currentUser.imageUrl
                        Log.d(LOG_TAG, "editProfile.onClickUpdateUser: $result}")
                        Toast.makeText(requireContext(), StringUtils.getString(R.string.update_success), Toast.LENGTH_LONG).show()
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        startActivity(intent)
                        (activity as MainActivity).finish()
                    }
                    hideLoading()
                }
                Status.ERROR -> {
                    it.message.let { msg ->
                        Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
                        hideLoading()
                    }
                }
            }
        }
    }

    private fun onClickRequestPermission() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            openDialogChooseCameraOrGallery()
            return
        }
        val appPerms = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )
        permissionResultLauncher.launch(appPerms)
    }

    private fun loadInfoCurrentUser(currentUser: UserFirebase){
        editProfileViewModel.initCurrentUser(currentUser)
        editProfileViewModel.currentUser.observe(viewLifecycleOwner){ user ->
            editProfileViewModel.name.value = user.username
            editProfileViewModel.phoneNumber.value = user.phoneNumber
            editProfileViewModel.dob.value = user.dateOfBirth
        }
        val errorImage = if(currentUser.imageUrl == "") R.drawable.avt_default else R.drawable.no_image
        Glide.with(requireContext()).load(currentUser.imageUrl).error(errorImage).into(binding.userAvt)
    }

    private fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                // Lưu ngày được chọn
                selectedDate.set(year, month, dayOfMonth)

                // Hiển thị ngày trong ô nhập
                updateDateInView()
            },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        )

        // Hiển thị hộp thoại chọn ngày
        datePickerDialog.show()
    }
    private fun updateDateInView() {
        val myFormat = "dd/MM/yyyy" // Định dạng ngày tháng
        val sdf = SimpleDateFormat(myFormat, Locale.US)

        // Hiển thị ngày trong ô nhập
        binding.edtBirthday.setText(sdf.format(selectedDate.time))
    }


    private fun openDialogChooseCameraOrGallery() {
        val openDialog = AlertDialog.Builder(requireContext())
        openDialog.setIcon(R.drawable.ic_image_24)
        openDialog.setTitle(R.string.choose_image_from)
        openDialog.setPositiveButton("Camera"){
                dialog,_->
            openCamera()
            dialog.dismiss()
        }
        openDialog.setNeutralButton("Gallery"){
                dialog,_->
            openGallery()
            dialog.dismiss()
        }
        openDialog.setNegativeButton(R.string.cancel){
                dialog,_->
            dialog.dismiss()
        }

        openDialog.create()
        openDialog.show()
    }

    private fun openGallery() {
        val galIntent = Intent()
        galIntent.action = Intent.ACTION_GET_CONTENT
        galIntent.type = "image/*"
        activityResultLauncher.launch(Intent.createChooser(galIntent, "Select image from gallery"))
    }


    private fun openCamera() {

        val camIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        val photoFile: File? = try {
            val imagePath = (activity as MainActivity).getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            File(imagePath, "JPEG_${System.currentTimeMillis()}.jpg")
        } catch (e: IOException){
            e.printStackTrace()
            null
        }
        photoFile?.also { file ->
            val photoUri: Uri = FileProvider.getUriForFile(
                requireContext(),
                "com.android.hoang.chatapplication.provider",
                file
            )
            filePath = photoUri
            camIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        }
        camIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        activityResultLauncher.launch(camIntent)
    }

    private fun getPickImageResultUri(data: Intent?): Uri? {
        var isCamera = true
        if (data != null) {
            val action: String? = data.action
            isCamera = true && action == MediaStore.ACTION_IMAGE_CAPTURE
        }

        if (!isCamera) filePath = data?.data
        return filePath
    }

}