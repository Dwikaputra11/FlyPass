package cthree.user.flypass.ui.booking

import android.Manifest
import android.annotation.SuppressLint
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import cthree.user.flypass.R
import cthree.user.flypass.databinding.DialogProgressBarBinding
import cthree.user.flypass.databinding.FragmentTransferBankConfirmBinding
import cthree.user.flypass.utils.ImageConfig
import cthree.user.flypass.utils.SessionManager
import cthree.user.flypass.utils.Utils.makeStatusNotification
import cthree.user.flypass.viewmodels.BookingViewModel
import cthree.user.flypass.viewmodels.PreferencesViewModel
import cthree.user.flypass.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.ByteArrayOutputStream
import java.io.File

private const val REQUEST_IMAGE_CODE_PERMISSION = 100
private const val TAG = "TransferBankConfirmFragment"

@AndroidEntryPoint
class TransferBankConfirmFragment : Fragment() {

    private lateinit var binding: FragmentTransferBankConfirmBinding
    private lateinit var imgUri: Uri
    private lateinit var imgFile: File
    private lateinit var progressAlertDialog        : AlertDialog
    private lateinit var progressAlertDialogBuilder : MaterialAlertDialogBuilder
    private lateinit var downloadUri: Uri
    private var bookingId: Int = -1
    private val prefVM: PreferencesViewModel by viewModels()
    private var userToken: String? = null
    private lateinit var sessionManager: SessionManager
    private val bookingVM: BookingViewModel by viewModels()

    private val cameraResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                handleCameraImage(result.data)
            }
        }

    private val galleryResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { result ->
            if(result != null) imgUri = result
            binding.llPhotoEmpty.isVisible = false
            binding.ivPhoto.setImageURI(imgUri)
            imgFile = ImageConfig.uriToFile(imgUri, requireContext())
            Log.d(TAG, "Gallery result: $imgUri")
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
        binding = FragmentTransferBankConfirmBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupToolbar()
        setArgs()
        initProgressDialog()

        prefVM.dataUser.observe(viewLifecycleOwner){
            if(it.token.isNotEmpty()){
                userToken = it.token
            }
        }


        bookingVM.getPaymentResponse().observe(viewLifecycleOwner){
            if(it  != null){
                makeStatusNotification(getString(R.string.payment_success_with_trf_msg),requireContext())
                progressAlertDialog.dismiss()
                findNavController().navigate(R.id.action_transferBankConfirmFragment_to_bookingCompleteFragment)
            }
        }

        binding.paymentDetails.ivIcon.isVisible = false
        binding.paymentDetails.tvPaymentMethod.text = "Transfer Bank"
        binding.llPhotoFilled.setOnClickListener {
            Log.d(TAG, "onViewCreated: Clicked")
            checkPermission()
        }
        binding.btnConfirm.setOnClickListener {
            postImageFile(imgFile)
            progressAlertDialog.show()
        }
    }

    private fun setArgs(){
        val bundle = arguments ?: return

        val args = TransferBankConfirmFragmentArgs.fromBundle(bundle)
        bookingId = args.bookingId
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
            .create()
            .show()
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
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
        binding.llPhotoEmpty.isVisible = false
        binding.ivPhoto.setImageURI(imgUri)
        Log.d(TAG, "handleCameraImage: $imgUri")
    }

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    private fun postImageFile(imgFile: File?) {
        if(imgFile != null){
            val file = ImageConfig.reduceFileImage(imgFile)
            val currentImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "image",
                file.name,
                currentImageFile
            )
            progressAlertDialog.show()
            bookingVM.callPayment(
                token = userToken,
                bookingId = bookingId,
                image = imageMultipart
            )
        }
    }

    private fun initProgressDialog(){
        val progressBarBinding = DialogProgressBarBinding.inflate(layoutInflater, null, false)
        progressAlertDialogBuilder.setView(progressBarBinding.root)

        progressAlertDialog = progressAlertDialogBuilder.create()
        progressAlertDialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        progressAlertDialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
    }

    private fun setupToolbar(){
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbarLayout.toolbar)
        val supportActionBar = (requireActivity() as AppCompatActivity).supportActionBar
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbar.title = "Payment"
        binding.toolbarLayout.toolbar.setNavigationIcon(R.drawable.ic_round_arrow_back_ios_24)
        binding.toolbarLayout.toolbar.setNavigationOnClickListener {
            Navigation.findNavController(binding.root).popBackStack()
        }
    }

}