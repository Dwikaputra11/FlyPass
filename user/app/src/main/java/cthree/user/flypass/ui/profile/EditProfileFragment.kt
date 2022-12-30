package cthree.user.flypass.ui.profile

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import cthree.user.flypass.R
import cthree.user.flypass.databinding.DialogProgressBarBinding
import cthree.user.flypass.databinding.DialogTwoButtonAlertBinding
import cthree.user.flypass.databinding.FragmentEditProfileBinding
import cthree.user.flypass.models.user.Profile
import cthree.user.flypass.utils.ImageConfig
import cthree.user.flypass.utils.SessionManager
import cthree.user.flypass.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File

private const val REQUEST_IMAGE_CODE_PERMISSION = 100
private const val TAG = "EditProfileFragment"

@AndroidEntryPoint
class EditProfileFragment : Fragment() {

    private lateinit var binding                    : FragmentEditProfileBinding
    private lateinit var imgUri                     : Uri
    private lateinit var downloadUri                : Uri
    private var imgFile                             : File? = null
    private lateinit var confirmAlertBuilder        : MaterialAlertDialogBuilder
    private lateinit var progressAlertDialog        : AlertDialog
    private lateinit var progressAlertDialogBuilder : MaterialAlertDialogBuilder
    private val userViewModel                       : UserViewModel by viewModels()
    private lateinit var profile                    : Profile
    private lateinit var sessionManager             : SessionManager

