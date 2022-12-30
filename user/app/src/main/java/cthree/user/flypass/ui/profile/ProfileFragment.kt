package cthree.user.flypass.ui.profile

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import cthree.user.flypass.R
import cthree.user.flypass.databinding.DialogProgressBarBinding
import cthree.user.flypass.databinding.FragmentProfileBinding
import cthree.user.flypass.models.login.LoginData
import cthree.user.flypass.ui.dialog.DialogCaller
import cthree.user.flypass.utils.AlertButton
import cthree.user.flypass.utils.Utils
import cthree.user.flypass.viewmodels.PreferencesViewModel
import cthree.user.flypass.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "ProfileFragment"

@AndroidEntryPoint
class ProfileFragment : Fragment(), MenuProvider {

    private lateinit var binding    : FragmentProfileBinding
    private lateinit var progressAlertDialogBuilder : MaterialAlertDialogBuilder
    private lateinit var progressAlertDialog        : AlertDialog
    private val userVM       : UserViewModel by viewModels()
    private val prefVM: PreferencesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        userVM.loginToken().observe(viewLifecycleOwner){
            if(it != null){
                prefVM.saveToken(it)
                progressAlertDialog.dismiss()
            }
        }
        prefVM.dataUser.observe(viewLifecycleOwner){
            binding.unregistProfileLayout.root.isVisible    = it.token.isEmpty()
            binding.userProfileLayout.root.isVisible        = it.token.isNotEmpty()
            if(it.token.isNotEmpty()){
                if(!Utils.isTokenExpired(it.token.toString())){
                    Log.d(TAG, "onViewCreated: ${it.email.isEmpty()}")
                    setUserPrefView()
                }else{
                    DialogCaller(requireActivity())
                        .setTitle(R.string.token_expired_title)
                        .setMessage(R.string.token_expired_subtitle)
                        .setPrimaryButton(R.string.token_expired_login){dialog, _ ->
                            run{
                                // handle data
                                prefVM.clearToken()
                                prefVM.clearRefreshToken()
                                dialog.dismiss()
                                callLoginDialog()
                            }
                        }
                        .setSecondaryButton(R.string.token_expired_later){dialog, _ ->
                            run{
                                dialog.dismiss()
                            }
                        }
                        .create(layoutInflater, AlertButton.TOKEN)
                        .show()
                }
            }
        }
        setupToolbar()
        setViews()
        setBottomNav()
        initProgressDialog()
    }

    private fun setViews() {
        binding.cvCallCenter.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_callCenterFragment)
        }
        binding.cvHowToBook.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_howToBookFragment)
        }
        binding.cvFaq.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_faqFragment)
        }
    }

    private fun setUserPrefView(){
        prefVM.dataUser.observe(viewLifecycleOwner){
            if(it.image.isNotEmpty()){
                Glide.with(binding.root)
                    .load(it.image)
                    .circleCrop()
                    .into(binding.userProfileLayout.ivUserProfile)
            }
        }
        binding.userProfileLayout.cvHistory.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_historyProfileFragment)
        }
        binding.userProfileLayout.cvSeeProfile.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }
        binding.userProfileLayout.cvSettings.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_settingsFragment)
        }
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
                    Log.d(TAG, "onClick Google: $email, $password")
                    dialog.dismiss()
                }
            })
            .create(layoutInflater, AlertButton.LOGIN)
            .show()
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

    private fun initProgressDialog(){
        val progressBarBinding = DialogProgressBarBinding.inflate(layoutInflater, null, false)
        progressAlertDialogBuilder.setView(progressBarBinding.root)

        progressAlertDialog = progressAlertDialogBuilder.create()
        progressAlertDialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        progressAlertDialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
    }

    private fun setBottomNav(){
        val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav?.isVisible = true
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.home_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        Log.d(TAG, "onMenuItemSelected: ")
        return when (menuItem.itemId){
            R.id.notification ->{
                Log.d(TAG, "onMenuItemSelected: Clicked")
//                checkUserMember()
                true
            }
            else -> false
        }
    }
}