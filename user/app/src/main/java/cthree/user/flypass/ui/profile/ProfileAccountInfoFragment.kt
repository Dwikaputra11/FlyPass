package cthree.user.flypass.ui.profile

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.RadioButton
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import cthree.user.flypass.R
import cthree.user.flypass.data.UpdateProfile
import cthree.user.flypass.databinding.DialogOneButtonAlertBinding
import cthree.user.flypass.databinding.DialogProgressBarBinding
import cthree.user.flypass.databinding.DialogTwoButtonAlertBinding
import cthree.user.flypass.databinding.FragmentProfileAccountInfoBinding
import cthree.user.flypass.models.user.Profile
import cthree.user.flypass.utils.SessionManager
import cthree.user.flypass.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "ProfileAccountInfoFragment"

@AndroidEntryPoint
class ProfileAccountInfoFragment : Fragment() {

    private lateinit var binding: FragmentProfileAccountInfoBinding
    private val userViewModel: UserViewModel by viewModels()
    private lateinit var progressAlertDialogBuilder : MaterialAlertDialogBuilder
    private lateinit var progressAlertDialog        : AlertDialog
    private lateinit var confirmAlertBuilder        : MaterialAlertDialogBuilder
    private lateinit var updateStatusDialogBuilder  : MaterialAlertDialogBuilder
    private lateinit var sessionManager: SessionManager
    private var roleId: Int = 0
    private var userId: Int = 0
    private val cal: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager              = SessionManager(requireContext())
        progressAlertDialogBuilder  = MaterialAlertDialogBuilder(requireContext())
        confirmAlertBuilder         = MaterialAlertDialogBuilder(requireContext())
        updateStatusDialogBuilder   = MaterialAlertDialogBuilder(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileAccountInfoBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupToolbar()
        initProgressDialog()
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                // after set we have to update in view
                updateDateInView()
            }
        userViewModel.dataUser.observe(viewLifecycleOwner){
            if(it.email.isNotEmpty()){
                binding.etEmail.setText(it.email)
                binding.etName.setText(it.name)
                binding.etPhone.setText(it.phone)
                userId = it.id
                roleId = it.roleId
                if(it.gender == "Male") binding.rgGender.check(R.id.rbMale) else binding.rgGender.check(R.id.rbFemale)
                binding.etBirthDate.setText(it.birthDate)
            }
            if(it.image.isNotEmpty()){
                Glide.with(binding.root)
                    .load(it.image)
                    .into(binding.ivProfile)
            }
        }
        userViewModel.getUpdateProfile().observe(viewLifecycleOwner){
            if(it != null){
                progressAlertDialog.dismiss()
                val gender = requireActivity().findViewById<RadioButton>(binding.rgGender.checkedRadioButtonId).text
                val profile = Profile(
                    name = binding.etName.text.toString(),
                    phone = binding.etName.text.toString(),
                    email = binding.etEmail.text.toString(),
                    birthDate = binding.etBirthDate.text.toString(),
                    gender = gender.toString(),
                    image = null,
                    id = userId,
                    roleId = roleId,
                )
                userViewModel.saveData(profile)
                // TODO: DIALOG NOT SHOW
                updateStatusDialog()
            }
        }

        binding.etBirthDate.setOnClickListener {
            DatePickerDialog(
                requireActivity(),
                dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.btnSave.setOnClickListener {
            if(isValid()){
                updateProfileDialog()
            }
        }
    }

    private fun isValid(): Boolean {
        return  binding.etBirthDate.text.isNotEmpty()
                && binding.etName.text.isNotEmpty()
                && binding.etPhone.text.isNotEmpty()
                && binding.etEmail.text.isNotEmpty()
    }

    private fun updateDateInView() {
        val myFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding.etBirthDate.setText(sdf.format(cal.time))
    }

    private fun updateStatusDialog(){
        val updateStatusBinding     = DialogOneButtonAlertBinding.inflate(layoutInflater, null, false)
        val materAlertDialog        = updateStatusDialogBuilder.create()

        updateStatusDialogBuilder.setView(updateStatusBinding.root)

        materAlertDialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        materAlertDialog.window?.requestFeature(Window.FEATURE_NO_TITLE)

        updateStatusBinding.tvTitle.text = "Update Berhasil!"
        updateStatusBinding.tvSubtitle.text = "Profile anda telah berhasil di update!"
        updateStatusBinding.btnYes.text = "Oke, Got It"

        materAlertDialog.show()
        updateStatusBinding.btnYes.setOnClickListener {
            Log.d(TAG, "updateStatusBinding: Clicked")
            materAlertDialog.dismiss()
        }
    }

    private fun updateProfileDialog(){
        val alertDialogBinding = DialogTwoButtonAlertBinding.inflate(layoutInflater, null, false)
        confirmAlertBuilder = MaterialAlertDialogBuilder(requireContext())

        confirmAlertBuilder.setView(alertDialogBinding.root)

        val materAlertDialog = confirmAlertBuilder.create()
        materAlertDialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        materAlertDialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        materAlertDialog.show()

        alertDialogBinding.tvTitle.text = "Anda Yakin Ubah Profile?"
        alertDialogBinding.tvSubtitle.text = "Pastikan sesuatu lorem ipsum dolor sit amet."
        alertDialogBinding.btnYes.text = "Ubah Profile"
        alertDialogBinding.tvNo.text = "Batalkan"

        alertDialogBinding.btnYes.setOnClickListener {
            progressAlertDialog.show()
            val gender = requireActivity().findViewById<RadioButton>(binding.rgGender.checkedRadioButtonId).text
            val profile = UpdateProfile(
                name = binding.etName.text.toString(),
                phone = binding.etName.text.toString(),
                email = binding.etEmail.text.toString(),
                birthDate = binding.etBirthDate.text.toString(),
                gender = gender.toString(),
            )
            sessionManager.getToken()?.let { token -> userViewModel.updateProfile(token, profile) }
            materAlertDialog.dismiss()
        }
        alertDialogBinding.tvNo.setOnClickListener {
            Log.d(TAG, "notEnoughBalanceDialog: Maybe Later")
            materAlertDialog.dismiss()
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
        binding.toolbarLayout.toolbar.title = "Booking"
        binding.toolbarLayout.toolbar.setNavigationIcon(R.drawable.ic_round_arrow_back_ios_24)
        binding.toolbarLayout.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }
}