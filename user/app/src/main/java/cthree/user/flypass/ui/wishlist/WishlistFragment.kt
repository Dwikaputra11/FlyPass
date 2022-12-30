package cthree.user.flypass.ui.wishlist

import android.content.DialogInterface
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import cthree.user.flypass.R
import cthree.user.flypass.adapter.WishlistAdapter
import cthree.user.flypass.data.GoogleTokenRequest
import cthree.user.flypass.databinding.DialogProgressBarBinding
import cthree.user.flypass.databinding.FragmentWishlistBinding
import cthree.user.flypass.models.login.LoginData
import cthree.user.flypass.models.wishlist.get.WishListItem
import cthree.user.flypass.ui.dialog.DialogCaller
import cthree.user.flypass.utils.AlertButton
import cthree.user.flypass.utils.Utils
import cthree.user.flypass.viewmodels.PreferencesViewModel
import cthree.user.flypass.viewmodels.UserViewModel
import cthree.user.flypass.viewmodels.WishlistViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "WishlistFragment"

@AndroidEntryPoint
class WishlistFragment : Fragment(), MenuProvider {

    private lateinit var progressAlertDialogBuilder : MaterialAlertDialogBuilder
    private lateinit var progressAlertDialog        : AlertDialog
    private lateinit var binding: FragmentWishlistBinding
    private val adapter: WishlistAdapter = WishlistAdapter()
    private val wishlistVM: WishlistViewModel by viewModels()
    private val prefVM: PreferencesViewModel by viewModels()
    private val userVM: UserViewModel by viewModels()
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
        progressAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWishlistBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initProgressDialog()
        configSignInGoogle()
        setupToolbar()
        setViews()
        setBottomNav()
        userVM.loginToken().observe(viewLifecycleOwner){
            if(it != null){
                val profile = Utils.decodeAccountToken(it)
                // save incoming data
                prefVM.saveToken(it)
                userToken = it
                prefVM.saveData(profile)
                wishlistVM.getUserWishlist(it)
            }
        }
        prefVM.dataUser.observe(viewLifecycleOwner){
            if(it.token.isNotEmpty()){
                if(!Utils.isTokenExpired(it.token.toString())){
                    userToken = it.token
                    Log.d(TAG, "Adapter count: ${adapter.getList().isEmpty()}")
                    if(adapter.getList().isEmpty()) wishlistVM.getUserWishlist(it.token)
                    progressAlertDialog.show()
                }else{
                    showSessionExpiredDialog()
                }
            }else if(it.token.isEmpty()){
                binding.notFound.root.isVisible  = false
                binding.rvMovie.isVisible = false
                joinMemberDialog()
            }
        }
        wishlistVM.getAllWishlist().observe(viewLifecycleOwner){
            if(it != null){
                adapter.submitList(it.wishlistItem)
                binding.swipeRefresh.isRefreshing = false
                binding.notFound.root.isVisible = false
                binding.rvMovie.isVisible = true
                progressAlertDialog.dismiss()
            }else{
                binding.notFound.root.isVisible = true
                binding.rvMovie.isVisible = false
            }
        }
        binding.swipeRefresh.setOnRefreshListener {
            adapter.submitList(listOf())
            wishlistVM.getUserWishlist(userToken)
        }
    }

    private fun setupToolbar() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbarLayout.toolbar)
        val supportActionBar = (requireActivity() as AppCompatActivity).supportActionBar
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayUseLogoEnabled(true)
        supportActionBar?.setLogo(R.drawable.logo_toolbar)
    }

    private fun setBottomNav(){
        val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav?.isVisible = true
    }

    private fun joinMemberDialog() {
        DialogCaller(requireActivity())
            .setTitle(R.string.member_only_title)
            .setMessage(R.string.member_only_msg)
            .setPrimaryButton(R.string.confirm_one_btn_dialog){dialog, _ ->
                run {
                    dialog.dismiss()
                }
            }
            .create(layoutInflater, AlertButton.ONE)
            .show()
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

    private fun setViews() {
        binding.rvMovie.adapter = adapter
        binding.rvMovie.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        adapter.setOnItemClickListener(object : WishlistAdapter.OnItemClickListener{
            override fun onClick(wishlist: WishListItem) {
                Log.d(TAG, "onClick: $wishlist")
            }
        })
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.home_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        Log.d(TAG, "onMenuItemSelected: ")
        return when (menuItem.itemId){
            R.id.notification ->{
                Log.d(TAG, "onMenuItemSelected: Clicked")
                checkUserMember()
                true
            }
            else -> false
        }
    }
    private fun checkUserMember() {
        prefVM.dataUser.observe(viewLifecycleOwner) {
            if (it.token.isNotEmpty()) {
                findNavController().navigate(R.id.action_wishlistFragment_to_notificationFragment)
            } else {
                findNavController().navigate(R.id.action_wishlistFragment_to_notificationFragment)
                // unregistered user
//                DialogCaller(requireActivity())
            }
        }
    }

}