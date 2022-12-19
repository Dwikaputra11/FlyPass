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
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
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
import java.lang.Boolean
import java.util.*
import kotlin.String
import kotlin.getValue
import kotlin.run
import kotlin.toString

private const val TAG = "LoginFragment"
private const val REQ_ONE_TAP = 100
@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val userVM: UserViewModel by viewModels()
    private val prefVM: PreferencesViewModel by viewModels()
    private lateinit var sessionManager: SessionManager
    private lateinit var progressAlertDialogBuilder: MaterialAlertDialogBuilder
    private lateinit var progressAlertDialog: AlertDialog
    private lateinit var fromDestination : TokenNav
    private lateinit var oneTapClient: SignInClient
    private lateinit var verifier: GoogleIdTokenVerifier
    private lateinit var signInRequest: BeginSignInRequest

    private val resolutionForResult = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { activityResult ->
        run {
            if(activityResult != null){
                val credential = oneTapClient.getSignInCredentialFromIntent(activityResult.data)
                val idToken = credential.googleIdToken
                val username = credential.givenName
                val password = credential.password
                Log.d(TAG, "Got ID token -> $idToken")
                Log.d(TAG, "Got password -> $password")
                Log.d(TAG, "Got json -> $username")
                verifier = GoogleIdTokenVerifier.Builder(NetHttpTransport(), GsonFactory())
                    .setAudience(Collections.singletonList(requireContext().getString(R.string.mobile_client_id)))
                    .setIssuer("accounts.google.com")
                    .build()
//                val userToken = JWT(idToken!!)
                val userToken = verifier.verify(credential.googleIdToken)
//                Log.d(TAG, "userToken: ${userToken.claims}")
                Log.d(TAG, "userToken: $userToken")
                if (userToken != null) {
                    val payload: Payload = userToken.payload

                    // Print user identifier
                    val userId = payload.subject
                    println("User ID: $userId")

                    // Get profile information from payload
                    val email = payload.email
                    val emailVerified = Boolean.valueOf(payload.emailVerified)
                    val name = payload["name"] as String?
                    val pictureUrl = payload["picture"] as String?
                    val locale = payload["locale"] as String?
                    val familyName = payload["family_name"] as String?
                    val givenName = payload["given_name"] as String?

                    // Use or store profile information
                    // ...
                } else {
                    println("Invalid ID token.")
                }
                when {
                    idToken != null -> {
                        // Got an ID token from Google. Use it to authenticate
                        // with your backend.
                        Log.d(TAG, "Got ID token -> $idToken")
                    }
                    password != null -> {
                        // Got a saved username and password. Use them to authenticate
                        // with your backend.
                        Log.d(TAG, "Got password -> $password")
                    }
                    username != null ->{
                        // Got a saved username and password. Use them to authenticate
                        // with your backend.
                        Log.d(TAG, "Got password -> $username")
                    }
                    else -> {
                        // Shouldn't happen.
                        Log.d(TAG, "No ID token or password!")
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        oneTapClient = Identity.getSignInClient(requireContext())
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
        configSignInGoogle()

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

        binding.btnGoogle.setOnClickListener {
            oneTapClient.beginSignIn(signInRequest)
                .addOnCompleteListener(requireActivity()){ result ->
                    try {
                        Log.d(TAG, "Login Success: ${result.result.pendingIntent}")
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

        binding.tvtoRegister.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.action_loginFragment_to_registerFragment)
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