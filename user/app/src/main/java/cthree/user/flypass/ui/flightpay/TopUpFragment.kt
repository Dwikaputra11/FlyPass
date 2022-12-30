package cthree.user.flypass.ui.flightpay

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.IntentSender
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.RadioGroup.OnCheckedChangeListener
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import cthree.user.flypass.R
import cthree.user.flypass.data.BalanceRequestAmount
import cthree.user.flypass.data.GoogleTokenRequest
import cthree.user.flypass.databinding.DialogProgressBarBinding
import cthree.user.flypass.databinding.FragmentTopUpBinding
import cthree.user.flypass.models.login.LoginData
import cthree.user.flypass.ui.dialog.DialogCaller
import cthree.user.flypass.utils.AlertButton
import cthree.user.flypass.utils.SessionManager
import cthree.user.flypass.utils.Utils
import cthree.user.flypass.viewmodels.FlightPayViewModel
import cthree.user.flypass.viewmodels.PreferencesViewModel
import cthree.user.flypass.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "TopUpFragment"

@AndroidEntryPoint
class TopUpFragment : Fragment() {

    private lateinit var binding: FragmentTopUpBinding
    private val flightPayVM: FlightPayViewModel by viewModels()
    private val preferencesVM: PreferencesViewModel by viewModels()
    private val userVM: UserViewModel by viewModels()
    private lateinit var progressAlertDialogBuilder : MaterialAlertDialogBuilder
    private lateinit var progressAlertDialog        : AlertDialog
    private lateinit var sessionManager             : SessionManager
    private lateinit var userToken: String
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest

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
                Log.d(TAG, "Got Email: $email")
                Log.d(TAG, "Got ID token -> $idToken")
                Log.d(TAG, "Got password -> $password")
                Log.d(TAG, "Got json -> $username")

