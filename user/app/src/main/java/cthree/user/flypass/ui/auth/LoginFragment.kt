package cthree.user.flypass.ui.auth

import android.app.Activity
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import cthree.user.flypass.R
import cthree.user.flypass.data.GoogleTokenRequest
import cthree.user.flypass.databinding.DialogProgressBarBinding
import cthree.user.flypass.databinding.FragmentLoginBinding
import cthree.user.flypass.models.login.LoginData
import cthree.user.flypass.ui.dialog.DialogCaller
import cthree.user.flypass.utils.AlertButton
import cthree.user.flypass.utils.SessionManager
import cthree.user.flypass.utils.RegisterOption
import cthree.user.flypass.utils.Utils
import cthree.user.flypass.viewmodels.PreferencesViewModel
import cthree.user.flypass.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.apache.http.client.methods.HttpPost
import java.util.*
import kotlin.getValue
import kotlin.run
import kotlin.toString


private const val TAG = "LoginFragment"
private const val REQ_ONE_TAP = 100
private const val REQ_SIGN_IN = 200
@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val userVM: UserViewModel by viewModels()
    private val prefVM: PreferencesViewModel by viewModels()
    private lateinit var sessionManager: SessionManager
    private lateinit var progressAlertDialogBuilder: MaterialAlertDialogBuilder
    private lateinit var progressAlertDialog: AlertDialog
    private lateinit var fromDestination : RegisterOption
    private lateinit var oneTapClient: SignInClient
    private lateinit var verifier: GoogleIdTokenVerifier
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var signInClient: GoogleSignInClient

    private val resolutionForResult = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { activityResult ->
        run {
            if(activityResult != null){
                Log.d(TAG, "Activity Result: ${activityResult.data.toString()}")
                val credential = oneTapClient.getSignInCredentialFromIntent(activityResult.data)
                Log.d(TAG, "Credential: ${credential.id}")
                val email = credential.id
                val idToken = credential.googleIdToken
                val username = credential.givenName
                val password = credential.password
                val httpsPost = HttpPost("")
                Log.d(TAG, "Got Email: $email")
                Log.d(TAG, "Got ID token -> $idToken")
                Log.d(TAG, "Got password -> $password")
                Log.d(TAG, "Got json -> $username")

                if(idToken != null) userVM.callGoogleIdTokenLogin(GoogleTokenRequest(idToken))
//                verifier = GoogleIdTokenVerifier.Builder(NetHttpTransport(), GsonFactory())
//                    .setAudience(Collections.singletonList(requireContext().getString(R.string.mobile_client_id)))
//                    .setIssuer("accounts.google.com")
//                    .build()
////                val userToken = JWT(idToken!!)
//                val userToken = verifier.verify(credential.googleIdToken)
////                Log.d(TAG, "userToken: ${userToken.payload}")
//                Log.d(TAG, "userToken: $userToken")
//                if (userToken != null) {
//                    val payload: Payload = userToken.payload
//                    // Print user identifier
//                    val userId = payload.subject
//                    println("User ID: $userId")
//
//                    // Get profile information from payload
//                    val email = payload.email
////                    val emailVerified = Boolean.valueOf(payload.emailVerified)
//                    val name = payload["name"] as String?
//                    val pictureUrl = payload["picture"] as String?
//                    val locale = payload["locale"] as String?
//                    val familyName = payload["family_name"] as String?
//                    val givenName = payload["given_name"] as String?
//
//                    // Use or store profile information
//                    // ...
//                } else {
//                    println("Invalid ID token.")
//                }
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

    private val signInResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){result -> run{
        if(result.resultCode == Activity.RESULT_OK){
            val data = result.data
            Log.d(TAG, "SignIn: $data")
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
                val authCode = account.serverAuthCode

                // Show signed-un UI
                updateUI(account)

                // TODO(developer): send code to server and exchange for access/refresh/ID tokens
            } catch (e: ApiException) {
                Log.w(TAG, "Sign-in failed", e)
                updateUI(null)
            }
        }
    }}

    private fun updateUI(account: GoogleSignInAccount?) {
        if(account != null){
            Log.d(TAG, "updateUI serverAuthCode: ${account.serverAuthCode}")
            Log.d(TAG, "updateUI idToken: ${account.idToken}")
            val inputStream = requireContext().resources.assets.open("client_secret_web.json")
            Log.d(TAG, "inputStream: $inputStream")
            Log.d(TAG, "available input Stream: ${inputStream.available()}")
//            Log.d(TAG, "file: $file")
//            Log.d(TAG, "available input Stream: ${file.path}")
//            val clientSecrets = GoogleClientSecrets.load(
//                GsonFactory.getDefaultInstance(), FileReader()
//            )
//            val tokenResponse = GoogleAuthorizationCodeTokenRequest(
//                NetHttpTransport(),
//                GsonFactory.getDefaultInstance(),
//                "https://oauth2.googleapis.com/token",
//                clientSecrets.details.clientId,
//                clientSecrets.details.clientSecret,
//                account.serverAuthCode,
//                ""
//            ).execute()
//            Log.d(TAG, "access Token: $tokenResponse")
//            val accessToken  = tokenResponse.accessToken
//
//            Log.d(TAG, "access Token: $accessToken")
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
        configSignInGoogle()


        userVM.loginToken().observe(viewLifecycleOwner) {
            if(it != null){
                Log.d(TAG, "Access Token: $it")
                progressAlertDialog.dismiss()
                sessionManager.setToken(it)
                // save data profile to proto
                val profile = Utils.decodeAccountToken(it)
                prefVM.clearToken()
                prefVM.clearRefreshToken()
                prefVM.clearProfileData()
                prefVM.saveToken(it)
                Log.d(TAG, "Profile Not Null: ${profile.toString()}")
                sessionManager.setUserId(profile.id)
                prefVM.saveData(profile)
                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
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
//            signIn()
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


    private fun signIn(){
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Scope(Scopes.DRIVE_APPFOLDER))
            .requestServerAuthCode(resources.getString(R.string.web_client_id))
            .requestEmail()
            .build()
        signInClient = GoogleSignIn.getClient(requireContext(), gso)
        val singInIntent = signInClient.signInIntent
        signInResult.launch(singInIntent)
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


//    private fun navigateConfig(){
//        when(fromDestination){
//            TokenNav.BOOKING -> findNavController().navigate(R.id.action_loginFragment_to_bookingFragment)
//            TokenNav.TOP_UP -> findNavController().navigate(R.id.action_loginFragment_to_topUpFragment)
//            TokenNav.MY_BOOKING -> findNavController().navigate(R.id.action_loginFragment_to_myBookingFragment)
//            TokenNav.PAYMENT -> findNavController().navigate(R.id.action_loginFragment_to_paymentFragment)
//            TokenNav.FLIGHT_PAY -> findNavController().navigate(R.id.action_loginFragment_to_flightPayFragment)
//            TokenNav.PROFILE_INFO -> findNavController().navigate(R.id.action_loginFragment_to_profileAccountInfoFragment)
//            TokenNav.WISHLIST -> findNavController().navigate(R.id.action_loginFragment_to_wishlistFragment)
//            else -> findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
//        }
//    }

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