    private val cameraResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                handleCameraImage(result.data)
                Log.d(TAG, "Camera result File: ${imgFile?.absoluteFile}")
                Log.d(TAG, "Camera result Uri: $imgUri")
            }
        }

    private val galleryResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { result ->
            if(result != null) {
                imgUri = result
                imgFile = ImageConfig.uriToFile(result, requireContext())
//                binding.ivImageProfile.setImageURI(imgUri)
                postProfile(imgFile)
            }
            Log.d(TAG, "Gallery result Uri: $imgUri")
            Log.d(TAG, "Gallery result File: $imgFile")
//            saveToFirebase()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext())
        sessionManager = SessionManager(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupToolbar()
        initProgressDialog()
        binding.ivImageProfile.setOnClickListener {
            changePicProfile()
        }
        userViewModel.dataUser.observe(viewLifecycleOwner){
            if(it.email.isNotEmpty() && it.gender != null && it.birthDate != null && it.phone != null){
                profile = Profile(
                    name = it.name,
                    roleId = it.roleId,
                    id = it.id,
                    email = it.email,
                    phone = it.phone,
                    gender = it.gender,
                    birthDate = it.birthDate,
                    image = it.image.ifEmpty { null }
                )
                Log.d(TAG, "onViewCreated: $profile")
            }
            if(it.image.isNotEmpty()){
                Glide.with(binding.root)
                    .load(it.image)
                    .circleCrop()
                    .into(binding.ivImageProfile)
            }
        }
        userViewModel.getUpdatePhotoProfile().observe(viewLifecycleOwner){
            if(it != null){
                sessionManager.getToken()?.let { token -> userViewModel.callUserProfile(token) }
            }
        }
        userViewModel.getUserProfile().observe(viewLifecycleOwner){
            if(it != null){
                profile.image = it.profile.image
                Glide.with(binding.root)
                    .load(it.profile.image)
                    .circleCrop()
                    .into(binding.ivImageProfile)
                userViewModel.saveData(profile)
                progressAlertDialog.dismiss()
            }
        }
    }

    private fun setupToolbar(){
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbarLayout.toolbar)
        val supportActionBar = (requireActivity() as AppCompatActivity).supportActionBar
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbar.title = "Booking"
        binding.toolbarLayout.toolbar.setNavigationIcon(R.drawable.ic_round_arrow_back_ios_24)
        binding.toolbarLayout.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun changePicProfile(){
        val alertDialogBinding = DialogTwoButtonAlertBinding.inflate(layoutInflater, null, false)
        confirmAlertBuilder = MaterialAlertDialogBuilder(requireContext())

        confirmAlertBuilder.setView(alertDialogBinding.root)

        val materAlertDialog = confirmAlertBuilder.create()
        materAlertDialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        materAlertDialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        materAlertDialog.show()

        alertDialogBinding.tvTitle.text = "Anda Yakin Ubah Foto Profile?"
        alertDialogBinding.tvSubtitle.text = "Pastikan sesuatu lorem ipsum dolor sit amet."
        alertDialogBinding.btnYes.text = "Ubah Profile"
        alertDialogBinding.tvNo.text = "Nanti Saja"

        alertDialogBinding.btnYes.setOnClickListener {
            checkPermission()
            materAlertDialog.dismiss()
        }
        alertDialogBinding.tvNo.setOnClickListener {
            Log.d(TAG, "notEnoughBalanceDialog: Maybe Later")
            materAlertDialog.dismiss()
        }
    }

    private fun postProfile(imgFile: File?) {
        if(imgFile != null){
            val file = ImageConfig.reduceFileImage(imgFile)
            val currentImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "image",
                file.name,
                currentImageFile
            )
            progressAlertDialog.show()
            sessionManager.getToken()?.let {
                userViewModel.updatePhotoProfile(
                    token = it,
                    name = profile.name.toRequestBody("text/plain".toMediaTypeOrNull()),
                    phone = profile.phone?.toRequestBody("text/plain".toMediaTypeOrNull()),
                    birthDate = profile.birthDate?.toRequestBody("text/plain".toMediaTypeOrNull()),
                    gender = profile.gender?.toRequestBody("text/plain".toMediaTypeOrNull()),
                    email = profile.email.toRequestBody("text/plain".toMediaTypeOrNull()),
                    image = imageMultipart
                )
            }
        }
    }

    private fun initProgressDialog(){
        val progressBarBinding = DialogProgressBarBinding.inflate(layoutInflater, null, false)
        progressAlertDialogBuilder.setView(progressBarBinding.root)

        progressAlertDialog = progressAlertDialogBuilder.create()
        progressAlertDialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        progressAlertDialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
    }

    private fun checkPermission(){
        if (isGranted(
                requireActivity(),
                Manifest.permission.CAMERA,
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                REQUEST_IMAGE_CODE_PERMISSION,
            )
        ) {
            requestImage()
        }
    }

    private fun isGranted(
        activity: Activity,
        permission: String,
        permissions: Array<String>,
        request: Int,
    ): Boolean {
        val permissionCheck = ActivityCompat.checkSelfPermission(activity, permission)
        return if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                showPermissionDeniedDialog()
            } else {
                ActivityCompat.requestPermissions(activity, permissions, request)
            }
            false
        } else {
            true
        }
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Permission Denied")
            .setMessage("Permission is denied, Please allow permissions from App Settings.")
            .setPositiveButton(
                "App Settings"
            ) { _, _ ->
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", requireActivity().packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
            .show()
    }

    private fun requestImage(){
        AlertDialog.Builder(requireContext())
            .setMessage("Pilih Gambar")
            .setPositiveButton("Gallery") { _, _ -> openGallery()  }
            .setNegativeButton("Camera") { _, _ -> openCamera() }
            .show()
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        imgUri = FileProvider.getUriForFile(requireActivity(),"${requireActivity().packageName}.provider",ImageConfig.createFile(requireContext()))
//        Log.d(TAG, "openCamera: $imgUri")
//        imgFile = ImageConfig.uriToFile(imgUri,requireContext())
//        Log.d(TAG, "openCamera: $imgFile")
        cameraResult.launch(cameraIntent)
    }

    private fun openGallery() {
        requireActivity().intent.type = "image/*"
        galleryResult.launch("image/*")
    }

    private fun handleCameraImage(intent: Intent?) {
        val bitmap = intent?.extras?.get("data") as Bitmap
        imgUri = getImageUri(requireContext(), bitmap)
        imgFile = ImageConfig.uriToFile(imgUri,requireContext())
        postProfile(imgFile)
//        saveToFirebase()
        Log.d(TAG, "handleCameraImage Uri: $imgUri")
        Log.d(TAG, "handleCameraImage File: $imgFile")
        Log.d(TAG, "handleCameraImage File: $imgFile")
    }

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }
}