package cthree.admin.flypass.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import cthree.admin.flypass.R
import cthree.admin.flypass.databinding.DialogOneButtonAlertBinding
import cthree.admin.flypass.databinding.DialogProgressBarBinding
import cthree.admin.flypass.databinding.FragmentRegisterAdminBinding
import cthree.admin.flypass.models.admin.AdminDataClass
import cthree.admin.flypass.models.admin.RegisterAdminDataClass
import cthree.admin.flypass.utils.SessionManager
import cthree.admin.flypass.viewmodels.AdminViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterAdminFragment : Fragment() {

    lateinit var binding : FragmentRegisterAdminBinding
    private val adminVM: AdminViewModel by viewModels()
    private lateinit var sessionManager: SessionManager
    private lateinit var progressAlertDialogBuilder: MaterialAlertDialogBuilder
    private lateinit var errorMsgAlertBuilder: MaterialAlertDialogBuilder
    private lateinit var progressAlertDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(requireContext())
        progressAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext())
        errorMsgAlertBuilder = MaterialAlertDialogBuilder(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRegisterAdminBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initProgressDialog()

        val token = sessionManager.getToken()

        adminVM.postRegisterAdmin().observe(viewLifecycleOwner){
            if(it != null){
                progressAlertDialog.dismiss()
                Toast.makeText(requireContext(), "Registration Success", Toast.LENGTH_SHORT).show()
                requireActivity().onBackPressed()
            }
        }

        adminVM.getRegisterErrorMessage().observe(viewLifecycleOwner){
            if(it != null){
                progressAlertDialog.dismiss()
                if(it.contains("email")){
                    errorMessageDialog(
                        resources.getString(R.string.email_exist_title),
                        resources.getString(R.string.email_exist_subtitle),
                        resources.getString(R.string.confirm_one_btn_dialog)
                    )
                }else{
                    errorMessageDialog(
                        resources.getString(R.string.wrong_confirm_password_title),
                        resources.getString(R.string.wrong_confirm_password_subtitle),
                        resources.getString(R.string.confirm_one_btn_dialog)
                    )
                }
            }
        }

        binding.btnRegister.setOnClickListener {
            val name = binding.registerName.text.toString()
            val email = binding.registerEmail.text.toString()
            val password = binding.registerPassword.text.toString()
            val confirmationPassword = binding.registerConfirm.text.toString()
            val birthdate = binding.registerBirthdate.text.toString()

            if(name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmationPassword.isEmpty() || birthdate.isEmpty()) {
                binding.registerName.error = "Field Masih Kosong"
                binding.registerEmail.error = "Field Masih Kosong"
                binding.registerPassword.error = "Field Masih Kosong"
                binding.registerConfirm.error = "Field Masih Kosong"
                binding.registerBirthdate.error = "Field Masih Kosong"
            }else{
                progressAlertDialog.show()
                adminVM.registerAdmin("Bearer ${token!!.trim()}", RegisterAdminDataClass(name, email, password, confirmationPassword, birthdate))
            }
        }
    }

    private fun errorMessageDialog(title: String, subtitle: String, btnMsg: String){
        val errorMessageDialog  = DialogOneButtonAlertBinding.inflate(layoutInflater, null, false)

        errorMsgAlertBuilder.setView(errorMessageDialog.root)

        val materAlertDialog    = errorMsgAlertBuilder.create()
        materAlertDialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        materAlertDialog.window?.requestFeature(Window.FEATURE_NO_TITLE)

        errorMessageDialog.tvTitle.text         = title
        errorMessageDialog.tvSubtitle.text      = subtitle
        errorMessageDialog.btnYes.text          = btnMsg

        materAlertDialog.show()
        errorMessageDialog.btnYes.setOnClickListener {
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

}