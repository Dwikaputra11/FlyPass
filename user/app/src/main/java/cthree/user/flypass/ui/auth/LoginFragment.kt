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
import cthree.user.flypass.databinding.DialogProgressBarBinding
import cthree.user.flypass.databinding.FragmentLoginBinding
import cthree.user.flypass.models.login.LoginData
import cthree.user.flypass.ui.dialog.DialogCaller
import cthree.user.flypass.utils.AlertButton
import cthree.user.flypass.utils.SessionManager
import cthree.user.flypass.utils.TokenNav
import cthree.user.flypass.utils.Utils
import cthree.user.flypass.viewmodels.PreferencesViewModel
import cthree.user.flypass.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "LoginFragment"
@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val userVM: UserViewModel by viewModels()
    private val prefVM: PreferencesViewModel by viewModels()
    private lateinit var sessionManager: SessionManager
    private lateinit var progressAlertDialogBuilder: MaterialAlertDialogBuilder
    private lateinit var progressAlertDialog: AlertDialog
    private lateinit var fromDestination : TokenNav

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
        getArgs()

        userVM.loginToken().observe(viewLifecycleOwner) {
            if(it != null){
                Log.d(TAG, "Access Token: $it")
                progressAlertDialog.dismiss()
                sessionManager.setToken(it)
                // save data profile to proto
                val profile = Utils.decodeAccountToken(it)
                sessionManager.setUserId(profile.id)
                prefVM.saveToken(it)
                prefVM.saveData(profile)
                navigateConfig()
            }
        }

        userVM.getRefreshToken().observe(viewLifecycleOwner){
            if(it != null){
                Log.d(TAG, "Refresh Token: $it")
                userVM.saveRefreshToken(it)
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

    private fun getArgs(){
        val bundle = arguments ?: return
        val args = LoginFragmentArgs.fromBundle(bundle)
        fromDestination = args.fromDestination
    }

    private fun navigateConfig(){
        when(fromDestination){
            TokenNav.BOOKING -> findNavController().navigate(R.id.action_loginFragment_to_bookingFragment)
            TokenNav.TOP_UP -> findNavController().navigate(R.id.action_loginFragment_to_topUpFragment)
            TokenNav.MY_BOOKING -> findNavController().navigate(R.id.action_loginFragment_to_myBookingFragment)
            TokenNav.PAYMENT -> findNavController().navigate(R.id.action_loginFragment_to_paymentFragment)
            TokenNav.FLIGHT_PAY -> findNavController().navigate(R.id.action_loginFragment_to_flightPayFragment)
            TokenNav.PROFILE_INFO -> findNavController().navigate(R.id.action_loginFragment_to_profileAccountInfoFragment)
            TokenNav.WISHLIST -> findNavController().navigate(R.id.action_loginFragment_to_wishlistFragment)
            else -> findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
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