package cthree.user.flypass.ui.flightpay

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import cthree.user.flypass.R
import cthree.user.flypass.adapter.FlightPayTransactionAdapter
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
    private val preferencesVM: PreferencesViewModel by viewModels()
    private val userVM: UserViewModel by viewModels()
    private lateinit var progressAlertDialogBuilder : MaterialAlertDialogBuilder
    private lateinit var progressAlertDialog        : AlertDialog
    private lateinit var userToken: String
    private lateinit var sessionManager             : SessionManager
    private val adapter = FlightPayTransactionAdapter()
    private val pinInputFragment = PinInputFragment(InputPinFrom.FLIGHTPAY)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

    override fun onResume() {
        Log.d(TAG, "onResume: Started")
        super.onResume()
    }

    override fun onStart() {
        Log.d(TAG, "onStart: Started")
        super.onStart()
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
                preferencesVM.clearToken()
                preferencesVM.clearProfileData()

                preferencesVM.saveToken(it)
                preferencesVM.saveData(profile)
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
        preferencesVM.dataUser.observe(viewLifecycleOwner){
            if(it.token.isNotEmpty()){
                userToken = it.token
                if(Utils.isTokenExpired(it.token)){
                    DialogCaller(requireActivity())
                        .setTitle(R.string.token_expired_title)
                        .setMessage(R.string.token_expired_subtitle)
                        .setPrimaryButton(R.string.token_expired_login){dialog, _ ->
                            run{
                                // handle data
                                preferencesVM.clearToken()
                                preferencesVM.clearRefreshToken()
                                dialog.dismiss()
                                callLoginDialog()
                            }
                        }
                        .create(layoutInflater, AlertButton.ONE)
                        .show()
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