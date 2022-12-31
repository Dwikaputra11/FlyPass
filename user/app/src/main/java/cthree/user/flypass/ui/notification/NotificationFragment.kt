package cthree.user.flypass.ui.notification

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
import cthree.user.flypass.adapter.NotificationAdapter
import cthree.user.flypass.data.GoogleTokenRequest
import cthree.user.flypass.databinding.FragmentNotificationBinding
import cthree.user.flypass.models.login.LoginData
import cthree.user.flypass.models.notification.Notification
import cthree.user.flypass.ui.dialog.DialogCaller
import cthree.user.flypass.utils.AlertButton
import cthree.user.flypass.utils.Utils
import cthree.user.flypass.viewmodels.NotificationViewModel
import cthree.user.flypass.viewmodels.PreferencesViewModel
import cthree.user.flypass.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.net.Socket

private const val TAG = "NotificationFragment"

@AndroidEntryPoint
class NotificationFragment : Fragment() {

    private lateinit var socket: Socket
    private val notifyVM: NotificationViewModel by viewModels()
    private val prefVM: PreferencesViewModel by viewModels()
    private val userVM: UserViewModel by viewModels()
    private lateinit var progressAlertDialogBuilder : MaterialAlertDialogBuilder
    private lateinit var progressAlertDialog        : AlertDialog
    private lateinit var binding: FragmentNotificationBinding
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var userToken: String
    private lateinit var notification: Notification

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
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotificationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupToolbar()
        setBottomNav()
        configSignInGoogle()
        progressAlertDialog.show()
        userVM.loginToken().observe(viewLifecycleOwner){
            if(it != null){
                progressAlertDialog.dismiss()
                // save data profile to proto
                val profile = Utils.decodeAccountToken(it)
                userToken = it

                prefVM.saveToken(it)
                prefVM.saveData(profile)
            }
        }

        prefVM.dataUser.observe(viewLifecycleOwner){
            if(it.token.isNotEmpty()){
                if(Utils.isTokenExpired(it.token)){
                    showSessionExpiredDialog()
                }else{
                    userToken = it.token
                    progressAlertDialog.show()
                    notifyVM.callNotification(it.token)
                }
            }
        }

        notifyVM.getUpdateNotify().observe(viewLifecycleOwner){
            if(it != null){
                val directions = NotificationFragmentDirections.actionNotificationFragmentToNotificationDetailFragment(notification)
                findNavController().navigate(directions)
                progressAlertDialog.dismiss()
            }
        }

        val adapter = NotificationAdapter()
        binding.rvNotification.adapter = adapter
        binding.rvNotification.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        notifyVM.getNotification().observe(viewLifecycleOwner){
            if(it != null){
                progressAlertDialog.dismiss()
                adapter.submitList(it.notification)
            }
        }
        adapter.setOnItemClickListener(object : NotificationAdapter.OnItemClickListener{
            override fun onItemClick(notify: Notification) {
                notification = notify
                notifyVM.updateNotification(userToken, notify.id)
                progressAlertDialog.show()
                Log.d(TAG, "onItemClick: $notify")
            }

        })
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

    private fun setupToolbar(){
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbarLayout.toolbar)
        val supportActionBar = (requireActivity() as AppCompatActivity).supportActionBar
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbar.title = "Notification"
        binding.toolbarLayout.toolbar.setNavigationIcon(R.drawable.ic_round_arrow_back_ios_24)
        binding.toolbarLayout.toolbar.setNavigationOnClickListener {
            Navigation.findNavController(binding.root).popBackStack()
        }
    }
    private fun setBottomNav(){
        val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav?.isVisible = false
    }


}