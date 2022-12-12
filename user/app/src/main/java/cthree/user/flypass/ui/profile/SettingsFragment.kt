package cthree.user.flypass.ui.profile

import android.os.Bundle
import android.util.Log
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
import cthree.user.flypass.databinding.DialogTwoButtonAlertBinding
import cthree.user.flypass.databinding.FragmentSettingsBinding
import cthree.user.flypass.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "SettingsFragment"

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var confirmAlertBuilder: MaterialAlertDialogBuilder
    private lateinit var progressAlertDialog: AlertDialog
    private lateinit var progressAlertDialogBuilder: MaterialAlertDialogBuilder
    private val userViewModel : UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext())
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
            logoutConfirmation()
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
    private fun logoutConfirmation(){
        val alertDialogBinding = DialogTwoButtonAlertBinding.inflate(layoutInflater, null, false)
        confirmAlertBuilder = MaterialAlertDialogBuilder(requireContext())

        confirmAlertBuilder.setView(alertDialogBinding.root)

        val materAlertDialog = confirmAlertBuilder.create()
        materAlertDialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        materAlertDialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        materAlertDialog.show()

        alertDialogBinding.tvTitle.text = "Anda Yakin Logout?"
        alertDialogBinding.tvSubtitle.text = "Pastikan sesuatu lorem ipsum dolor sit amet."
        alertDialogBinding.btnYes.text = "Logout"
        alertDialogBinding.tvNo.text = "Nanti Saja"

        alertDialogBinding.btnYes.setOnClickListener {
            userViewModel.clearProfileData()
            progressAlertDialog.show()
            materAlertDialog.dismiss()
            userViewModel.dataUser.observe(viewLifecycleOwner){
                if(it.email.isEmpty()){
                    Log.d(TAG, "logoutConfirmation: Observe")
                    progressAlertDialog.dismiss()
                    val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)
                    findNavController().popBackStack()
                    bottomNav.selectedItemId = R.id.homeFragment
                }
            }
        }
        alertDialogBinding.tvNo.setOnClickListener {
            Log.d(TAG, "notEnoughBalanceDialog: Maybe Later")
            materAlertDialog.dismiss()
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