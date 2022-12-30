package cthree.user.flypass.ui.profile

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import cthree.user.flypass.R
import cthree.user.flypass.databinding.DialogProgressBarBinding
import cthree.user.flypass.databinding.FragmentSettingsBinding
import cthree.user.flypass.ui.dialog.DialogCaller
import cthree.user.flypass.utils.AlertButton
import cthree.user.flypass.utils.SessionManager
import cthree.user.flypass.viewmodels.PreferencesViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "SettingsFragment"

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var progressAlertDialog: AlertDialog
    private lateinit var progressAlertDialogBuilder: MaterialAlertDialogBuilder
    private lateinit var sessionManager: SessionManager
    private val prefVM : PreferencesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext())
        sessionManager = SessionManager(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupToolbar()
        setBottomNav()
        initProgressDialog()
        binding.cvAccountInfo.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_profileAccountInfoFragment)
        }
        binding.cvLanguage.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_languageFragment)
        }
        binding.cvTermConditions.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_termsConditionsFragment)
        }
        binding.cvPrivacyPolicy.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_privacyPolicyFragment)
        }

        binding.btnLogout.setOnClickListener {
            DialogCaller(requireActivity())
                .setTitle(R.string.logout_confirm_title)
                .setMessage(R.string.logout_confirm_subtitle)
                .setPrimaryButton(R.string.logout_confirm_btn_yes){ dialog, _ ->
                    run {
                        prefVM.clearProfileData()
                        prefVM.clearRefreshToken()
                        prefVM.clearToken()
                        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)
                        findNavController().popBackStack()
                        bottomNav.selectedItemId = R.id.homeFragment
//                        userViewModel.logoutUser(sessionManager.getToken()!!)
//                        progressAlertDialog.show()
                        dialog.dismiss()
                    }
                }
                .setSecondaryButton(R.string.logout_confirm_btn_no){ dialog, _ ->
                    run {
                        dialog.dismiss()
                    }
                }
                .create(layoutInflater, AlertButton.TWO)
                .show()
        }
    }

    private fun setupToolbar(){
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbarLayout.toolbar)
        val supportActionBar = (requireActivity() as AppCompatActivity).supportActionBar
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarLayout.toolbar.title = "Booking"
        binding.toolbarLayout.toolbar.setNavigationIcon(R.drawable.ic_round_arrow_back_ios_24)
        binding.toolbarLayout.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
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