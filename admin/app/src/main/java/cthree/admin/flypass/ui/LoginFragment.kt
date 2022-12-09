package cthree.admin.flypass.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import cthree.admin.flypass.R
import cthree.admin.flypass.databinding.DialogOneButtonAlertBinding
import cthree.admin.flypass.databinding.DialogProgressBarBinding
import cthree.admin.flypass.databinding.FragmentLoginBinding
import cthree.admin.flypass.models.admin.AdminDataClass
import cthree.admin.flypass.utils.SessionManager
import cthree.admin.flypass.utils.Utils
import cthree.admin.flypass.viewmodels.AdminViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    lateinit var binding : FragmentLoginBinding
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
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initProgressDialog()

        adminVM.getLoginToken().observe(viewLifecycleOwner) {
            if(it != null){
                Log.d("string token", "onViewCreated: $it")
                progressAlertDialog.dismiss()
                sessionManager.setToken(it)
//                adminVM.saveData(Utils.decodeAccountToken(it))
                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
            }
        }

        adminVM.getLoginErrorMessage().observe(viewLifecycleOwner){
            if(it != null){
                progressAlertDialog.dismiss()
                if(it.contains("Email")){
                    errorMessageDialog(
                        resources.getString(R.string.wrong_email_title),
                        resources.getString(R.string.wrong_email_subtitle),
                        resources.getString(R.string.confirm_one_btn_dialog)
                    )
                }else{
                    errorMessageDialog(
                        resources.getString(R.string.wrong_password_title),
                        resources.getString(R.string.wrong_password_subtitle),
                        resources.getString(R.string.confirm_one_btn_dialog)
                    )
                }
            }
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.loginEmail.text.toString()
            val password = binding.loginPassword.text.toString()

            if(email.isEmpty() || password.isEmpty()) {
                binding.loginEmail.error = "Field Masih Kosong"
                binding.loginPassword.error = "Field Masih Kosong"
            }else{
                progressAlertDialog.show()
                adminVM.loginAdmin(AdminDataClass(email, password))
                adminVM.saveLoginStatus(true)
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