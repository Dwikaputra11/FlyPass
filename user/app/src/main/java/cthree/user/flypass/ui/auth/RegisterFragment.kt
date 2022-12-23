package cthree.user.flypass.ui.auth

import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import cthree.user.flypass.R
import cthree.user.flypass.databinding.DialogOneButtonAlertBinding
import cthree.user.flypass.databinding.DialogProgressBarBinding
import cthree.user.flypass.databinding.FragmentRegisterBinding
import cthree.user.flypass.ui.dialog.DialogCaller
import cthree.user.flypass.utils.AlertButton
import cthree.user.flypass.utils.RegisterOption
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "RegisterFragment"

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private lateinit var binding                    : FragmentRegisterBinding
    private lateinit var errorMsgAlertBuilder       : MaterialAlertDialogBuilder
    private var idToken: String? = null
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest

    private val resolutionForResult = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { activityResult ->
        run {
            if (activityResult != null) {
                Log.d(TAG, "Activity Result: ${activityResult.data.toString()}")
                val credential = oneTapClient.getSignInCredentialFromIntent(activityResult.data)
                Log.d(TAG, "Credential: ${credential.id}")
                idToken = credential.googleIdToken
                val direction = RegisterFragmentDirections.actionRegisterFragmentToAccountInformationFragment(
                    email = null,
                    password = null,
                    confPassword = null,
                    idToken = idToken,
                    registerMethod = RegisterOption.GOOGLE
                )
                findNavController().navigate(direction)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        errorMsgAlertBuilder            = MaterialAlertDialogBuilder(requireContext())
        oneTapClient                    = Identity.getSignInClient(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setBottomNav()
        configSignInGoogle()
        binding.btnNext.setOnClickListener {
            if(isValid()){
                val direction = RegisterFragmentDirections.actionRegisterFragmentToAccountInformationFragment(
                    email = binding.registerEmail.text.toString(),
                    password = binding.registerPassword.text.toString(),
                    confPassword = binding.registerPassword.text.toString(),
                    idToken = null,
                    registerMethod = RegisterOption.MANUAL
                )
                findNavController().navigate(direction)
            }
        }
        binding.tvtoLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
        binding.registerGoogle.setOnClickListener {
            oneTapClient.beginSignIn(signInRequest)
                .addOnCompleteListener(requireActivity()){ result ->
                    try {
                        Log.d(TAG, "Register Success: ${result.result.pendingIntent}")
                        val intentSenderRequest = IntentSenderRequest.Builder(result.result.pendingIntent).build()
                        resolutionForResult.launch(intentSenderRequest)
                    }catch (e: IntentSender.SendIntentException) {
                        Log.e(TAG, "Couldn't start One Tap UI: ${e.localizedMessage}")
                    }
                }
                .addOnFailureListener(requireActivity()) { e ->
                    // No saved credentials found. Launch the One Tap sign-up flow, or
                    // do nothing and continue presenting the signed-out UI.
                    Log.d(TAG, e.localizedMessage)
                }
        }
    }

    private fun configSignInGoogle() {
        signInRequest = BeginSignInRequest.builder()
            .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                .setSupported(true)
                .build())
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(requireContext().getString(R.string.web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build())
            .setAutoSelectEnabled(true)
            .build()
    }

    private fun isValid(): Boolean {
        if(binding.registerEmail.text.toString().isEmpty()&&
            binding.registerConfirm.text.toString().isEmpty() &&
            binding.registerPassword.text.toString().isEmpty()
        ){
            DialogCaller(requireActivity())
                .setTitle(R.string.empty_field_title)
                .setMessage(R.string.empty_field_subtitle)
                .setPrimaryButton(R.string.confirm_one_btn_dialog
                ) { dialog, _ ->
                    run {
                        Log.d(TAG, "PrimaryButton: Clicked")
                        dialog.dismiss()
                    }
                }
                .create(layoutInflater, AlertButton.ONE).show()
            return false
        }else if(binding.registerPassword.text.toString() != binding.registerConfirm.text.toString()){
            DialogCaller(requireActivity())
                .setTitle(R.string.conf_password_not_match_title)
                .setMessage(R.string.conf_password_not_match_subtitle)
                .setPrimaryButton(R.string.confirm_one_btn_dialog
                ) { dialog, _ ->
                    run {
                        Log.d(TAG, "PrimaryButton: Clicked")
                        dialog.dismiss()
                    }
                }
                .create(layoutInflater, AlertButton.ONE).show()
            return false
        }
        return  true
    }

    private fun setBottomNav(){
        val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav?.isVisible = false
    }
}