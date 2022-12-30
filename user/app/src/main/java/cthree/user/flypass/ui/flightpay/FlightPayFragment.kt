package cthree.user.flypass.ui.flightpay

import android.content.DialogInterface
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import cthree.user.flypass.R
import cthree.user.flypass.adapter.FlightPayTransactionAdapter
import cthree.user.flypass.data.GoogleTokenRequest
import cthree.user.flypass.databinding.FragmentFlightPayBinding
import cthree.user.flypass.models.login.LoginData
import cthree.user.flypass.ui.dialog.DialogCaller
import cthree.user.flypass.utils.AlertButton
import cthree.user.flypass.utils.InputPinFrom
import cthree.user.flypass.utils.SessionManager
import cthree.user.flypass.utils.Utils
import cthree.user.flypass.viewmodels.FlightPayViewModel
import cthree.user.flypass.viewmodels.PreferencesViewModel
import cthree.user.flypass.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "FlightPayFragment"

@AndroidEntryPoint
class FlightPayFragment : Fragment() {

    private lateinit var binding:FragmentFlightPayBinding
    private val flightPayVM: FlightPayViewModel by viewModels()
    private val prefVM: PreferencesViewModel by viewModels()
    private val userVM: UserViewModel by viewModels()
    private lateinit var progressAlertDialogBuilder : MaterialAlertDialogBuilder
    private lateinit var progressAlertDialog        : AlertDialog
    private lateinit var userToken: String
    private lateinit var sessionManager             : SessionManager
    private val adapter = FlightPayTransactionAdapter()
    private val pinInputFragment = PinInputFragment(InputPinFrom.FLIGHTPAY)
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
        binding = FragmentFlightPayBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated: Started")
        setupToolbar()
        setBottomNav()

        binding.swipeRefresh.setColorSchemeColors(ContextCompat.getColor(requireContext(),R.color.color_primary))

        userVM.loginToken().observe(viewLifecycleOwner){
            if(it != null){
                progressAlertDialog.dismiss()
                sessionManager.setToken(it)
                // save data profile to proto
                val profile = Utils.decodeAccountToken(it)
                userToken = it

                prefVM.saveToken(it)
                prefVM.saveData(profile)
                sessionManager.setUserId(profile.id)
                flightPayVM.userWallet(it)
                flightPayVM.walletHistory(it)
            }
        }
        binding.swipeRefresh.setOnRefreshListener {
            adapter.submitList(mutableListOf())
            flightPayVM.userWallet(userToken)
            flightPayVM.walletHistory(userToken)
        }
        prefVM.dataUser.observe(viewLifecycleOwner){
            if(it.token.isNotEmpty()){
                userToken = it.token
                if(Utils.isTokenExpired(it.token)){
                    showSessionExpiredDialog()
                }else{
                    flightPayVM.userWallet(it.token)
                    flightPayVM.walletHistory(it.token)
                }
            }
        }


        flightPayVM.getRequestCode().observe(viewLifecycleOwner){
            if(it != null){
                if(it == 401){ // wallet not active
                    binding.swipeRefresh.isRefreshing = false
                    DialogCaller(requireActivity())
                        .setTitle(R.string.wallet_activation_title)
                        .setMessage(R.string.wallet_activation_msg)
                        .setPrimaryButton(R.string.wallet_activation_btn){ dialog, _ ->
                            run{
                                dialog.dismiss()
                                pinInputFragment.show(requireActivity().supportFragmentManager.beginTransaction(), pinInputFragment.tag)
                            }
                        }
                        .create(layoutInflater, AlertButton.ONE)
                        .show()
                }
            }
        }

        flightPayVM.getUserWallet().observe(viewLifecycleOwner){
            if(it != null){
                binding.txtNominal.text = Utils.formattedMoney(it.wallet.balance)
            }
        }
        binding.rvLatestTransaction.adapter = adapter
        binding.rvLatestTransaction.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        flightPayVM.walletHistory().observe(viewLifecycleOwner){
            if(it != null){
                binding.swipeRefresh.isRefreshing = false
                adapter.submitList(it.history)
            }
        }
        binding.mcvTopup.setOnClickListener {
            findNavController().navigate(R.id.action_flightPayFragment_to_topUpFragment)
        }
    }

    private fun showSessionExpiredDialog(){
        DialogCaller(requireActivity())
            .setTitle(R.string.token_expired_title)
            .setMessage(R.string.token_expired_subtitle)
            .setPrimaryButton(R.string.token_expired_login){dialog, _ ->
                run{
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

    private fun setBottomNav(){
        val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav?.isVisible = false
    }

    private fun setupToolbar(){
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbarLayout.toolbar)
        val supportActionBar = (requireActivity() as AppCompatActivity).supportActionBar
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbar.title = "FlightPay"
        binding.toolbarLayout.toolbar.setNavigationIcon(R.drawable.ic_round_arrow_back_ios_24)
        binding.toolbarLayout.toolbar.setNavigationOnClickListener {
            Navigation.findNavController(binding.root).popBackStack()
        }
    }
}