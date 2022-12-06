package cthree.user.flypass.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import cthree.user.flypass.R
import cthree.user.flypass.databinding.DialogOneButtonAlertBinding
import cthree.user.flypass.databinding.DialogProgressBarBinding
import cthree.user.flypass.databinding.FragmentLoginBinding
import cthree.user.flypass.models.login.LoginData
import cthree.user.flypass.utils.SessionManager
import cthree.user.flypass.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "LoginFragment"
@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val userVM: UserViewModel by viewModels()
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
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initProgressDialog()

        userVM.getLoginToken().observe(viewLifecycleOwner) {
            if(it != null){
                Log.d(TAG, "onViewCreated: $it")
                progressAlertDialog.dismiss()
                sessionManager.setToken(it)
            }
        }
        
        userVM.getErrorMessage().observe(viewLifecycleOwner){
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
                userVM.callLoginUser(LoginData(email, password))
            }
        }

        binding.tvtoRegister.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun errorMessageDialog(title: String, subtitle: String, btnMsg: String){
        val errorMessageDialog = DialogOneButtonAlertBinding.inflate(layoutInflater, null, false)

        errorMsgAlertBuilder.setView(errorMessageDialog.root)

        val materAlertDialog = errorMsgAlertBuilder.create()
        materAlertDialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        materAlertDialog.window?.requestFeature(Window.FEATURE_NO_TITLE)

        errorMessageDialog.tvTitle.text = title
        errorMessageDialog.tvSubtitle.text = subtitle
        errorMessageDialog.btnYes.text = btnMsg

        materAlertDialog.show()
        errorMessageDialog.btnYes.setOnClickListener {
            Log.d(TAG, "errorMessageDialog: Clicked")
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