                if(idToken != null) userVM.callGoogleIdTokenLogin(GoogleTokenRequest(idToken))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        oneTapClient = Identity.getSignInClient(requireContext())
        progressAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext())
        sessionManager = SessionManager(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTopUpBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: Started")
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated: Started")
        setBottomNav()
        setupToolbar()
        configSignInGoogle()
        initProgressDialog()

        userVM.loginToken().observe(viewLifecycleOwner){
            if(it != null){
                progressAlertDialog.dismiss()
                sessionManager.setToken(it)
                // save data profile to proto
                val profile = Utils.decodeAccountToken(it)
                userToken = it
                preferencesVM.saveToken(it)
                preferencesVM.saveData(profile)
                sessionManager.setUserId(profile.id)
            }
        }
        preferencesVM.dataUser.observe(viewLifecycleOwner){
            if(it.token.isNotEmpty()){
                if(Utils.isTokenExpired(it.token)){
                    showSessionExpiredDialog()
                }else{
                    userToken = it.token
                }
            }
        }

        flightPayVM.getTopUpRequest().observe(viewLifecycleOwner){
            if(it != null){
                progressAlertDialog.dismiss()

                val directions = TopUpFragmentDirections.actionTopUpFragmentToTopUpPaymentFragment(
                    binding.etNominal.text.toString(),
                    it.request.id
                )
                findNavController().navigate(directions)
            }
        }

        binding.etNominal.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
               if(s != null){
                   if(s.toString().isNotEmpty()){
                       binding.tvTopupNominal.text  = "Rp ${Utils.formattedMoney(s.toString().toInt())}"
                       binding.tvTopupTotal.text  = "Rp ${Utils.formattedMoney(s.toString().toInt())}"
                   }else{
                       binding.tvTopupNominal.text  = "Rp 0"
                       binding.tvTopupTotal.text  = "Rp 0"
                   }
               }
            }

            override fun afterTextChanged(s: Editable?) {
                if(s != null){
                    if(s.toString().isNotEmpty()){
                        binding.tvTopupNominal.text  = "Rp ${Utils.formattedMoney(s.toString().toInt())}"
                        binding.tvTopupTotal.text  = "Rp ${Utils.formattedMoney(s.toString().toInt())}"
                    }else{
                        binding.tvTopupNominal.text  = "Rp 0"
                        binding.tvTopupTotal.text  = "Rp 0"
                    }
                }
            }

        })

        var move = false
        var move1 = false

        binding.rgCash1.setOnCheckedChangeListener { radioButton, checkedId ->
            run{
                Log.d(TAG, "rgCash1: changes")
                Log.d(TAG, "rgCash1: checkId -> $checkedId")
                Log.d(TAG, "button check in rg 2: ${binding.rgCash2.checkedRadioButtonId}")
//                move1 = binding.rgCash2.checkedRadioButtonId != -1
                if(binding.rgCash2.checkedRadioButtonId != -1 && !move){
                    binding.rgCash2.clearCheck()
                    Log.d(TAG, "rgCash1: clearCheck rg 2")
                }
                if(checkedId != -1){
                    Log.d(TAG, "rgCash2 in rgCash1: ${binding.rgCash2.checkedRadioButtonId}")
                    Log.d(TAG, "onViewCreated rg1: $checkedId")
                    val nominal = view.findViewById<RadioButton>(checkedId).text.toString().substringAfter(" ").replace(".","")
                    binding.etNominal.setText(nominal)
                    binding.tvTopupNominal.text = "Rp $nominal"
                    binding.tvTopupTotal.text = "Rp $nominal"
                }
            }
        }

        binding.rgCash2.setOnCheckedChangeListener { radioButton, checkedId ->
            run{
                Log.d(TAG, "rgCash2: changes")
                Log.d(TAG, "rgCash2: checkId -> $checkedId")
                Log.d(TAG, "button check in rg 1: ${binding.rgCash1.checkedRadioButtonId}")
                move = binding.rgCash1.checkedRadioButtonId != -1
                if(binding.rgCash1.checkedRadioButtonId != -1 && move){
                    binding.rgCash1.clearCheck()
                    Log.d(TAG, "rgCash2: clearCheck rg 1")
                }
                if(checkedId != -1){
                    Log.d(TAG, "rgCash1 in rgCash2: ${binding.rgCash1.checkedRadioButtonId}")
                    Log.d(TAG, "onViewCreated rg2: $checkedId")
                    val nominal = view.findViewById<RadioButton>(checkedId).text.toString().substringAfter(" ").replace(".","")
                    binding.etNominal.setText(nominal)
                    binding.tvTopupNominal.text = "Rp $nominal"
                    binding.tvTopupTotal.text = "Rp $nominal"
                }
            }
        }
        binding.btnPayNow.setOnClickListener {
            flightPayVM.topUpRequest(userToken, BalanceRequestAmount(binding.etNominal.text.toString().toInt()))
            progressAlertDialog.show()
        }
    }

    private fun showSessionExpiredDialog(){
        DialogCaller(requireActivity())
            .setTitle(R.string.token_expired_title)
            .setMessage(R.string.token_expired_subtitle)
            .setPrimaryButton(R.string.token_expired_login){dialog, _ ->
                run{
                    // handle data
                    dialog.dismiss()
                    callLoginDialog()
                }
            }
            .setSecondaryButton(R.string.token_expired_later){dialog, _ ->
                run{
                    // handle without token
                    dialog.dismiss()
                }
            }
            .create(layoutInflater, AlertButton.TOKEN)
            .show()
    }

    private fun callLoginDialog() {
        DialogCaller(requireActivity())
            .setTitle(R.string.login_dialog_title)
            .setLoginButton(R.string.login_dialog_login_btn, object : DialogCaller.OnClickLoginListener{
                override fun onClick(dialog: DialogInterface, email: String?, password: String?) {
                    if(email != null && password != null){
                        userVM.callLoginUser(LoginData(email, password))
                        dialog.dismiss()
                        progressAlertDialog.show()
                    }
                }
            })
            .setGoogleButton(R.string.login_dialog_google_btn, object : DialogCaller.OnClickGoogleListener{
                override fun onClick(dialog: DialogInterface, email: String?, password: String?) {
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
                    dialog.dismiss()
                }
            })
            .create(layoutInflater, AlertButton.LOGIN)
            .show()
    }

    private fun configSignInGoogle() {
        signInRequest = BeginSignInRequest.builder()
            .setPasswordRequestOptions(
                BeginSignInRequest.PasswordRequestOptions.builder()
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

    private fun setupToolbar(){
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbarLayout.toolbar)
        val supportActionBar = (requireActivity() as AppCompatActivity).supportActionBar
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbar.title = "Top Up"
        binding.toolbarLayout.toolbar.setNavigationIcon(R.drawable.ic_round_arrow_back_ios_24)
        binding.toolbarLayout.toolbar.setNavigationOnClickListener {
            Navigation.findNavController(binding.root).popBackStack()
        }
    }
}