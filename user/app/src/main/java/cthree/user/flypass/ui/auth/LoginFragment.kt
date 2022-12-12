package cthree.user.flypass.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import cthree.user.flypass.R
import cthree.user.flypass.databinding.DialogOneButtonAlertBinding
import cthree.user.flypass.databinding.DialogProgressBarBinding
import cthree.user.flypass.databinding.FragmentLoginBinding
import cthree.user.flypass.models.login.LoginData
import cthree.user.flypass.ui.dialog.DialogCaller
import cthree.user.flypass.utils.AlertButton
import cthree.user.flypass.utils.SessionManager
import cthree.user.flypass.utils.Utils
import cthree.user.flypass.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "LoginFragment"
@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val userVM: UserViewModel by viewModels()
    private lateinit var sessionManager: SessionManager
    private lateinit var progressAlertDialogBuilder: MaterialAlertDialogBuilder
    private lateinit var progressAlertDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(requireContext())
        progressAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext())
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
        setBottomNav()

        userVM.getLoginToken().observe(viewLifecycleOwner) {
            if(it != null){
                Log.d(TAG, "onViewCreated: $it")
                progressAlertDialog.dismiss()
                sessionManager.setToken(it)
                // save data profile to proto
                val profile = Utils.decodeAccountToken(it)
                sessionManager.setUserId(profile.id)
                userVM.saveToken(it)
                userVM.saveData(profile)
                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
            }
        }
        
        userVM.getLoginErrorMessage().observe(viewLifecycleOwner){
            if(it != null){
                progressAlertDialog.dismiss()
                if(it.contains("Email")){
                    DialogCaller(requireActivity())
                        .setTitle(R.string.wrong_email_title)
                        .setMessage(R.string.wrong_email_subtitle)
                        .setPrimaryButton(R.string.confirm_one_btn_dialog
                        ) { dialog, _ ->
                            run {
                                Log.d(TAG, "PrimaryButton: Clicked")
                                dialog.dismiss()
                            }
                        }
                        .create(layoutInflater, AlertButton.ONE).show()
                }else{
                    DialogCaller(requireActivity())
                        .setTitle(R.string.wrong_password_title)
                        .setMessage(R.string.wrong_password_subtitle)
                        .setPrimaryButton(R.string.confirm_one_btn_dialog
                        ) { dialog, _ ->
                            run {
                                Log.d(TAG, "PrimaryButton: Clicked")
                                dialog.dismiss()
                            }
                        }
                        .create(layoutInflater, AlertButton.ONE).show()
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

    private fun initProgressDialog(){
        val progressBarBinding = DialogProgressBarBinding.inflate(layoutInflater, null, false)
        progressAlertDialogBuilder.setView(progressBarBinding.root)

        progressAlertDialog = progressAlertDialogBuilder.create()
        progressAlertDialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        progressAlertDialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
    }
    private fun setBottomNav(){
        val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav?.isVisible = false
    }


}