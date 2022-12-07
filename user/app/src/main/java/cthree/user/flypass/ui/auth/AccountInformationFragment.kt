package cthree.user.flypass.ui.auth

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.RadioButton
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import cthree.user.flypass.R
import cthree.user.flypass.databinding.DialogOneButtonAlertBinding
import cthree.user.flypass.databinding.DialogProgressBarBinding
import cthree.user.flypass.databinding.FragmentAccountInformationBinding
import cthree.user.flypass.models.user.RegisterUser
import cthree.user.flypass.utils.Utils
import cthree.user.flypass.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "AccountInformationFragment"

@AndroidEntryPoint
class AccountInformationFragment : Fragment() {

    private lateinit var binding                    : FragmentAccountInformationBinding
    private lateinit var email                      : String
    private lateinit var password                   : String
    private lateinit var confPassword               : String
    private val cal                                 : Calendar = Calendar.getInstance()
    private lateinit var progressAlertDialogBuilder : MaterialAlertDialogBuilder
    private lateinit var progressAlertDialog        : AlertDialog
    private lateinit var messageAlertDialogBuilder  : MaterialAlertDialogBuilder
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        progressAlertDialogBuilder          = MaterialAlertDialogBuilder(requireContext())
        messageAlertDialogBuilder           = MaterialAlertDialogBuilder(requireContext())
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountInformationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initProgressDialog()
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                // after set we have to update in view
                updateDateInView()
            }
        userViewModel.registerDataUser().observe(viewLifecycleOwner){
            if(it != null){
                progressAlertDialog.dismiss()
                with(requireContext()){
                    showAlertDialog(
                        getString(R.string.register_success_title),
                        getString(R.string.register_success_subtitle),
                        getString(R.string.register_success_btn),
                        emptyField      = false,
                        goToRegister    = false
                    )
                }
            }
        }
        userViewModel.getRegisterErrorMessage().observe(viewLifecycleOwner){
            if(it != null){
                progressAlertDialog.dismiss()
                if(it.contains("email")){
                    with(requireContext()){
                        showAlertDialog(
                            getString(R.string.email_already_exist_title),
                            getString(R.string.email_already_exist_subtitle),
                            getString(R.string.confirm_one_btn_dialog),
                            emptyField      = false,
                            goToRegister    = true
                        )
                    }
                }
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
        getArgs()
        binding.btnRegister.setOnClickListener {
            if(isValid()){
                val date    = Utils.covertYearMonDay(cal.time)
                val gender  = view.findViewById<RadioButton>(binding.rgGender.checkedRadioButtonId).text.toString()
                val registerUser = RegisterUser(
                    name                    = binding.etName.text.toString(),
                    email                   = email,
                    password                = password,
                    confirmationPassword    = confPassword,
                    phone                   = binding.etPhoneNumber.text.toString(),
                    birthDate               = date,
                    gender                  = gender
                )
                userViewModel.registerUser(registerUser)
                progressAlertDialog.show()
            }else{
                with(requireContext()){
                    showAlertDialog(
                        getString(R.string.empty_field_title),
                        getString(R.string.empty_field_subtitle),
                        getString(R.string.confirm_one_btn_dialog),
                        emptyField      = true,
                        goToRegister    = false
                    )
                }
            }
        }
    }

    private fun showAlertDialog(title: String, subtitle: String, btnMsg: String, emptyField: Boolean, goToRegister: Boolean){
        val alertMessageDialog  = DialogOneButtonAlertBinding.inflate(layoutInflater, null, false)

        messageAlertDialogBuilder.setView(alertMessageDialog.root)

        val materAlertDialog    = messageAlertDialogBuilder.create()
        materAlertDialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        materAlertDialog.window?.requestFeature(Window.FEATURE_NO_TITLE)

        alertMessageDialog.tvTitle.text = title
        alertMessageDialog.tvSubtitle.text = subtitle
        alertMessageDialog.btnYes.text = btnMsg

        materAlertDialog.show()
        alertMessageDialog.btnYes.setOnClickListener {
            Log.d(TAG, "errorMessageDialog: Clicked")
            if(goToRegister){
                findNavController().popBackStack()
            }else if(!emptyField){
                // register success
                findNavController().navigate(R.id.action_accountInformationFragment_to_loginFragment)
            }
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

    private fun updateDateInView() {
        val myFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding.etBirthDate.setText(sdf.format(cal.time))
    }

    private fun isValid(): Boolean {
        return  binding.etBirthDate.text.isNotEmpty()
                && binding.etName.text.isNotEmpty()
                && binding.etPhoneNumber.text.isNotEmpty()
    }

    private fun getArgs() {
        val bundle = arguments
        if(bundle == null){
            Log.e(TAG, "onViewCreated: Args Failed")
            return
        }
        val args        = AccountInformationFragmentArgs.fromBundle(bundle)
        email           = args.email
        password        = args.password
        confPassword    = args.confPassword
    }
}